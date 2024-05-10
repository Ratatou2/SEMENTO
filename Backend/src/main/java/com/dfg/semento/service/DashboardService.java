package com.dfg.semento.service;

import com.dfg.semento.document.LogDocument;
import com.dfg.semento.dto.request.SearchTimeRequest;
import com.dfg.semento.dto.response.OhtJobAnalysisResponse;
import com.dfg.semento.dto.response.OhtJobHourlyResponse;
import com.dfg.semento.repository.DashboardRepository;
import com.dfg.semento.util.CalculateOhtData;
import com.dfg.semento.util.FormattedTime;
import com.dfg.semento.util.GenerateIndexNameArray;
import com.dfg.semento.util.TimeConverter;

import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryFieldBuilders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.ScriptQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.InternalNumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregator;
import org.elasticsearch.search.aggregations.metrics.ScriptedMetric;
import org.elasticsearch.search.aggregations.metrics.ScriptedMetricAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.script.Script;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    private final DashboardRepository dashboardRepository;
    private final RestHighLevelClient client;

    public List<LogDocument> test() {
        return dashboardRepository.findAll();
    }

    /** 기간동안 OHT 작업량 분석(oht대수, 전체 작업량, oht별 작업량 평균)
     */
    public OhtJobAnalysisResponse ohtJobAnalysis(LocalDateTime startTime, LocalDateTime endTime) throws IOException {
        // 운행한 OHT 대수
        long ohtCount = getOhtCount(startTime, endTime);
        System.out.println(ohtCount);

        // 기간동안 전체 OHT 작업량
        long totalWork = getOhtTotalWorkByStartTimeAndEndTime(startTime, endTime);
        System.out.println(totalWork);

        // 기간동안 각 OHT별 작업량 평균
        double averageWork = getOhtAverageWorkByStartTimeAndTime(startTime, endTime);
        System.out.println(averageWork);

        return OhtJobAnalysisResponse.builder()
            .ohtCount(ohtCount)
            .totalWork(totalWork)
            .averageWork(averageWork)
            .build();
    }



    /**
     * 기간동안 운행했던 OHT 대수 계산 함수
    */
    private long getOhtCount(LocalDateTime startTime, LocalDateTime endTime) throws IOException {
        //ES의 질의 생성
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // ==== 쿼리 검색 ====
        // startTime과 endTime 사이에 있는 로그만 검색하도록 설정
        //시간 포맷 변환
        FormattedTime FormattedTime = TimeConverter.convertElasticsearchTime(startTime, endTime);
        RangeQueryBuilder timeFilter = QueryBuilders.rangeQuery("curr_time")
            .gte(FormattedTime.getStartTime())
            .lte(FormattedTime.getEndTime());

        // 질의 추가
        searchSourceBuilder.query(timeFilter);


        // ==== 집계 검색 ====
        CardinalityAggregationBuilder cardinalityAggregation = AggregationBuilders.cardinality("oht_count")
            .field("oht_id");

        // 질의 추가
        searchSourceBuilder.aggregation(cardinalityAggregation);


        // 집계반환만 원하고 Document는 반환X
        searchSourceBuilder.size(0);

        // ==== 질의 ====
        SearchResponse searchResponse = sendElasticsearchQuery(startTime, endTime, searchSourceBuilder);

        // 결과에서 작업량 추출
        Cardinality ohtCount  = searchResponse.getAggregations().get("oht_count");

        return ohtCount.getValue();
    }

    /**
     * 기간동안 전체 작업량 계산 함수
     */
    public long getOhtTotalWorkByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime) throws
        IOException {
        //ES의 질의 생성
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // ==== 쿼리 검색 ====
        // startTime과 endTime 사이에 있는 로그만 검색하도록 설정
        //시간 포맷 변환
        FormattedTime FormattedTime = TimeConverter.convertElasticsearchTime(startTime, endTime);
        RangeQueryBuilder timeFilter = QueryBuilders.rangeQuery("curr_time")
            .gte(FormattedTime.getStartTime())
            .lte(FormattedTime.getEndTime());

        // 작업 중인 로그만 검색하도록 설정
        TermQueryBuilder statusFilter = QueryBuilders.termQuery("status.keyword", "W");

        // Bool Query로  두 filter 적용
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
            .filter(timeFilter)
            .filter(statusFilter);

        // 질의 추가
        searchSourceBuilder.query(boolQueryBuilder);


        // ==== 집계 검색 ====
        ScriptedMetricAggregationBuilder aggregationBuilder = AggregationBuilders.scriptedMetric("total_work")
            .initScript(new Script("""
                    state.unique_combinations = [:];
                    """))
            .mapScript(new Script("""
                        def combination = doc['oht_id'].value + '|' + doc['start_time'].value;
                        state.unique_combinations.put(combination, true);
                        """))
            .combineScript(new Script("return state.unique_combinations.size();"))
            .reduceScript(new Script("""
                        def result = 0;
                        for (state in states) {
                            result += state;
                        }
                        return result;
                        """));
        // 질의 추가
        searchSourceBuilder.aggregation(aggregationBuilder);


        // 집계반환만 원하고 Document는 반환X
        searchSourceBuilder.size(0);

        // ==== 질의 ====
        SearchResponse searchResponse = sendElasticsearchQuery(startTime, endTime, searchSourceBuilder);

        // 결과에서 작업량 추출
        ScriptedMetric totalWork = searchResponse.getAggregations().get("total_work");
        return (int) totalWork.aggregation();
    }

    /**
     * 기간동안 OHT별 평균 작업량 계산 함수
     */
    private double getOhtAverageWorkByStartTimeAndTime(LocalDateTime startTime, LocalDateTime endTime) throws
        IOException {
        //ES의 질의 생성
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // ==== 쿼리 검색 ====
        // startTime과 endTime 사이에 있는 로그만 검색하도록 설정
        //시간 포맷 변환
        FormattedTime FormattedTime = TimeConverter.convertElasticsearchTime(startTime, endTime);
        RangeQueryBuilder timeFilter = QueryBuilders.rangeQuery("curr_time")
            .gte(FormattedTime.getStartTime())
            .lte(FormattedTime.getEndTime());

        // 작업 중인 로그만 검색하도록 설정
        TermQueryBuilder statusFilter = QueryBuilders.termQuery("status.keyword", "W");

        // Bool Query로  두 filter 적용
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
            .filter(timeFilter)
            .filter(statusFilter);

        // 질의 추가
        searchSourceBuilder.query(boolQueryBuilder);


        // ==== 집계 검색 ====
        TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("by_oht_id")
            .field("oht_id");

        CardinalityAggregationBuilder cardinalityAggregation = AggregationBuilders.cardinality("count_by_oht_id")
            .field("start_time");

        // oht_id 별 start_time 개수세는 쿼리 추가
        termsAggregation.subAggregation(cardinalityAggregation);

        // 질의 추가
        searchSourceBuilder.aggregation(termsAggregation);


        // 집계반환만 원하고 Document는 반환X
        searchSourceBuilder.size(0);

        // ==== 질의 ====
        SearchResponse searchResponse = sendElasticsearchQuery(startTime, endTime, searchSourceBuilder);

        // 결과에서 작업량 추출
        Terms termsAgg  = searchResponse.getAggregations().get("by_oht_id");

        // 평균 계산
        if(termsAgg.getBuckets().isEmpty()) return 0;
        double sum = 0;
        for(Terms.Bucket bucket: termsAgg.getBuckets()) {
            Cardinality cardinality = bucket.getAggregations().get("count_by_oht_id");
            sum += cardinality.getValue();
        }
        return sum / termsAgg.getBuckets().size();
    }

    /**
     * 기간동안 시간별 작업량 분석
     */
    public List<OhtJobHourlyResponse> ohtJobHourly(LocalDateTime startTime, LocalDateTime endTime) throws IOException {
        Map<Integer, Integer> hourlyWork = getOhtJobHourly(startTime, endTime);
        List<OhtJobHourlyResponse> response = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : hourlyWork.entrySet()) {
            response.add(OhtJobHourlyResponse.builder()
                    .hour(entry.getKey())
                    .work(entry.getValue())
                    .build());
        }
        return response;
    }

    public Map getOhtJobHourly(LocalDateTime startTime, LocalDateTime endTime) throws IOException {
        //ES의 질의 생성
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // ==== 쿼리 검색 ====
        // 쿼리에 필요한 필터 만들기
        //시간 포맷 변환
        FormattedTime FormattedTime = TimeConverter.convertElasticsearchTime(startTime, endTime);
        RangeQueryBuilder timeFilter = QueryBuilders.rangeQuery("curr_time")
            .gte(FormattedTime.getStartTime())
            .lte(FormattedTime.getEndTime());
        TermQueryBuilder statusFilter = QueryBuilders.termQuery("status.keyword", "W");
        MatchQueryBuilder carrierFilter = QueryBuilders.matchQuery("carrier", false);
        ScriptQueryBuilder scriptFilter = QueryBuilders.scriptQuery(
            new Script("doc['current_node.keyword'].value == doc['target_node.keyword'].value")
        );

        // Bool Query로  두 filter 적용
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
            .filter(timeFilter)
            .filter(statusFilter)
            .filter(carrierFilter)
            .filter(scriptFilter);

        // 질의 추가
        searchSourceBuilder.query(boolQueryBuilder);


        // ==== 집계 검색 ====
        MaxAggregationBuilder maxAggregation = AggregationBuilders.max("max_curr_time").field("curr_time");
        TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("group_by_oht_id_start_time")
            .script(
                new Script("doc['oht_id'].value + ' ' + doc['start_time'].value")
            ).size(Integer.MAX_VALUE)
            .subAggregation(maxAggregation);

        // 질의 추가
        searchSourceBuilder.aggregation(termsAggregation);


        // 집계반환만 원하고 Document는 반환X
        searchSourceBuilder.size(0);

        // ==== 질의 ====
        SearchResponse searchResponse = sendElasticsearchQuery(startTime, endTime, searchSourceBuilder);


        // ==== 결과에서 시간 추출 ====
        // 각 Terms의 bucket에서 max_curr_time을 추출하고 카운트
        Terms terms = searchResponse.getAggregations().get("group_by_oht_id_start_time");
        Map<Integer, Integer> hourCounts = new HashMap<>();

        // 시간 포맷터 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for(int i=0; i<=23; i++) {
            hourCounts.put(i, 0);
        }
        for (Terms.Bucket bucket : terms.getBuckets()) {
            Max maxCurrTime = bucket.getAggregations().get("max_curr_time");
            String maxTime = maxCurrTime.getValueAsString();
            // 날짜 파싱 및 시간으로 잘라내기
            LocalDateTime dateTime = LocalDateTime.parse(maxTime, formatter);
            hourCounts.put(dateTime.getHour(), dateTime.getHour() + 1);
        }
        return hourCounts;
    }

    /**
     * elasticsearch로 검색을 요청하고 응답받는 common 함수
     */
    public  SearchResponse sendElasticsearchQuery(LocalDateTime startTime, LocalDateTime endTime, SearchSourceBuilder searchSourceBuilder) throws
        IOException {
        // index 리스트를 만든다.
        String[] indexArray = GenerateIndexNameArray.getIndexNameArray(startTime, endTime);

        //검색 요청객체 생성 및 인덱스이름 설정 및 검색소스 설정
        SearchRequest searchRequest = new SearchRequest(indexArray); // 실제 인덱스 이름 사용
        searchRequest.source(searchSourceBuilder);

        //요청 및 응답받음
        log.debug("[ES request] : "+ searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        log.debug("[ES response] : "+searchResponse.toString());

        return searchResponse;
    }
}

package com.dfg.semento.service;

import com.dfg.semento.document.LogDocument;
import com.dfg.semento.dto.DoubleDataDto;
import com.dfg.semento.dto.IntegerDataDto;
import com.dfg.semento.dto.request.SearchTimeRequest;
import com.dfg.semento.dto.response.JobResultAnalysisErrorLogResponse;
import com.dfg.semento.dto.response.JobResultAnalysisErrorResponse;
import com.dfg.semento.dto.response.JobResultAnalysisRatioResponse;
import com.dfg.semento.dto.response.JobResultAnalysisResponse;
import com.dfg.semento.dto.response.OhtJobAnalysisResponse;
import com.dfg.semento.dto.response.OhtJobHourlyResponse;
import com.dfg.semento.repository.DashboardRepository;
import com.dfg.semento.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.ScriptQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregation;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.TermsValuesSourceBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ScriptedMetric;
import org.elasticsearch.search.aggregations.metrics.ScriptedMetricAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.script.Script;

import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    @Value("${elasticsearch.bucket-size}")
    private int BUKET_SIZE;
    private final DashboardRepository dashboardRepository;
    private final ElasticsearchQueryUtil elasticsearchQueryUtil;

    public List<LogDocument> test() {
        return dashboardRepository.findAll();
    }

    /** 기간동안 OHT 작업량 분석(oht대수, 전체 작업량, oht별 작업량 평균)
     */
    public OhtJobAnalysisResponse ohtJobAnalysis(LocalDateTime startTime, LocalDateTime endTime) throws IOException {
        SearchTimeRequest lastPeriod = calculateLastPeriod(startTime, endTime);

        // 운행한 OHT 대수
        int ohtCount = getOhtCount(startTime, endTime);

        // 기간동안 전체 OHT 작업량
        int totalWork = getOhtTotalWorkByStartTimeAndEndTime(startTime, endTime);
        int lastTotalWork = getOhtTotalWorkByStartTimeAndEndTime(lastPeriod.getStartTime(), lastPeriod.getEndTime());
        double totalWorkPercentage = CalculateData.getDifferencePercentage(totalWork, lastTotalWork);


        // 기간동안 각 OHT별 작업량 평균
        double averageWork = getOhtAverageWorkByStartTimeAndTime(startTime, endTime);
        double lastAverageWork = getOhtAverageWorkByStartTimeAndTime(lastPeriod.getStartTime(), lastPeriod.getEndTime());
        double averageWorkPercentage = CalculateData.getDifferencePercentage(averageWork, lastAverageWork);

        return OhtJobAnalysisResponse.builder()
            .ohtCount(new IntegerDataDto(ohtCount, 0))
            .totalWork(new IntegerDataDto(totalWork, totalWorkPercentage))
            .averageWork(new DoubleDataDto(averageWork, averageWorkPercentage))
            .build();
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

    /**
     * 기간동안 작업 성공/실패 결과 분석
     */
    public JobResultAnalysisResponse jobResultAnalysis(LocalDateTime startTime, LocalDateTime endTime) throws
        IOException {
        // ==== 작업 성공 실패율 분석
        // [0: 전체 작업량 수, 1: 성공 작업량 수, 2: 실패 작업량 수]
        JobResultAnalysisRatioResponse ratioResponse = getJobResultCount(startTime, endTime);

        // ==== 작업 실패 중 에러 발생량
        JobResultAnalysisResponse errorResult = getJobErrorCount(startTime, endTime);

        return JobResultAnalysisResponse.builder()
            .jobResultRatio(ratioResponse)
            .jobResultError(errorResult.getJobResultError())
            .jobResultErrorLog(errorResult.getJobResultErrorLog())
            .build();
    }

    /**
     * [Elasticsearch 검색] 실패한 작업 중 에러 발생 작업 카운트
     */
    private JobResultAnalysisResponse getJobErrorCount(LocalDateTime startTime, LocalDateTime endTime) throws IOException {
        //ES의 질의 생성
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 시간 필터 생성
        FormattedTime FormattedTime = TimeConverter.convertElasticsearchTime(startTime, endTime);
        RangeQueryBuilder timeFilter = QueryBuilders.rangeQuery("curr_time")
            .gte(FormattedTime.getStartTime())
            .lte(FormattedTime.getEndTime());

        //Composite(집계) 설정
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(timeFilter);  // 시간 필터를 bool 쿼리에 추가
        boolQuery.mustNot(QueryBuilders.matchQuery("error", 0));

        //Composite Source 설정
        CompositeAggregationBuilder compositeAgg = AggregationBuilders.composite(
                "unique_combinations",
                Arrays.asList(
                    new TermsValuesSourceBuilder("oht_id").field("oht_id").missingBucket(true),
                    new TermsValuesSourceBuilder("current_node").field("current_node.keyword").missingBucket(true),
                    new TermsValuesSourceBuilder("error").field("error").missingBucket(true),
                    new TermsValuesSourceBuilder("start_time").field("start_time").missingBucket(true)

                ))
            .size(BUKET_SIZE);

        // 검색 요청
        SearchResponse searchResponse = elasticsearchQueryUtil.sendEsQuery(startTime, endTime, boolQuery, compositeAgg);

        // 결과에서 집계 데이터 추출
        CompositeAggregation compositeAggregation = searchResponse.getAggregations().get("unique_combinations");

        // 에러 수 집계용
        int ohtError = 0;
        int facilityError = 0;
        int totalError = compositeAggregation.getBuckets().size();

        // oht_id 별 에러 수 집계
        Map<String, Integer> countErrorPerOht = new HashMap<>();

        // 각 버킷에서 에러 수 추출
        for (CompositeAggregation.Bucket bucket : compositeAggregation.getBuckets()) {
            Map<String, Object> bucketKey = bucket.getKey();
            int ohtId = (int) bucketKey.get("oht_id");
            int error = Integer.parseInt((String) bucketKey.get("error"));
            switch (error) {
                case 200:
                    ohtError++;
                    break;
                case 300:
                    facilityError++;
                    break;
            }
            String key = ohtId+" "+error;
            countErrorPerOht.put(key, countErrorPerOht.getOrDefault(key,  0)+1);
        }

        // 에러 집계 Response로 변환
        JobResultAnalysisErrorResponse errorResponse = JobResultAnalysisErrorResponse
            .builder()
            .totalError(totalError)
            .facilityError(facilityError)
            .ohtError(ohtError)
            .facilityErrorPercentage(CalculateData.getPercentage(facilityError, totalError))
            .ohtErrorPercentage(CalculateData.getPercentage(ohtError, totalError))
            .build();

        // 로그 Response로 변환
        List<JobResultAnalysisErrorLogResponse> logResponses = new ArrayList<>();
        StringTokenizer st;
        for(Map.Entry<String, Integer> entry : countErrorPerOht.entrySet()) {
            st = new StringTokenizer(entry.getKey());
            int ohtId = Integer.parseInt(st.nextToken());
            int error = Integer.parseInt(st.nextToken());
            int count = entry.getValue();
            logResponses.add(
                    JobResultAnalysisErrorLogResponse.builder()
                        .ohtId(ohtId)
                        .error(error)
                        .count(count)
                        .build()
            );
        }
        return JobResultAnalysisResponse.builder()
            .jobResultError(errorResponse)
            .jobResultErrorLog(logResponses)
            .build();
    }

    /**
     * 기간동안 운행했던 OHT 대수 계산 함수
    */
    private int getOhtCount(LocalDateTime startTime, LocalDateTime endTime) throws IOException {
        //ES의 질의 생성

        // ==== 쿼리 검색 ====
        FormattedTime FormattedTime = TimeConverter.convertElasticsearchTime(startTime, endTime);
        RangeQueryBuilder timeFilter = QueryBuilders.rangeQuery("curr_time")
            .gte(FormattedTime.getStartTime())
            .lte(FormattedTime.getEndTime());
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
            .filter(timeFilter);

        // ==== 집계 검색 ====
        CardinalityAggregationBuilder cardinalityAggregation = AggregationBuilders.cardinality("oht_count")
            .field("oht_id");


        // ==== 질의 ====
        SearchResponse searchResponse = elasticsearchQueryUtil.sendEsQuery(startTime, endTime, boolQueryBuilder, cardinalityAggregation);

        // 결과에서 작업량 추출
        Cardinality ohtCount  = searchResponse.getAggregations().get("oht_count");

        return (int) ohtCount.getValue();
    }

    /**
     * [Elasticsearch 검색] 기간동안 전체 작업량 계산 함수
     */
    public int getOhtTotalWorkByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime) throws
        IOException {
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


        // ==== 질의 ====
        SearchResponse searchResponse = elasticsearchQueryUtil.sendEsQuery(startTime, endTime, boolQueryBuilder, aggregationBuilder);

        // 결과에서 작업량 추출
        ScriptedMetric totalWork = searchResponse.getAggregations().get("total_work");
        return (int) totalWork.aggregation();
    }

    /**
     * [Elasticsearch 검색] 기간동안 OHT별 평균 작업량 계산 함수
     */
    private double getOhtAverageWorkByStartTimeAndTime(LocalDateTime startTime, LocalDateTime endTime) throws
        IOException {
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

        // ==== 집계 검색 ====
        TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("by_oht_id")
            .field("oht_id");

        CardinalityAggregationBuilder cardinalityAggregation = AggregationBuilders.cardinality("count_by_oht_id")
            .field("start_time");

        // oht_id 별 start_time 개수세는 쿼리 추가
        termsAggregation.subAggregation(cardinalityAggregation);


        // ==== 질의 ====
        SearchResponse searchResponse = elasticsearchQueryUtil.sendEsQuery(startTime, endTime, boolQueryBuilder, termsAggregation);

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
     * [Elasticsearch 검색] 기간동안 시간대별 작업량
     */
    public Map getOhtJobHourly(LocalDateTime startTime, LocalDateTime endTime) throws IOException {
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

        // ==== 집계 검색 ====
        MaxAggregationBuilder maxAggregation = AggregationBuilders.max("max_curr_time").field("curr_time");
        TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("group_by_oht_id_start_time")
            .script(
                new Script("doc['oht_id'].value + ' ' + doc['start_time'].value")
            ).size(Integer.MAX_VALUE)
            .subAggregation(maxAggregation);

        // ==== 질의 ====
        SearchResponse searchResponse = elasticsearchQueryUtil.sendEsQuery(startTime, endTime, boolQueryBuilder, termsAggregation);


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
     * [Elasticsearch 검색] 전체 작업량 중 성공, 실패량 및 퍼센테이지
     */
    private JobResultAnalysisRatioResponse getJobResultCount(LocalDateTime startTime, LocalDateTime endTime) throws
        IOException {
        // ==== 쿼리 검색 ====
        // 쿼리에 필요한 필터 만들기
        //시간 포맷 변환
        FormattedTime FormattedTime = TimeConverter.convertElasticsearchTime(startTime, endTime);
        RangeQueryBuilder timeFilter = QueryBuilders.rangeQuery("curr_time")
            .gte(FormattedTime.getStartTime())
            .lte(FormattedTime.getEndTime());
        TermQueryBuilder statusFilter = QueryBuilders.termQuery("status", "W");
        ScriptQueryBuilder scriptFilter = QueryBuilders.scriptQuery(
            new Script("doc['current_node'] == doc['target_node']")
        );

        // Bool Query로  두 filter 적용
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
            .filter(timeFilter)
            .filter(scriptFilter)
            .filter(statusFilter);

        // ==== 집계 검색 ====
        TopHitsAggregationBuilder topHitsAggregation = AggregationBuilders.topHits("latest_record")
            .size(1)
            .sort(SortBuilders.fieldSort("curr_time").order(SortOrder.DESC))
            .fetchSource("is_fail", null);
        TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("group_by_oht_id_start_time")
            .script(
                new Script("doc['oht_id'].value + ' ' + doc['start_time'].value")
            ).size(Integer.MAX_VALUE)
            .subAggregation(topHitsAggregation);

        // ==== 질의 ====
        SearchResponse searchResponse = elasticsearchQueryUtil.sendEsQuery(startTime, endTime, boolQueryBuilder, termsAggregation);

        // ==== 결과에서 개수 추출 ====
        Terms termsAgg  = searchResponse.getAggregations().get("group_by_oht_id_start_time");

        int totalWork = termsAgg.getBuckets().size();
        int successWork = 0;
        int failedWork = 0;

        for(Terms.Bucket bucket: termsAgg.getBuckets()) {
            TopHits topHits = bucket.getAggregations().get("latest_record");
            boolean isFail = (boolean) topHits.getHits().getHits()[0].getSourceAsMap().get("is_fail");
            if(isFail) failedWork++;
            else successWork++;
        }

        return JobResultAnalysisRatioResponse.builder()
            .totalWork(totalWork)
            .successWork(successWork)
            .failedWork(failedWork)
            .successPercentage(CalculateData.getPercentage(successWork, totalWork))
            .failedPercentage(CalculateData.getPercentage(failedWork, totalWork))
            .build();
    }


    /**
     * startTime, endTime을 기준으로 지난 기간의 시작, 종료 시간 계산하기
     */
    private SearchTimeRequest calculateLastPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        // 현재 시간에서 1달 전의 년도와 월 계산
        int lastMonthYear = startTime.minusMonths(1).getYear();
        int lastMonth = startTime.minusMonths(1).getMonthValue();

        // 지난 달의 첫 번째 날과 마지막 날 구하기
        LocalDateTime firstDayOfLastMonth = LocalDateTime.of(LocalDate.of(lastMonthYear, lastMonth, 1), LocalTime.MIN);
        LocalDateTime lastDayOfLastMonth = LocalDateTime.of(LocalDate.of(lastMonthYear, lastMonth, 1).withDayOfMonth(1).plusMonths(1).minusDays(1), LocalTime.MAX);
        return new SearchTimeRequest(firstDayOfLastMonth, lastDayOfLastMonth);
    }


}

package com.dfg.semento.service;

import com.dfg.semento.dto.DoubleDataDto;
import com.dfg.semento.dto.IntegerDataDto;
import com.dfg.semento.dto.request.DateAndOhtRequest;
import com.dfg.semento.dto.response.ComparedDataResponse;
import com.dfg.semento.repository.SimulationRepository;
import com.dfg.semento.util.CalculateOhtData;
import com.dfg.semento.util.GenerateESQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregation;
import org.elasticsearch.search.aggregations.bucket.composite.CompositeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.composite.ParsedComposite;
import org.elasticsearch.search.aggregations.bucket.composite.TermsValuesSourceBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SimulationService {

    @Value("${elasticsearch.bucket-size}")
    private int bucketSize;
    private final SimulationRepository simulationRepository;
    private final RestHighLevelClient client;
    private Long runningOhtCnt;

    /** 개별OHT와 모든OHT의 비교데이터를 구하는 메소드
     * @author 최서현
     */
    public ComparedDataResponse getComparedDate(DateAndOhtRequest dateAndOht){
        LocalDateTime startTime = dateAndOht.getStartDate();
        LocalDateTime endTime = dateAndOht.getEndDate();
        Long ohtId = dateAndOht.getOhtId();

        try{
            runningOhtCnt = setRunningOhtCnt(startTime, endTime);

            IntegerDataDto totalWorkData = totalWorkComparison(startTime, endTime, ohtId);
            IntegerDataDto deadLineData = outOfDeadLineComparison(startTime, endTime, ohtId);
            DoubleDataDto speedData = averageSpeedComparison(startTime, endTime, ohtId);
            IntegerDataDto errorData = ohtErrorComparison(startTime, endTime, ohtId);

            return ComparedDataResponse.builder()
                    .totalWork(totalWorkData)
                    .outOfDeadLine(deadLineData)
                    .averageSpeed(speedData)
                    .ohtError(errorData).build();

        } catch (IOException e){
            log.error("검색 작업 실행 실패", e);
            throw new RuntimeException("Elasticsearch 검색 작업 실패", e);
        }
    }


    /** 개별 OHT의 작업량과 전체 OHT 평균작업량 비교
     * @author 최서현
     */
    public IntegerDataDto totalWorkComparison(LocalDateTime startTime, LocalDateTime endTime, Long ohtId){
        return null;
    }

    /** 해당기간동안 일을 한 OHT들의 갯수를 구하는 메소드(평균구할때 사용)
     * @author 최서현
     */
    public Long setRunningOhtCnt(LocalDateTime startTime, LocalDateTime endTime) throws IOException {
        //인덱스 리스트 생성
        String[] indexArray = GenerateESQuery.getIndexNameArray(startTime, endTime);
        //시간필터 생성
        RangeQueryBuilder timeFilter = GenerateESQuery.generatedTimeFilter(startTime, endTime);

        // 고유 oht_id 수를 계산하기 위한 cardinality 집계
        CardinalityAggregationBuilder uniqueOhtIdCount = AggregationBuilders.cardinality("unique_oht_count")
                .field("oht_id");

        //기간동안 일을 했던 oht들만 계산에 반영함
        SearchSourceBuilder searchSourceBuilder2 = new SearchSourceBuilder()
                .query(timeFilter)
                .aggregation(uniqueOhtIdCount)
                .size(0);  // 문서 반환 없이 집계 결과만 반환

        SearchRequest searchRequest2 = new SearchRequest(indexArray);
        searchRequest2.source(searchSourceBuilder2);
        log.debug("[ES 요청]: " + searchSourceBuilder2);

        SearchResponse searchResponse2 = client.search(searchRequest2, RequestOptions.DEFAULT);

        // 집계 결과 추출
        Cardinality result = searchResponse2.getAggregations().get("unique_oht_count");
        Long runningOhtCnt = result.getValue();

        return runningOhtCnt;
    }

    /** 개별 OHT의 에러 갯수과 전체 OHT 에러갯수 비교
     * @author 최서현
     */
    public IntegerDataDto ohtErrorComparison(LocalDateTime startTime, LocalDateTime endTime, Long ohtId) throws IOException {
        //인덱스 리스트 생성
        String[] indexArray = GenerateESQuery.getIndexNameArray(startTime, endTime);

        //시간필터 생성
        RangeQueryBuilder timeFilter = GenerateESQuery.generatedTimeFilter(startTime, endTime);

        //Composite(집계) 설정
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(timeFilter);  // 시간 필터를 bool 쿼리에 추가
        //boolQuery.must(QueryBuilders.termQuery("oht_id", ohtId));
        boolQuery.must(QueryBuilders.scriptQuery(new Script("doc['error'].value != 0")));

        //Composite Source 설정
        CompositeAggregationBuilder compositeAgg = AggregationBuilders.composite(
                "unique_combinations",
                Arrays.asList(
                        new TermsValuesSourceBuilder("oht_id").field("oht_id").missingBucket(true),
                        new TermsValuesSourceBuilder("current_node").field("current_node.keyword").missingBucket(true),
                        new TermsValuesSourceBuilder("error").field("error").missingBucket(true),
                        new TermsValuesSourceBuilder("start_time").field("start_time").missingBucket(true)

                ))
                .size(1000);

        //Request 만들기
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQuery);
        searchSourceBuilder.aggregation(compositeAgg);
        searchSourceBuilder.size(0);  // Document 반환 없음

        // SearchRequest 생성 및 설정
        SearchRequest searchRequest = new SearchRequest(indexArray);
        searchRequest.source(searchSourceBuilder);

        log.debug("[ES 요청]: " + searchSourceBuilder);
        // ElasticSearch 요청 및 응답
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 결과에서 집계 데이터 추출
        CompositeAggregation compositeAggregation = searchResponse.getAggregations().get("unique_combinations");

        int ohtError = 0;
        int overallError = compositeAggregation.getBuckets().size();
        double overallErrorAvg = (double) overallError/runningOhtCnt;

        // 각 버킷에서 OHT ID와 에러 수 추출
        for (CompositeAggregation.Bucket bucket : compositeAggregation.getBuckets()) {
            Map<String, Object> bucketKey = bucket.getKey();
            if((int) bucketKey.get("oht_id") == ohtId.intValue()) ohtError++;
        }

        double differencePercentage = CalculateOhtData.getDifferencePercentage(ohtError, overallErrorAvg);
        return IntegerDataDto.builder().data(ohtError).percent(differencePercentage).build();
    }

    /** DeadLine을 넘은 작업량
     * @author 최서현
     */
    public IntegerDataDto outOfDeadLineComparison(LocalDateTime startTime, LocalDateTime endTime, Long ohtId) throws IOException {
        //인덱스 리스트 생성
        String[] indexArray = GenerateESQuery.getIndexNameArray(startTime, endTime);

        //시간필터 생성
        RangeQueryBuilder timeFilter = GenerateESQuery.generatedTimeFilter(startTime, endTime);

        // 고유 oht_id 수를 계산하기 위한 cardinality 집계
        CardinalityAggregationBuilder uniqueOhtIdCount = AggregationBuilders.cardinality("unique_oht_count")
                .field("oht_id");

        // 전체 DeadLine 카운트 및 oht_id의 고유 개수를 위한 설정
        CompositeAggregationBuilder overallCount = AggregationBuilders.composite("overall_count",
                List.of(
                        new TermsValuesSourceBuilder("oht_id").field("oht_id"),
                        new TermsValuesSourceBuilder("start_time").field("start_time")
                ))
                .size(bucketSize);

        // 특정 OHT DeadLine 카운트를 위한 설정
        FilterAggregationBuilder ohtCountFilter = AggregationBuilders.filter("oht_"+String.valueOf(ohtId)+"_count",
                        QueryBuilders.boolQuery()
                                .must(QueryBuilders.termQuery("oht_id", ohtId))
                                .must(timeFilter))
                                .subAggregation(AggregationBuilders.composite("oht_count",
                                    List.of(
                                            new TermsValuesSourceBuilder("oht_id").field("oht_id"),
                                            new TermsValuesSourceBuilder("start_time").field("start_time")
                                    ))
                                .size(bucketSize)
                                );

        BoolQueryBuilder baseFilter = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("status.keyword", "W"))
                .must(QueryBuilders.termQuery("is_fail", true));

        // 쿼리 빌더 및 집계 설정
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery().must(timeFilter).must(baseFilter))
                .aggregation(overallCount)
                .aggregation(ohtCountFilter)
                .size(0);  // 문서 반환 없이 집계 결과만 반환

        //기간동안 일을 했던 oht들만 계산에 반영함
        SearchSourceBuilder searchSourceBuilder2 = new SearchSourceBuilder()
                .query(timeFilter)
                .aggregation(uniqueOhtIdCount)
                .size(0);  // 문서 반환 없이 집계 결과만 반환


        // 검색 요청 생성 및 실행
        SearchRequest searchRequest = new SearchRequest(indexArray);
        searchRequest.source(searchSourceBuilder);

        log.debug("[ES 요청]: " + searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        // 집계 결과 추출
        Aggregations aggregations = searchResponse.getAggregations();
        ParsedComposite overallResults = aggregations.get("overall_count");
        ParsedComposite ohtResults = ((ParsedFilter)aggregations.get("oht_" + ohtId + "_count")).getAggregations().get("oht_count");

        int overallCnt = overallResults.getBuckets().size();
        int ohtCnt = ohtResults.getBuckets().size();
        int overallCntAvg = overallCnt/runningOhtCnt.intValue();

        //차이 퍼센티지 구함
        double differencePercentage = CalculateOhtData.getDifferencePercentage(ohtCnt, overallCntAvg);
        return IntegerDataDto.builder().data(ohtCnt).percent(differencePercentage).build();
    }

    /** 개별 OHT의 평균속도와 전체 OHT 평균속도 비교
     * @author 최서현
     */
    public DoubleDataDto averageSpeedComparison(LocalDateTime startTime, LocalDateTime endTime, Long ohtId) throws IOException {
        
        //인덱스 리스트 생성
        String[] indexArray = GenerateESQuery.getIndexNameArray(startTime, endTime);

        //시간필터 생성
        RangeQueryBuilder timeFilter = GenerateESQuery.generatedTimeFilter(startTime, endTime);

        //필터를 쿼리에 추가함
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(timeFilter);

        // 전체 평균속도 계산 집계
        AvgAggregationBuilder overallAvgSpeed = AggregationBuilders.avg("overall_average_speed").field("speed");
        // 특정호기 OHT 평균속도 계산을 위한 집계
        AvgAggregationBuilder ohtAvgSpeed = AggregationBuilders.avg("oht_"+String.valueOf(ohtId)+"_average_speed").field("speed");

        // 특정호기 OHT에 대한 필터 생성
        FilterAggregationBuilder ohtFilter = AggregationBuilders
                .filter("filter_oht_"+String.valueOf(ohtId), QueryBuilders.termQuery("oht_id", ohtId))
                .subAggregation(ohtAvgSpeed);

        // 전체 평균속도와 특정호기 OHT 평균속도를 검색소스빌더에 추가
        searchSourceBuilder.aggregation(overallAvgSpeed);
        searchSourceBuilder.aggregation(ohtFilter);

        // 집계반환만 원하고 Document는 반환X
        searchSourceBuilder.size(0);

        //검색 요청객체 생성 및 인덱스이름 설정 및 검색소스 설정
        SearchRequest searchRequest = new SearchRequest(indexArray); // 실제 인덱스 이름 사용
        searchRequest.source(searchSourceBuilder);

        //요청 및 응답받음
        log.debug("[ES request] : "+ searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        //log.debug("[ES response] : "+searchResponse.toString());

        // 결과에서 전체 및 특정호기 평균속도를 추출
        Avg overallAvg = searchResponse.getAggregations().get("overall_average_speed");
        Filter filter = searchResponse.getAggregations().get("filter_oht_"+String.valueOf(ohtId));
        Avg ohtAvg = filter.getAggregations().get("oht_"+String.valueOf(ohtId)+"_average_speed");

        //차이 퍼센티지 구함
        double differencePercentage = CalculateOhtData.getDifferencePercentage(ohtAvg.getValue(), overallAvg.getValue());
        return DoubleDataDto.builder().data(ohtAvg.value()).percent(differencePercentage).build();

    }

}

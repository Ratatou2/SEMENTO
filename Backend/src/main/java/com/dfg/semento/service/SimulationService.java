package com.dfg.semento.service;

import com.dfg.semento.dto.DoubleDataDto;
import com.dfg.semento.dto.IntegerDataDto;
import com.dfg.semento.dto.request.DateAndOhtRequest;
import com.dfg.semento.dto.response.ComparedDataResponse;
import com.dfg.semento.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
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
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimulationService {

    @Value("${elasticsearch.bucket-size}")
    private int bucketSize;
    private final DashboardService dashboardService;
    private final ElasticsearchQueryUtil elasticsearchQueryUtil;
    private Long runningOhtCnt;

    /** 개별OHT와 모든OHT의 비교데이터를 구하는 메소드
     * @author 최서현
     */
    public ComparedDataResponse getComparedDate(DateAndOhtRequest dateAndOht) throws IOException {
        LocalDateTime startTime = dateAndOht.getStartDate();
        LocalDateTime endTime = dateAndOht.getEndDate();
        Long ohtId = dateAndOht.getOhtId();

        //평균위한 Running oht 갯수 초기화
        runningOhtCnt = setRunningOhtCnt(startTime, endTime);

        //각 검색결과 얻기
        IntegerDataDto totalWorkData = totalWorkComparison(startTime, endTime, ohtId);
        IntegerDataDto deadLineData = outOfDeadLineComparison(startTime, endTime, ohtId);
        DoubleDataDto speedData = averageSpeedComparison(startTime, endTime, ohtId);
        IntegerDataDto errorData = ohtErrorComparison(startTime, endTime, ohtId);

        return ComparedDataResponse.builder()
                .totalWork(totalWorkData)
                .outOfDeadLine(deadLineData)
                .averageSpeed(speedData)
                .ohtError(errorData).build();
    }


    /** 해당기간동안 일을 한 OHT들의 갯수를 구하는 메소드(평균구할때 사용)
     * @author 최서현
     */
    public Long setRunningOhtCnt(LocalDateTime startTime, LocalDateTime endTime) throws IOException {
        // 고유 oht_id 수를 계산하기 위한 cardinality 집계 생성
        CardinalityAggregationBuilder uniqueOhtIdCount = AggregationBuilders.cardinality("unique_oht_count")
                .field("oht_id");

        // 판단필터 생성 => 운행중이었던것만 체크
        TermQueryBuilder statusFilter = QueryBuilders.termQuery("status.keyword", "G");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .filter(statusFilter);

        // 쿼리날림
        SearchResponse searchResponse = elasticsearchQueryUtil.sendEsQuery(startTime, endTime, boolQueryBuilder, uniqueOhtIdCount);

        // 집계 결과 추출
        Cardinality result = searchResponse.getAggregations().get("unique_oht_count");
        Long runningOhtCnt = result.getValue();

        return runningOhtCnt;
    }


    /** 개별 OHT의 작업량과 전체 OHT 평균작업량 비교
     * @author 최서현
     */
    public IntegerDataDto totalWorkComparison(LocalDateTime startTime, LocalDateTime endTime, Long ohtId) throws IOException {
        // 전체 OHT 작업량 평균
        double overallTotalWorkAvg = (double) dashboardService.getOhtTotalWorkByStartTimeAndEndTime(startTime, endTime) / runningOhtCnt;

        // 작업 중인 로그만 검색하도록 설정
        TermQueryBuilder statusFilter = QueryBuilders.termQuery("status.keyword", "W");
        // 특정 OHT_ID만 필터링
        TermQueryBuilder ohtIdFilter = QueryBuilders.termQuery("oht_id", ohtId);

        // Bool Query로  두 filter 적용
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .filter(ohtIdFilter)
                .filter(statusFilter);

        // ==== 집계 검색 ====
        TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("by_oht_id").field("oht_id");
        CardinalityAggregationBuilder cardinalityAggregation = AggregationBuilders.cardinality("count_by_oht_id").field("start_time");
        termsAggregation.subAggregation(cardinalityAggregation);

        // 쿼리날림
        SearchResponse searchResponse = elasticsearchQueryUtil.sendEsQuery(startTime, endTime, boolQueryBuilder, termsAggregation);

        // 결과에서 작업량 추출
        Terms termsAgg  = searchResponse.getAggregations().get("by_oht_id");
        int ohtTotalWork = 0;
        for(Terms.Bucket bucket: termsAgg.getBuckets()) {
            Cardinality cardinality = bucket.getAggregations().get("count_by_oht_id");
            ohtTotalWork += cardinality.getValue();
        }

        double differencePercentage = CalculateData.getDifferencePercentage(ohtTotalWork, overallTotalWorkAvg);
        return IntegerDataDto.builder().data(ohtTotalWork).percent(differencePercentage).build();
    }

    /** 개별 OHT의 에러 갯수과 전체 OHT 에러갯수 비교
     * @author 최서현
     */
    public IntegerDataDto ohtErrorComparison(LocalDateTime startTime, LocalDateTime endTime, Long ohtId) throws IOException {

        //Composite(집계) 설정
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
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

        // ElasticSearch 요청 및 응답
        SearchResponse searchResponse = elasticsearchQueryUtil.sendEsQuery(startTime, endTime, boolQuery, compositeAgg);

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

        double differencePercentage = CalculateData.getDifferencePercentage(ohtError, overallErrorAvg);
        return IntegerDataDto.builder().data(ohtError).percent(differencePercentage).build();
    }

    /** DeadLine을 넘은 작업량
     * @author 최서현
     */
    public IntegerDataDto outOfDeadLineComparison(LocalDateTime startTime, LocalDateTime endTime, Long ohtId) throws IOException {
        RangeQueryBuilder timeFilter = ElasticsearchQueryUtil.generatedTimeFilter(startTime, endTime);

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

        BoolQueryBuilder filter = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("status.keyword", "W"))
                .must(QueryBuilders.termQuery("is_fail", true));

        SearchResponse searchResponse = elasticsearchQueryUtil.sendEsQuery(startTime, endTime, filter,
                overallCount, ohtCountFilter);

        // 집계 결과 추출
        Aggregations aggregations = searchResponse.getAggregations();
        ParsedComposite overallResults = aggregations.get("overall_count");
        ParsedComposite ohtResults = ((ParsedFilter)aggregations.get("oht_" + ohtId + "_count")).getAggregations().get("oht_count");

        int overallCnt = overallResults.getBuckets().size();
        int ohtCnt = ohtResults.getBuckets().size();
        int overallCntAvg = overallCnt/runningOhtCnt.intValue();

        //차이 퍼센티지 구함
        double differencePercentage = CalculateData.getDifferencePercentage(ohtCnt, overallCntAvg);
        return IntegerDataDto.builder().data(ohtCnt).percent(differencePercentage).build();
    }

    /** 개별 OHT의 평균속도와 전체 OHT 평균속도 비교
     * @author 최서현
     */
    public DoubleDataDto averageSpeedComparison(LocalDateTime startTime, LocalDateTime endTime, Long ohtId) throws IOException {

        // 전체 평균속도 계산 집계
        AvgAggregationBuilder overallAvgSpeed = AggregationBuilders.avg("overall_average_speed").field("speed");
        // 특정호기 OHT 평균속도 계산을 위한 집계
        AvgAggregationBuilder ohtAvgSpeed = AggregationBuilders.avg("oht_"+String.valueOf(ohtId)+"_average_speed").field("speed");

        // 특정호기 OHT에 대한 필터 생성
        FilterAggregationBuilder ohtFilter = AggregationBuilders
                .filter("filter_oht_"+String.valueOf(ohtId), QueryBuilders.termQuery("oht_id", ohtId))
                .subAggregation(ohtAvgSpeed);

        //요청 및 응답받음
        SearchResponse searchResponse = elasticsearchQueryUtil.sendEsQuery(startTime, endTime,
                overallAvgSpeed, ohtFilter);

        // 결과에서 전체 및 특정호기 평균속도를 추출
        Avg overallAvg = searchResponse.getAggregations().get("overall_average_speed");
        Filter filter = searchResponse.getAggregations().get("filter_oht_"+String.valueOf(ohtId));
        Avg ohtAvg = filter.getAggregations().get("oht_"+String.valueOf(ohtId)+"_average_speed");

        //차이 퍼센티지 구함
        double differencePercentage = CalculateData.getDifferencePercentage(ohtAvg.getValue(), overallAvg.getValue());
        return DoubleDataDto.builder().data(ohtAvg.value()).percent(differencePercentage).build();

    }

}

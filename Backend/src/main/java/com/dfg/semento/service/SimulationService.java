package com.dfg.semento.service;

import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import com.dfg.semento.dto.DoubleDataDto;
import com.dfg.semento.repository.SimulationRepository;
import com.dfg.semento.util.CalculateOhtData;
import com.dfg.semento.util.FormattedTime;
import com.dfg.semento.util.GenerateIndexNameArray;
import com.dfg.semento.util.TimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimulationService {

    private final SimulationRepository simulationRepository;
    private final RestHighLevelClient client;

    /** 개별 OHT의 작업량과 전체 OHT 평균작업량 비교
     * @author 최서현
     */
    public void getTotalWorkComparison(){
        
    }



    /** 개별 OHT의 평균속도와 전체 OHT 평균속도 비교
     * @author 최서현
     */
    public DoubleDataDto getAverageSpeedComparison(LocalDateTime startTime, LocalDateTime endTime, Long ohtId) throws IOException {
        // index 리스트를 만든다.
        String[] indexArray = GenerateIndexNameArray.getIndexNameArray(startTime, endTime);

        //시간 포맷 변환
        FormattedTime FormattedTime = TimeConverter.convertElasticsearchTime(startTime, endTime);
        // startTime과 endTime 사이에 있는 로그만 검색하도록 설정
        RangeQueryBuilder timeFilter = QueryBuilders.rangeQuery("curr_time")
                .gte(FormattedTime.getStartTime())
                .lte(FormattedTime.getEndTime());

        //ES의 질의 생성을 위한 객체, 필터를 쿼리에 추가
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(timeFilter);


        // 전체 평균속도 계산 집계
        AvgAggregationBuilder overallAvgSpeed = AggregationBuilders.avg("overall_average_speed").field("speed");

        // 12호기 OHT 평균속도 계산을 위한 집계
        AvgAggregationBuilder ohtAvgSpeed = AggregationBuilders.avg("oht_"+String.valueOf(ohtId)+"_average_speed").field("speed");

        // 12호기 OHT에 대한 필터
        FilterAggregationBuilder ohtFilter = AggregationBuilders
                .filter("filter_oht_"+String.valueOf(ohtId), QueryBuilders.termQuery("oht_id", ohtId))
                .subAggregation(ohtAvgSpeed);

        // 전체 평균속도와 12호기 OHT 평균속도를 검색소스빌더에 추가
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
        log.debug("[ES response] : "+searchResponse.toString());

        // 결과에서 전체 및 12호기 평균속도를 추출
        Avg overallAvg = searchResponse.getAggregations().get("overall_average_speed");
        Filter filter = searchResponse.getAggregations().get("filter_oht_"+String.valueOf(ohtId));
        Avg ohtAvg = filter.getAggregations().get("oht_"+String.valueOf(ohtId)+"_average_speed");

        //차이 퍼센티지 구함
        double differencePercentage = CalculateOhtData.getDifferencePercentage(ohtAvg.getValue(), overallAvg.getValue());

        return new DoubleDataDto(ohtAvg.value(),differencePercentage);
    }
}

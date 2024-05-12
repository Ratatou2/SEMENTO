package com.dfg.semento.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ElasticsearchQueryUtil {
    private final RestHighLevelClient client;

    /** ES의 검색할 인덱스명을 생성하는 메소드
     * @author 최서현
     * @param startTime 시작시간
     * @param endTime 종료시간
     * @return String[] 검색할 인덱스명 배열
     */
    public static String[] getIndexNameArray(LocalDateTime startTime, LocalDateTime endTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = startTime.toLocalDate();
        LocalDate end = endTime.toLocalDate();

        // 인덱스 리스트를 생성
        List<String> indices = new ArrayList<>();
        while (!start.isAfter(end)) {
            log.debug("인덱스명 [semento-mysql-logs-" + start.format(formatter)+"] 생성됨.");
            indices.add("semento-mysql-logs-" + start.format(formatter));
            start = start.plusDays(1);
        }

        return indices.toArray(new String[0]);  // 검색 요청에 사용할 인덱스 배열
    }

    /** ES의 검색할 시간을 원하는 포맷으로 설정하는 메소드
     * @author 최서현
     * @param startTime 시작시간
     * @param endTime 종료시간
     * @return RangeQueryBuilder 시간검색쿼리
     */
    public static RangeQueryBuilder generatedTimeFilter(LocalDateTime startTime, LocalDateTime endTime){
        //시간 포맷 변환
        FormattedTime formattedTime = TimeConverter.convertElasticsearchTime(startTime, endTime);

        // startTime과 endTime 사이에 있는 로그만 검색하도록 설정
        RangeQueryBuilder timeFilter = QueryBuilders.rangeQuery("curr_time")
                .gte(formattedTime.getStartTime())
                .lte(formattedTime.getEndTime());

        return timeFilter;
    }


    /**
     * Elasticsearch 쿼리를 전송하는 일반화된 메소드
     * @author 최서현
     * @param startTime 시작시간
     * @param endTime 종료시간
     * @param boolQueryBuilder 조건
     * @param aggregations 집계조건들
     * @return 검색 응답
     * @throws IOException 네트워크 또는 ES 서버에서 예외 발생 시
     */
    public SearchResponse sendEsQuery(LocalDateTime startTime, LocalDateTime endTime, BoolQueryBuilder boolQueryBuilder, AggregationBuilder... aggregations) throws IOException {
        //인덱스 리스트 생성
        String[] indexArray = getIndexNameArray(startTime, endTime);
        //시간필터 생성
        RangeQueryBuilder timeFilter = generatedTimeFilter(startTime, endTime);
        boolQueryBuilder.must(timeFilter);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(boolQueryBuilder)
                .size(0);  // 문서 반환 없이 집계 결과만 반환

        for(AggregationBuilder aggregation : aggregations) searchSourceBuilder.aggregation(aggregation);

        SearchRequest searchRequest = new SearchRequest(indexArray);
        searchRequest.source(searchSourceBuilder);

        log.debug("[ES request] : "+ searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        //log.debug("[ES response] : "+searchResponse.toString());

        return searchResponse;
    }

    /**
     * Elasticsearch 쿼리를 전송하는 일반화된 메소드
     * @author 최서현
     * @param startTime 검색시작시간
     * @param endTime 검색끝시간
     * @param aggregations 집계조건들
     * @return 검색 응답
     * @throws IOException 네트워크 또는 ES 서버에서 예외 발생 시
     */
    public SearchResponse sendEsQuery(LocalDateTime startTime, LocalDateTime endTime, AggregationBuilder... aggregations) throws IOException {
        //인덱스 리스트 생성
        String[] indexArray = getIndexNameArray(startTime, endTime);
        //시간필터 생성
        RangeQueryBuilder timeFilter = generatedTimeFilter(startTime, endTime);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .size(0);  // 문서 반환 없이 집계 결과만 반환

        for(AggregationBuilder aggregation : aggregations) searchSourceBuilder.aggregation(aggregation);

        SearchRequest searchRequest = new SearchRequest(indexArray);
        searchRequest.source(searchSourceBuilder);

        log.debug("[ES request] : "+ searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        //log.debug("[ES response] : "+searchResponse.toString());

        return searchResponse;
    }
}

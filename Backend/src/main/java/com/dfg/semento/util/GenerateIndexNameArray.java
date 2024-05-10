package com.dfg.semento.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class GenerateIndexNameArray {
    /** ES의 검색할 인덱스명을 생성하는 메소드
     * @author 최서현
     * @param startTime, endTime
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

}

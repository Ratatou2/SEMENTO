package com.dfg.semento.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


public class TimeConverter {

    /** ES 질의를 위한 시간포맷을 만들어주는 컨버터
     * @author 최서현
     * @param startTime, endTime
     * @return FormattedTime
     */
    public static FormattedTime convertElasticsearchTime(LocalDateTime startTime, LocalDateTime endTime){
        // 타임존을 지정하여 ZonedDateTime으로 변환
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedStartTime = startTime.atZone(zoneId);
        ZonedDateTime zonedEndTime = endTime.atZone(zoneId);

        // 원하는 포맷으로 문자열 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        String formattedStartTime = formatter.format(zonedStartTime);
        String formattedEndTime = formatter.format(zonedEndTime);

        return new FormattedTime(formattedStartTime, formattedEndTime);
    }

    /** String타입의 시간을 LocalDateTime으로 변환
     * @author 최서현
     * @param dateTimeStr 시간문자열
     * @return LocalDateTime
     */
    public static LocalDateTime convertStringToLocalDateTime(String dateTimeStr){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        OffsetDateTime odt = OffsetDateTime.parse(dateTimeStr, formatter);
        LocalDateTime ldt = odt.toLocalDateTime();
        return ldt;
    }
}

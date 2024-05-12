package com.dfg.semento.dto.response;

import com.dfg.semento.dto.DoubleDataDto;
import com.dfg.semento.dto.IntegerDataDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ComparedDataResponse {
    @JsonProperty(value = "total-work")
    private IntegerDataDto totalWork;

    @JsonProperty(value = "out-of-deadline")
    private IntegerDataDto outOfDeadLine;

    @JsonProperty(value = "average-speed")
    private DoubleDataDto averageSpeed;

    @JsonProperty(value = "oht-error")
    private IntegerDataDto ohtError;
}

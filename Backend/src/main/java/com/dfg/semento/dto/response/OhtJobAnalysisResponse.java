package com.dfg.semento.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OhtJobAnalysisResponse {

	private long ohtCount;

	private long totalWork;

	private double averageWork;
}

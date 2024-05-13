package com.dfg.semento.service;

import com.dfg.semento.dto.request.SearchTimeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-server-service", url="${ai.server.url")
public interface AiAnalyticsService {

    @GetMapping("/test")
    Object getAnalyticsResultByAi(@RequestBody SearchTimeRequest request);
}

package com.dfg.semento.controller;

import com.dfg.semento.document.LogDocument;
import com.dfg.semento.dto.request.SearchTimeRequest;
import com.dfg.semento.dto.response.OhtJobAnalysisResponse;
import com.dfg.semento.service.DashboardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/test")
    public ResponseEntity<List<LogDocument>> test(){
        List<LogDocument> logs = dashboardService.test();
        return ResponseEntity.ok(logs);
    }

    @PostMapping ("/oht-job-analysis")
    public ResponseEntity<OhtJobAnalysisResponse> ohtJobAnalysis(@Valid @RequestBody SearchTimeRequest request) throws IOException {
        OhtJobAnalysisResponse ohtJobAnalysis = dashboardService.ohtJobAnalysis(request.getStartTime(), request.getEndTime());
        return ResponseEntity.ok(ohtJobAnalysis);
    }

}

package com.dfg.semento.controller;

import com.dfg.semento.document.LogDocument;
import com.dfg.semento.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/test")
    public ResponseEntity<List<LogDocument>> test(){
        List<LogDocument> logs = dashboardService.test();
        return ResponseEntity.ok(logs);
    }


}

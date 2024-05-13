package com.dfg.semento.controller;

import com.dfg.semento.dto.request.SearchTimeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AiAnalyticsController {

    @PostMapping("/ai-detection")
    public ResponseEntity<Void> aiDetection(@RequestBody SearchTimeRequest searchTimeRequest){
        //return ResponseEntity.ok();
        return null;
    }
}

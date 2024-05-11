package com.dfg.semento.service;

import com.dfg.semento.document.LogDocument;
import com.dfg.semento.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardRepository dashboardRepository;
    public List<LogDocument> test() {
        return dashboardRepository.findAll();
    }
}

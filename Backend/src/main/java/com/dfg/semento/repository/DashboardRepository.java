package com.dfg.semento.repository;

import com.dfg.semento.document.LogDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface DashboardRepository extends ElasticsearchRepository<LogDocument, String> {

    List<LogDocument> findAll();
}

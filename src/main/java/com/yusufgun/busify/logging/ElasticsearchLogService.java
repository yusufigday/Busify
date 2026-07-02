package com.yusufgun.busify.logging;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.rmi.server.ExportException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchLogService {

    private final ElasticsearchClient client;

    public void sendLog(String index, String level, String message, Map<String, Object> details){
        try {
            Map<String, Object> logEntry = new HashMap<>();
            logEntry.put("@timestamp", Instant.now().toString());
            logEntry.put("level", level);
            logEntry.put("message", message);
            logEntry.put("service", "busify");

            if (details != null) {
                logEntry.putAll(details);
            }

            IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                    .index(index)
                    .document(logEntry)
            );

            client.index(request);
        }catch (Exception e){
            log.error("Failed to send log to Elasticsearch: {}", e.getMessage());
        }
    }

}

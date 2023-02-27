package com.sc.enricher.controller;

import com.sc.enricher.processor.IFileProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class TradeEnrichmentController {
    private static final Logger logger = LoggerFactory.getLogger(TradeEnrichmentController.class);
    private String contentType = "text/csv";

    @Autowired
    private IFileProcessor cvsFileProcessor;
    @PostMapping(value="/v1/enrich")
    public ResponseEntity<StreamingResponseBody> enrich(HttpServletRequest requestEntity) throws IOException {

        InputStream in = requestEntity.getInputStream();

        logger.info("Processing Trade details");

        String headerValue = "attachment; filename=\"trade.csv\"";
        StreamingResponseBody responseBody = response -> {
            cvsFileProcessor.process(in, response);
        };

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(responseBody);
    }

}

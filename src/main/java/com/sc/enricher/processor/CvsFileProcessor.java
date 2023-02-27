package com.sc.enricher.processor;

import com.sc.enricher.service.ITradeEnrichmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class CvsFileProcessor implements IFileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CvsFileProcessor.class);

    @Value("${scb.tradeenrichment.batch:10000}")
    private int batchSize;

    @Autowired
    private ITradeEnrichmentService tradeEnrichmentService;

    @Override
    public void process(InputStream in, OutputStream response) throws IOException {
        logger.info("Processing File records");
        Reader reader = new InputStreamReader(in);

        try (BufferedReader br = new BufferedReader(reader)) {
            boolean eof = false;
            while (!eof) {
                List<String> batch = new ArrayList<>(batchSize);
                for (int i = 0; i < batchSize; i++) {
                    String line = br.readLine();
                    if (eof = line == null) break;
                    batch.add(line);
                }

                if(batch.size() != 0){
                    batch = tradeEnrichmentService.enrichTrades(batch);
                }

                int count = 0;
                for (String element : batch) {
                    count++;
                    if(eof && (count == batch.size())){
                        response.write((element).getBytes());
                    }
                    else{
                        response.write((element + "\n").getBytes());
                    }
                }
            }
        }
    }
}

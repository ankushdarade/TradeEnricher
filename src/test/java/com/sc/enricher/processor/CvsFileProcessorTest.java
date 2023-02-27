package com.sc.enricher.processor;

import com.sc.enricher.service.TradeEnrichmentService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.*;
import java.util.ArrayList;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

@SpringBootTest
public class CvsFileProcessorTest {

    @InjectMocks
    private CvsFileProcessor cvsFileProcessor;

    @Mock
    TradeEnrichmentService service;

    @Before
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);
        Mockito.when(service.enrichTrades(any())).thenReturn(new ArrayList<>());
        ReflectionTestUtils.setField(cvsFileProcessor, "batchSize", 5);
    }

    @Test
    public void test_process_batch_empty_file() throws IOException {
        FileInputStream input
                = new FileInputStream(new File("src/test/resources/trade_empty.csv"));
        OutputStream o = new ByteArrayOutputStream();
        cvsFileProcessor.process(input, o);
        Mockito.verify(service, times(0)).enrichTrades(any());
    }
    @Test
    public void test_process_batch_single() throws IOException {
        FileInputStream input
                = new FileInputStream(new File("src/test/resources/trade.csv"));
        OutputStream o = new ByteArrayOutputStream();
        cvsFileProcessor.process(input, o);
        Mockito.verify(service, times(1)).enrichTrades(any());
    }

    @Test
    public void test_process_batch_multi() throws IOException {
        FileInputStream input
                = new FileInputStream(new File("src/test/resources/trade1.csv"));
        OutputStream o = new ByteArrayOutputStream();
        cvsFileProcessor.process(input, o);
        Mockito.verify(service, times(3)).enrichTrades(any());
    }
}

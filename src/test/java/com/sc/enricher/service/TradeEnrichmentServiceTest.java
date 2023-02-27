package com.sc.enricher.service;

import com.sc.enricher.cache.ProductsCacheManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TradeEnrichmentServiceTest {

    private static final String HEADER = "date,product_id,currency,price";
    @InjectMocks
    private TradeEnrichmentService service;

    @Mock
    ProductsCacheManager productsProcessor;

    @Before
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);

        Mockito.when(productsProcessor.getProductName(1)).thenReturn("Product1");
        Mockito.when(productsProcessor.getProductName(2)).thenReturn("Product2");
        Mockito.when(productsProcessor.getProductName(3)).thenReturn("Product3");
        Mockito.when(productsProcessor.getProductName(11)).thenReturn("Missing Product Name");
    }
    @Test
    public void test_enrichTrades()  {
        List<String> list = new ArrayList<>();
        list.add(HEADER);
        list.add("20160101,1,EUR,10.0");
        list.add("20160101,2,EUR,20.1");
        list.add("20160101,3,EUR,30.34");
        list.add("20160101,11,EUR,35.34");

        list = service.enrichTrades(list);

        Assert.assertEquals(list.size(), 5);
        Assert.assertEquals(HEADER, list.get(0));
        Assert.assertEquals("20160101,Product1,EUR,10.0", list.get(1));
        Assert.assertEquals("20160101,Product2,EUR,20.1", list.get(2));
        Assert.assertEquals("20160101,Product3,EUR,30.34", list.get(3));
        Assert.assertEquals("20160101,Missing Product Name,EUR,35.34", list.get(4));
    }

    @Test
    public void test_enrichTrades_invalid_date()  {
        List<String> list = new ArrayList<>();
        list.add(HEADER);
        list.add("2016asd0101,1,EUR,10.0");
        list.add("20160101,2,EUR,20.1");
        list.add("20160asd101,3,EUR,30.34");
        list.add("20160101,11,EUR,35.34");

        list = service.enrichTrades(list);

        Assert.assertEquals(list.size(), 3);
        Assert.assertEquals(HEADER, list.get(0));
        Assert.assertEquals("20160101,Product2,EUR,20.1", list.get(1));
        Assert.assertEquals("20160101,Missing Product Name,EUR,35.34", list.get(2));
    }

    @Test
    public void test_enrichTrades_noTrades()  {
        List<String> list = new ArrayList<>();
        list = service.enrichTrades(list);
        Assert.assertEquals(list.size(), 0);
    }

    @Test
    public void test_enrichTrades_onlyheader()  {
        List<String> list = new ArrayList<>();
        list.add(HEADER);
        list = service.enrichTrades(list);
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(HEADER, list.get(0));
    }

}

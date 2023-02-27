package com.sc.enricher.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

@SpringBootTest
public class ProductsCacheManagerTest {

    @InjectMocks
    private ProductsCacheManager productsCacheManager;

    @Before
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(productsCacheManager, "maxCacheSize", 5);
        productsCacheManager.initialize();
    }

    @Test
    public void test_getProductName() throws IOException {
        String name = productsCacheManager.getProductName(5);
        Assert.assertEquals("OTC Index Options", name);

        name = productsCacheManager.getProductName(1);
        Assert.assertEquals("Treasury Bills Domestic", name);

        name = productsCacheManager.getProductName(11);
        Assert.assertEquals("Missing Product Name", name);
    }
}

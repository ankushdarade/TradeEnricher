package com.sc.enricher.cache;

import com.sc.enricher.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Component
public class ProductsCacheManager {

    public static final String MISSING_PRODUCT_NAME = "Missing Product Name";
    private static final Logger logger = LoggerFactory.getLogger(ProductsCacheManager.class);
    private Map<Integer, Product> products = new HashMap<>();

    @Value("${scb.tradeenrichment.product.cache.max:100000}")
    private long maxCacheSize;

    public String getProductName(Integer id) {
        try{

            Product product = Optional.ofNullable(products.get(id)).orElseGet(() -> {
                try {
                    return searchInFile(id);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            if(product == null){
                if(products.keySet().size() < maxCacheSize){
                    products.put(id, new Product(id, MISSING_PRODUCT_NAME));
                }

                return MISSING_PRODUCT_NAME;
            }
            else{
                return product.getName();
            }
        }
        catch(Exception ex){
            return MISSING_PRODUCT_NAME;
        }
    }

    private Product searchInFile(Integer id) throws IOException {
        {
            logger.info("searchInFile for product id "+id);
            FileInputStream inputStream = null;
            Scanner sc = null;
            try {
                File file = ResourceUtils.getFile("classpath:product.csv");
                inputStream = new FileInputStream(file);

                sc = new Scanner(inputStream, "UTF-8");

                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    if(line.contains(id+",")){
                        String[] pr = line.split(",");
                        return new Product(id, pr[1]);
                    }
                }
                // note that Scanner suppresses exceptions
                if (sc.ioException() != null) {
                    throw sc.ioException();
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (sc != null) {
                    sc.close();
                }
            }
            return null;
        }
    }

    @PostConstruct
    public void initialize() throws IOException {
        logger.info("Initialize product cache");

        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            File file = ResourceUtils.getFile("classpath:product.csv");
            inputStream = new FileInputStream(file);

            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] pr = line.split(",");
                try {
                    Integer productId = Integer.parseInt(pr[0]);
                    products.put(productId, new Product(productId, pr[1]));
                    if(products.keySet().size() >= maxCacheSize){
                        break;
                    }
                }
                catch(Exception ex){
                    logger.warn("Exception while processing product record "+line);
                }
            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
    }
}
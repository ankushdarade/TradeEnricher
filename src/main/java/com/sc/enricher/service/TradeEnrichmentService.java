package com.sc.enricher.service;

import com.sc.enricher.cache.ProductsCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @TradeEnrichmentService - Service for Validating and Enriching Trade details
 */
@Service
public class TradeEnrichmentService implements ITradeEnrichmentService {

    private static final String CSV_DELIMITER = ",";
    private static final String DATE = "date";
    private static final Logger logger = LoggerFactory.getLogger(TradeEnrichmentService.class);

    @Autowired
    ProductsCacheManager productsProcessor;

    /**
     * This method Validates and Enriches Trade details
     * @param trades
     * @return List<String> - Enriched Trade Details
     */
    @Override
    public List<String> enrichTrades(List<String> trades) {
        //logger.info("Validating and Enriching Trade details");
        List<String> enrichedTrades = new ArrayList<>(trades.size());
        try {
            for (String trade : trades) {
                String[] tr = trade.split(CSV_DELIMITER);

                if (trade.startsWith(DATE)) {
                    enrichedTrades.add(trade);
                }
                else if (validateDateFormat(tr[0])) {
                    try {
                        enrichedTrades.add(trade.replace(CSV_DELIMITER + tr[1] + CSV_DELIMITER, CSV_DELIMITER + productsProcessor.getProductName(Integer.parseInt(tr[1])) + CSV_DELIMITER));
                    } catch (Exception ex) {
                        logger.error("Exception while parsing Trade details " + trade);
                    }
                } else {
                    logger.error("Date Invalid for Trade " + trade);
                }
            }
        }
        catch(Exception ex){
            logger.error("Exception while Validating and Enriching Trades", ex);
        }

        return enrichedTrades;
    }

    /**
     * Validates Date Format "YYYYMMDD"
     * @param input
     * @return boolean
     */
    private boolean validateDateFormat(String input) {
        if(input == null) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("YYYYMMDD");
        try {
            Date d = format.parse(input);
            return true;
        } catch(ParseException e) {
            return false;
        }
    }
}

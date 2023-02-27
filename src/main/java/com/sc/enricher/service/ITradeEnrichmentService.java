package com.sc.enricher.service;

import java.util.List;

public interface ITradeEnrichmentService {
    public List<String> enrichTrades(List<String> trades);
}

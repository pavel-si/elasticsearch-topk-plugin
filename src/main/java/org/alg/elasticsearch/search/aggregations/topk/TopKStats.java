package org.alg.elasticsearch.search.aggregations.topk;

import org.elasticsearch.search.aggregations.Aggregation;

import java.util.Map;

/**
 * Interface for TopKStats aggregation
 */
public interface TopKStats extends Aggregation {
    /** return the total document count */
    long getDocCount();
    /** return total field count (differs from docCount if there are missing values) */
    long getFieldCount(String field);
    /** return K */
    long getK();
    /** return top K property-value pairs */
    Map<String, Long> getTopK();
}

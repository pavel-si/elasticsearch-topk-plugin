package org.alg.elasticsearch.search.aggregations.topk;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregatorFactories;
import org.elasticsearch.search.aggregations.AggregatorFactory;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValuesSource;
import org.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.Map;

public class TopKBuilder extends AggregationBuilder {

    public static String ARTIFACT_ID = "topk-aggregation";
    public static String NAME = "topk";

    private String field;
    private Number size;
    private Number capacity;

    @Override
    protected AggregatorFactory<?> build(SearchContext context, AggregatorFactory<?> parent) throws IOException {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public AggregationBuilder setMetaData(Map<String, Object> metaData) {
        return null;
    }

    @Override
    public AggregationBuilder subAggregation(AggregationBuilder aggregation) {
        return null;
    }

    @Override
    public AggregationBuilder subAggregation(PipelineAggregationBuilder aggregation) {
        return null;
    }

    @Override
    public AggregationBuilder subAggregations(AggregatorFactories.Builder subFactories) {
        return null;
    }

    protected XContentBuilder internalXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (field != null) {
            builder.field("field", field);
        }
        if (size != null) {
            builder.field("size", size);
        }
        if (capacity != null) {
            builder.field("capacity", capacity);
        }
        return builder.endObject();
    }

    public TopKBuilder field(String field) {
        this.field = field;
        return this;
    }

    public TopKBuilder size(Number size) {
        this.size = size;
        return this;
    }

    public TopKBuilder capacity(Number capacity) {
        this.capacity = capacity;
        return this;
    }

    public TopKBuilder(StreamInput in) throws IOException {
        super(TopKBuilder.NAME);
    }

    @Override
    public String getWriteableName() {
        return TopKBuilder.NAME;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        return null;
    }
}

package org.alg.elasticsearch.search.aggregations.topk;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregatorFactories;
import org.elasticsearch.search.aggregations.AggregatorFactory;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.internal.SearchContext;

public class TopKBuilder extends AggregationBuilder {
    
    private String field;
    private Number size;
    private Number capacity;

    @Override
    protected AggregatorFactory<?> build(SearchContext context, AggregatorFactory<?> parent) throws IOException {
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
        super(InternalTopK.TYPENAME);
//        this.someValue = in.readVInt();
//        this.someMap = in.readMapOfLists(StreamInput::readString, StreamInput::readString);
    }

    @Override
    public String getWriteableName() {
        return InternalTopK.TYPENAME;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        return null;
    }
}

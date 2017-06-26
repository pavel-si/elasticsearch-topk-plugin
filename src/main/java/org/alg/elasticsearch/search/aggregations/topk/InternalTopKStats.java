package org.alg.elasticsearch.search.aggregations.topk;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.InternalAggregation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class InternalTopKStats  extends InternalAggregation implements TopKStats {

    public InternalTopKStats(StreamInput in) throws IOException {
        super(in);
        // TODO
    }

    @Override
    public boolean isFragment() {
        return false;
    }

    @Override
    public long getDocCount() {
        return 0;
    }

    @Override
    public long getFieldCount(String field) {
        return 0;
    }

    @Override
    public long getK() {
        return 0;
    }

    @Override
    public Map<String, Long> getTopK() {
        return null;
    }

    @Override
    protected void doWriteTo(StreamOutput out) throws IOException {

    }

    @Override
    public InternalAggregation doReduce(List<InternalAggregation> aggregations, ReduceContext reduceContext) {
        return null;
    }

    @Override
    public Object getProperty(List<String> path) {
        return null;
    }

    @Override
    public XContentBuilder doXContentBody(XContentBuilder builder, Params params) throws IOException {
        return null;
    }

    @Override
    public String getWriteableName() {
        return null;
    }
}

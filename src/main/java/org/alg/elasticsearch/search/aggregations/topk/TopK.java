package org.alg.elasticsearch.search.aggregations.topk;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.InternalAggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface TopK extends MultiBucketsAggregation {

    static class Bucket implements MultiBucketsAggregation.Bucket {
        private final String term;
        private final long count;
        final int bucketOrd;
        InternalAggregations aggregations;
        
        public Bucket(String term, long count, int bucketOrd, InternalAggregations aggregations) {
            this.term = term;
            this.count = count;
            this.bucketOrd = bucketOrd;
            this.aggregations = aggregations;
        }
        
        @Override
        public String getKey() {
            return term;
        }

        @Override
        public String getKeyAsString() {
            return term;
        }

        @Override
        public long getDocCount() {
            return count;
        }

        @Override
        public Aggregations getAggregations() {
            return aggregations;
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
//            builder.field(CommonFields.KEY, term);
//            builder.field(CommonFields.DOC_COUNT, count);
            aggregations.toXContentInternal(builder, params);
            builder.endObject();
            return builder;
        }
        
        public void writeTo(StreamOutput out) throws IOException {
            out.writeString(term);
            out.writeLong(count);
            out.writeInt(bucketOrd);
            if (aggregations != null) {
                out.writeBoolean(true);
                aggregations.writeTo(out);
            } else {
                out.writeBoolean(false);
            }
        }
        
        static TopK.Bucket readFrom(StreamInput in) throws IOException {
            String term = in.readString();
            long count = in.readLong();
            int bucketOrd = in.readInt();
            return new TopK.Bucket(term, count, bucketOrd, InternalAggregations.readOptionalAggregations(in));
        }
    }

    List<? extends MultiBucketsAggregation.Bucket> getBuckets();

    Bucket getBucketByKey(String term);

    @Override
    default String getName() {
        return null;
    }

    @Override
    default Map<String, Object> getMetaData() {
        return null;
    }
}

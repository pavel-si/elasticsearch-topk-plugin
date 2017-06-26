package org.alg.elasticsearch.search.aggregations.topk;

import com.clearspring.analytics.stream.StreamSummary;
import com.clearspring.analytics.util.Pair;
import org.apache.lucene.index.LeafReaderContext;
import org.elasticsearch.common.lease.Releasables;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.common.util.ObjectArray;
import org.elasticsearch.index.fielddata.SortedBinaryDocValues;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.SingleBucketAggregator;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.elasticsearch.search.aggregations.support.ValuesSource;
import org.elasticsearch.search.aggregations.support.ValuesSourceAggregatorFactory;
import org.elasticsearch.search.aggregations.support.ValuesSourceConfig;
import org.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 *
 */
public class TopKAggregator extends SingleBucketAggregator {
    
    private ValuesSource.Bytes valuesSource;
    private SortedBinaryDocValues values;
    
    static class Term implements Comparable<Term>, Serializable {
        private static final long serialVersionUID = 9135396685987711497L;

        Term(String term, int bucketOrd) {
            this.term = term;
            this.bucketOrd = bucketOrd;
        }
        Term(String term) {
            this(term, -1);
        }
        String term;
        int bucketOrd;
        
        @Override
        public int compareTo(Term o) {
            return term.compareTo(o.term);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Term) {
                return ((Term) obj).term.equals(term);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return term.hashCode();
        }
    }
    
    private final Number size;

    // Since the sub-aggregations need to be collected
    // before the final top-k have been computed we'll store
    // `capacity` Term in the `StreamSummary` associated to `capacity` `TopK.Bucket`
    // and hope the data is skewed enough to reassign the bucketOrd between terms
    // without any major accuracy issue
    private final Number capacity;

    private BigArrays bigArrays;
    private ObjectArray<Stack<Integer>> bucketOrds;
    private ObjectArray<StreamSummary<Term>> summaries;
    private ObjectArray<Map<String, Integer>> termToBucket;

    public TopKAggregator(String name, Number size, Number capacity, AggregatorFactories factories, long estimatedBucketsCount, ValuesSource.Bytes
            valuesSource, SearchContext aggregationContext, Aggregator parent) throws IOException {
        super(name, factories, aggregationContext, parent, null, null);
        this.size = size;
        this.capacity = capacity;
        this.valuesSource = valuesSource;
        if (valuesSource != null) {
            final long initialSize = estimatedBucketsCount < 2 ? 1 : estimatedBucketsCount;
            this.bigArrays = new BigArrays(new Settings(Collections.emptyMap(), null), null); // TODO settings etc
            this.summaries = bigArrays.newObjectArray(initialSize);
            this.bucketOrds = bigArrays.newObjectArray(initialSize);
            this.termToBucket = bigArrays.newObjectArray(initialSize);
        }
    }

    public void collect(int doc, long owningBucketOrdinal) throws IOException {
        assert this.valuesSource != null : "should collect first";
        
        this.summaries = bigArrays.grow(this.summaries, owningBucketOrdinal + 1);
        this.bucketOrds = bigArrays.grow(this.bucketOrds, owningBucketOrdinal + 1);
        this.termToBucket = bigArrays.grow(this.termToBucket, owningBucketOrdinal + 1);

        StreamSummary<Term> summary = this.summaries.get(owningBucketOrdinal);
        if (summary == null) {
            summary = new StreamSummary<>(capacity.intValue());
            this.summaries.set(owningBucketOrdinal, summary);
        }
        Stack<Integer> bucketOrds = this.bucketOrds.get(owningBucketOrdinal);
        if (bucketOrds == null) {
            bucketOrds = new Stack<>();
            for (int i = 0; i < capacity.intValue(); ++i) {
                bucketOrds.push(i);
            }
            this.bucketOrds.set(owningBucketOrdinal, bucketOrds);
        }
        Map<String, Integer> termToBucket = this.termToBucket.get(owningBucketOrdinal);
        if (termToBucket == null) {
            termToBucket = new HashMap<>();
            this.termToBucket.set(owningBucketOrdinal, termToBucket);
        }
        values.setDocument(doc);
        final int valuesCount = values.count();
        for (int i = 0; i < valuesCount; i++) {
            // store the term
            Term t = new Term(values.valueAt(i).utf8ToString());
            Pair<Boolean, Term> dropped = summary.offerReturnAll(t, 1);
            
            if (dropped.right != null) { // one item has been removed from summary
                // XXX: recycle bucketOrd (yes, we reuse wrongly aggregated values)
                termToBucket.remove(dropped.right.term);
                if (dropped.left) { // new item
                    t.bucketOrd = dropped.right.bucketOrd; // recycle
                    termToBucket.put(t.term, t.bucketOrd);
                } else { // existing item
                    bucketOrds.push(dropped.right.bucketOrd); // recycle
                    assert termToBucket.containsKey(t.term);
                    t.bucketOrd = termToBucket.get(t.term);
                }
            } else {
                // assign a bucketOrd
                if (dropped.left) { // new item
                    assert this.bucketOrds.size() > 0;
                    t.bucketOrd = bucketOrds.pop();
                    termToBucket.put(t.term, t.bucketOrd);
                } else { // existing item
                    t.bucketOrd = termToBucket.get(t.term);
                }
            }
            
            // collect sub aggregations
            assert t.bucketOrd != -1;
            collectBucket(subCollector, doc, t.bucketOrd); // TODO where is the subCollector
        }
    }

    @Override
    public InternalAggregation buildAggregation(long owningBucketOrdinal) throws IOException {
        StreamSummary<Term> summary = summaries == null || owningBucketOrdinal >= summaries.size() ? null : summaries.get(owningBucketOrdinal);
        InternalTopK topk = new InternalTopK(name, size, summary);
        for (TopK.Bucket bucket : topk.getBuckets()) {
            bucket.aggregations = bucketAggregations(bucket.bucketOrd);
        }
        return topk;
    }

    @Override
    public InternalAggregation buildEmptyAggregation() {
        return new InternalTopK(name, size, null);
    }

    @Override
    protected LeafBucketCollector getLeafCollector(LeafReaderContext ctx, LeafBucketCollector sub) throws IOException {
        return null;
    }

    @Override
    public void doClose() {
        if (this.summaries != null) {
            Releasables.close(this.summaries);
        }
        if (this.bucketOrds != null) {
            Releasables.close(this.bucketOrds);
        }
        if (this.termToBucket != null) {
            Releasables.close(this.termToBucket);
        }
    }

    public static class Factory extends ValuesSourceAggregatorFactory<ValuesSource.Bytes, Factory> {
        private final Number size;
        private final Number capacity;

        public Factory(String name, ValuesSourceConfig<ValuesSource.Bytes> config, SearchContext context, AggregatorFactory<?> parent, AggregatorFactories.Builder subFactoriesBuilder, Map<String, Object> metaData, Number size, Number capacity) throws IOException {
            super(name, config, context, parent, subFactoriesBuilder, metaData);
            this.size = size;
            this.capacity = capacity;
        }

        @Override
        protected Aggregator createUnmapped(Aggregator parent, List list, Map metaData) throws IOException {
            return new TopKAggregator(name, size, capacity, factories, 0, null, parent.context(), parent);
        }

        @Override
        protected Aggregator doCreateInternal(ValuesSource.Bytes valuesSource, Aggregator parent, boolean collectsFromSingleBucket, List<PipelineAggregator> pipelineAggregators, Map<String, Object> metaData) throws IOException {
            return new TopKAggregator(name, size, capacity, factories, 10, valuesSource, parent.context(), parent);
            // TODO collectsFromSingleBucket set to ten, is there a better default?
        }
    }
}

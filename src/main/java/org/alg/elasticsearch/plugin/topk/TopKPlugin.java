package org.alg.elasticsearch.plugin.topk;

import org.alg.elasticsearch.search.aggregations.topk.TopKBuilder;
import org.alg.elasticsearch.search.aggregations.topk.TopKParser;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.SearchPlugin;

import java.util.Collections;
import java.util.List;

public class TopKPlugin extends Plugin implements SearchPlugin {

    public String name() {
        return "topk-aggregation";
    }

    public String description() {
        return "Top-K Aggregation for Elasticsearch";
    }

    @Override
    public List<AggregationSpec> getAggregations() {
        TopKParser parser = new TopKParser();
        return Collections.singletonList(new AggregationSpec("topk", TopKBuilder::new, parser));
    }

    /*
    public void onModule(AggregationModule module) {
        module.addAggregatorParser(TopKParser.class);
        InternalTopK.registerStreams();
    }
*/

}

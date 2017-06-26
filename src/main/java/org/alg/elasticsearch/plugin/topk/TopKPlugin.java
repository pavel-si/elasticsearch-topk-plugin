package org.alg.elasticsearch.plugin.topk;

import org.alg.elasticsearch.search.aggregations.topk.InternalTopKStats;
import org.alg.elasticsearch.search.aggregations.topk.TopKBuilder;
import org.alg.elasticsearch.search.aggregations.topk.TopKParser;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.SearchPlugin;

import java.util.Collections;
import java.util.List;

public class TopKPlugin extends Plugin implements SearchPlugin {

    private final TopKPluginConfiguration config;

    public TopKPlugin(Settings settings) {
        Environment environment = new Environment(settings);
        config = new TopKPluginConfiguration(environment);
    }

    public String description() {
        return "Top-K Aggregation for Elasticsearch";
    }

    @Override
    public List<AggregationSpec> getAggregations() {
        return Collections.singletonList(new AggregationSpec(TopKBuilder.NAME, TopKBuilder::new, new TopKParser())
                .addResultReader(InternalTopKStats::new)); // aggregation of results across shards
    }
}

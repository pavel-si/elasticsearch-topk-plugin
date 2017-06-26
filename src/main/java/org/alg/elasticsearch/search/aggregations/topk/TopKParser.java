package org.alg.elasticsearch.search.aggregations.topk;

import java.io.IOException;

import org.elasticsearch.common.ParsingException;
import org.elasticsearch.common.xcontent.XContentLocation;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.mapper.FieldMapper;
import org.elasticsearch.index.query.QueryParseContext;
import org.elasticsearch.search.SearchParseException;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.support.FieldContext;
import org.elasticsearch.search.aggregations.support.ValuesSource;
import org.elasticsearch.search.aggregations.support.ValuesSourceConfig;

public class TopKParser implements Aggregator.Parser {

    @Override
    public AggregationBuilder parse(String aggregationName, QueryParseContext context) throws IOException {
        // TODO config
        /*ValuesSourceConfig<ValuesSource.Bytes> config = new ValuesSourceConfig<>(ValuesSource.Bytes.class);*/
        
        String field = null;
        Number size = null;
        Number capacity = 1000;

        XContentParser.Token token;
        String currentFieldName = null;
        XContentParser parser = context.parser();
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            XContentLocation location = parser.getTokenLocation();
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token == XContentParser.Token.VALUE_STRING) {
                if ("field".equals(currentFieldName)) {
                    field = parser.text();
                } else {
                    throw new ParsingException(location,  "Unknown key for a " + token + " in [" + aggregationName + "]: [" + currentFieldName +
                            "].");
                }
            } else if (token == XContentParser.Token.VALUE_NUMBER) {
                if ("size".equals(currentFieldName)) {
                    size = parser.numberValue();
                } else if ("capacity".equals(currentFieldName)) {
                        capacity = parser.numberValue();
                } else {
                    throw new ParsingException(location, "Unknown key for a " + token + " in [" + aggregationName + "]: [" + currentFieldName + "].");
                }
            } else {
                throw new ParsingException(location, "Unexpected token " + token + " in [" + aggregationName + "].");
            }
        }

        if (field == null) {
            throw new ParsingException(parser.getTokenLocation(), "Key 'field' cannot be null.", null);
        }
        if (size == null) {
            throw new ParsingException(parser.getTokenLocation(), "Key 'size' cannot be null.", null);
        }

        /*FieldMapper<?> mapper = context.parser().smartNameFieldMapper(field);
        if (mapper == null) {
            config.unmapped(true);
            return new TopKBuilder().field(aggregationName).size(size).capacity(capacity);
        }
        config.fieldContext(new FieldContext(field, context.fieldData().getForField(mapper), mapper));
        */
        // TODO where is the in stream?
        return new TopKBuilder(in).field(aggregationName).size(size).capacity(capacity); // TODO should we pass config in??
    }
}

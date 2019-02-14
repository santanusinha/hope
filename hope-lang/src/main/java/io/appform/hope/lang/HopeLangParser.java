package io.appform.hope.lang;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.hope.core.Evaluatable;
import io.appform.hope.core.functions.FunctionRegistry;
import io.appform.hope.core.visitors.Evaluator;
import io.appform.hope.lang.parser.HopeParser;

import java.io.StringReader;

/**
 *
 */
public class HopeLangParser {
    private final FunctionRegistry functionRegistry;

    public HopeLangParser() {
        this(new FunctionRegistry());
    }

    public HopeLangParser(FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
        this.functionRegistry.discover();
    }

    public Evaluatable parse(final String payload) throws Exception {
        return new HopeParser(new StringReader(payload)).parse(functionRegistry);
    }

    public boolean evaluate(Evaluatable rule, JsonNode node) {
        return new Evaluator().evaluate(rule, node);
    }
}

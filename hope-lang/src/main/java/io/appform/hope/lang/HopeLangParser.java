package io.appform.hope.lang;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.hope.core.Evaluatable;
import io.appform.hope.core.functions.FunctionRegistry;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.visitors.Evaluator;
import io.appform.hope.lang.parser.HopeParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class HopeLangParser {
    private final FunctionRegistry functionRegistry;

    private HopeLangParser(FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }

    public Evaluatable parse(final String payload) throws Exception {
        return new HopeParser(new StringReader(payload)).parse(functionRegistry);
    }

    public boolean evaluate(Evaluatable rule, JsonNode node) {
        return new Evaluator().evaluate(rule, node);
    }

    public static class Builder {
        private final List<String> userPackages = new ArrayList<>();
        private final FunctionRegistry functionRegistry = new FunctionRegistry();
        private Builder addPackage(final String userPackage) {
            userPackages.add(userPackage);
            return this;
        }

        public Builder registerFunction(Class<? extends HopeFunction> hopeFunctionClass) {
            functionRegistry.register(hopeFunctionClass);
            return this;
        }

        public HopeLangParser build() {
            functionRegistry.discover(userPackages);
            return new HopeLangParser(functionRegistry);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}

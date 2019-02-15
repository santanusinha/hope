package io.appform.hope.lang;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.hope.core.Evaluatable;
import io.appform.hope.core.exceptions.errorstrategy.DefaultErrorHandlingStrategy;
import io.appform.hope.core.exceptions.errorstrategy.ErrorHandlingStrategy;
import io.appform.hope.core.exceptions.impl.HopeExpressionParserError;
import io.appform.hope.core.functions.FunctionRegistry;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.visitors.Evaluator;
import io.appform.hope.lang.parser.HopeParser;
import io.appform.hope.lang.parser.ParseException;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class HopeLangParser {
    private final FunctionRegistry functionRegistry;
    private final ErrorHandlingStrategy errorHandlingStrategy;

    private HopeLangParser(
            FunctionRegistry functionRegistry,
            ErrorHandlingStrategy errorHandlingStrategy) {
        this.functionRegistry = functionRegistry;
        this.errorHandlingStrategy = errorHandlingStrategy;
    }

    public Evaluatable parse(final String payload) throws HopeExpressionParserError {
        try {
            return new HopeParser(new StringReader(payload)).parse(functionRegistry);
        }
        catch (ParseException e) {
            throw new HopeExpressionParserError(e.getMessage());
        }
    }

    public boolean evaluate(Evaluatable rule, JsonNode node) {
        return new Evaluator(errorHandlingStrategy).evaluate(rule, node);
    }

    public static class Builder {
        private final List<String> userPackages = new ArrayList<>();
        private final FunctionRegistry functionRegistry = new FunctionRegistry();
        private ErrorHandlingStrategy errorHandlingStrategy = new DefaultErrorHandlingStrategy();

        public Builder addPackage(final String userPackage) {
            userPackages.add(userPackage);
            return this;
        }

        public Builder registerFunction(Class<? extends HopeFunction> hopeFunctionClass) {
            functionRegistry.register(hopeFunctionClass);
            return this;
        }

        public Builder errorHandlingStrategy(ErrorHandlingStrategy errorHandlingStrategy) {
            this.errorHandlingStrategy = errorHandlingStrategy;
            return this;
        }

        public HopeLangParser build() {
            functionRegistry.discover(userPackages);
            return new HopeLangParser(functionRegistry, errorHandlingStrategy);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}

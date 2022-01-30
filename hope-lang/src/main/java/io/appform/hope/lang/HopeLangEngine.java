/*
 * Copyright 2019. Santanu Sinha
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */

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
import lombok.extern.slf4j.Slf4j;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Top level accessor for hope. Creation is expensive. Create and reuse.
 */
@Slf4j
public class HopeLangEngine {
    private final FunctionRegistry functionRegistry;
    private final ErrorHandlingStrategy errorHandlingStrategy;

    private HopeLangEngine(
            FunctionRegistry functionRegistry,
            ErrorHandlingStrategy errorHandlingStrategy) {
        this.functionRegistry = functionRegistry;
        this.errorHandlingStrategy = errorHandlingStrategy;
    }

    /**
     * Evaluates a hope expression using the provided json to return true or false.
     *
     * @param hopeLangExpression A hope language expression
     * @param root               The json node to be evaluated
     * @return true in  case of match
     */
    public boolean evaluate(final String hopeLangExpression, JsonNode root) {
        final Evaluatable evaluatable = parse(hopeLangExpression);
        return evaluate(evaluatable, root);
    }

    /**
     * Parse a hope lang string. The resultant parsed rule can be reused for multiple evaluations.
     *
     * @param hopeLangExpression Parse a string
     * @return An evaluatable expression tree
     * @throws HopeExpressionParserError
     */
    public Evaluatable parse(final String hopeLangExpression) throws HopeExpressionParserError {
        try {
            return new HopeParser(new StringReader(hopeLangExpression)).parse(functionRegistry);
        }
        catch (Exception e) {
            throw new HopeExpressionParserError(e.getMessage());
        }
    }

    /**
     * Evaluate a hope lang parsed expression
     *
     * @param rule Parsed rule
     * @param node JsonNode for which the match rule is to be evaluated
     * @return true in case of match
     */
    public boolean evaluate(Evaluatable rule, JsonNode node) {
        return new Evaluator(errorHandlingStrategy).evaluate(rule, node);
    }

    public static class Builder {
        private final List<String> userPackages = new ArrayList<>();
        private final FunctionRegistry functionRegistry = new FunctionRegistry();
        private ErrorHandlingStrategy errorHandlingStrategy = new DefaultErrorHandlingStrategy();

        private Builder() {}

        /**
         * Add a package that will be scanned besides stdlib for implementations of {@link HopeFunction}
         *
         * @param userPackage package to be scanned
         * @return builder
         */
        public Builder addPackage(final String userPackage) {
            userPackages.add(userPackage);
            return this;
        }

        /**
         * Register a {@link HopeFunction} implementation directly to the function registry
         *
         * @param hopeFunctionClass Implementation of {@link HopeFunction}.
         *                          Needs to be annotated with {@link io.appform.hope.core.functions.FunctionImplementation}
         *                          and have a constructor only having zero or more {@link io.appform.hope.core.Value}
         *                          as params.
         * @return builder
         */
        public Builder registerFunction(Class<? extends HopeFunction> hopeFunctionClass) {
            functionRegistry.register(hopeFunctionClass);
            return this;
        }

        /**
         * Override error handling strategy. Default is {@link DefaultErrorHandlingStrategy}.
         * Can also be {@link io.appform.hope.core.exceptions.errorstrategy.InjectValueErrorHandlingStrategy}
         * or something custom.
         *
         * @param errorHandlingStrategy Error handling strategy
         * @return builder
         */
        public Builder errorHandlingStrategy(ErrorHandlingStrategy errorHandlingStrategy) {
            this.errorHandlingStrategy = errorHandlingStrategy;
            return this;
        }

        /**
         * Build a Hope language parser
         *
         * @return a fully initialized immutable parser
         */
        public HopeLangEngine build() {
            functionRegistry.discover(userPackages);
            return new HopeLangEngine(functionRegistry, errorHandlingStrategy);
        }
    }

    /**
     * Create a builder for the parser.
     *
     * @return An initialized builder.
     */
    public static Builder builder() {
        return new Builder();
    }

}

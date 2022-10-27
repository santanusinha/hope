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

package io.appform.hope.core.visitors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import io.appform.hope.core.Evaluatable;
import io.appform.hope.core.VisitorAdapter;
import io.appform.hope.core.combiners.AndCombiner;
import io.appform.hope.core.combiners.OrCombiner;
import io.appform.hope.core.exceptions.errorstrategy.DefaultErrorHandlingStrategy;
import io.appform.hope.core.exceptions.errorstrategy.ErrorHandlingStrategy;
import io.appform.hope.core.operators.And;
import io.appform.hope.core.operators.Equals;
import io.appform.hope.core.operators.Greater;
import io.appform.hope.core.operators.GreaterEquals;
import io.appform.hope.core.operators.Lesser;
import io.appform.hope.core.operators.LesserEquals;
import io.appform.hope.core.operators.Not;
import io.appform.hope.core.operators.NotEquals;
import io.appform.hope.core.operators.Or;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.JsonPointerValue;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Evaluates a hope expression
 */
public class Evaluator {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private final ParseContext parseContext;
    @Getter
    private final ErrorHandlingStrategy errorHandlingStrategy;

    public Evaluator() {
        this(new DefaultErrorHandlingStrategy());
    }

    public Evaluator(ErrorHandlingStrategy errorHandlingStrategy) {
        this.errorHandlingStrategy = errorHandlingStrategy;
        parseContext = JsonPath.using(Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .options(Option.SUPPRESS_EXCEPTIONS)
                .build());

    }

    public boolean evaluate(Evaluatable evaluatable, JsonNode node) {
        return evaluatable.accept(new LogicEvaluator(new EvaluationContext(parseContext.parse(node), node, this)));
    }

    public List<Boolean> evaluate(final List<Evaluatable> evaluatables, final JsonNode node) {
        val logicEvaluator = new LogicEvaluator(new EvaluationContext(parseContext.parse(node), node, this));
        return evaluatables.stream()
                .map(evaluatable -> evaluatable.accept(logicEvaluator))
                .collect(Collectors.toList());
    }

    @Data
    @Builder
    public static class EvaluationContext {
        private final DocumentContext jsonContext;
        private final JsonNode rootNode;
        private final Evaluator evaluator;
        private final Map<String, JsonNode> jsonPathEvalCache = new HashMap<>(128);
        private final Map<String, JsonNode> jsonPointerEvalCache = new HashMap<>(128);
    }

    public static class LogicEvaluator extends VisitorAdapter<Boolean> {

        private final EvaluationContext evaluationContext;

        public LogicEvaluator(
                EvaluationContext evaluationContext) {
            super(() -> true);
            this.evaluationContext = evaluationContext;
        }

        public boolean evaluate(Evaluatable evaluatable) {
            return evaluatable.accept(this);
        }

        @Override
        public Boolean visit(AndCombiner andCombiner) {
            return andCombiner.getExpressions()
                    .stream()
                    .allMatch(expression -> expression.accept(new LogicEvaluator(evaluationContext)));
        }

        @Override
        public Boolean visit(OrCombiner orCombiner) {
            return orCombiner.getExpressions()
                    .stream()
                    .anyMatch(expression -> expression.accept(new LogicEvaluator(evaluationContext)));
        }

        @Override
        public Boolean visit(Equals equals) {
            final Object lhs = Converters.objectValue(evaluationContext, equals.getLhs(), null);
            final Object rhs = Converters.objectValue(evaluationContext, equals.getRhs(), null);
            return Objects.equals(lhs, rhs);
        }

        @Override
        public Boolean visit(NotEquals notEquals) {
            final Object lhs = Converters.objectValue(evaluationContext, notEquals.getLhs(), null);
            final Object rhs = Converters.objectValue(evaluationContext, notEquals.getRhs(), null);
            return !Objects.equals(lhs, rhs);
        }

        @Override
        public Boolean visit(Greater greater) {
            final Number lhs = Converters.numericValue(evaluationContext, greater.getLhs(), 0);
            final Number rhs = Converters.numericValue(evaluationContext, greater.getRhs(), 0);
            return lhs.doubleValue() > rhs.doubleValue();
        }

        @Override
        public Boolean visit(GreaterEquals greaterEquals) {
            final Number lhs = Converters.numericValue(evaluationContext, greaterEquals.getLhs(), 0);
            final Number rhs = Converters.numericValue(evaluationContext, greaterEquals.getRhs(), 0);
            return lhs.doubleValue() >= rhs.doubleValue();
        }

        @Override
        public Boolean visit(Lesser lesser) {
            final Number lhs = Converters.numericValue(evaluationContext, lesser.getLhs(), 0);
            final Number rhs = Converters.numericValue(evaluationContext, lesser.getRhs(), 0);
            return lhs.doubleValue() < rhs.doubleValue();
        }

        @Override
        public Boolean visit(LesserEquals lesserEquals) {
            final Number lhs = Converters.numericValue(evaluationContext, lesserEquals.getLhs(), 0);
            final Number rhs = Converters.numericValue(evaluationContext, lesserEquals.getRhs(), 0);
            return lhs.doubleValue() <= rhs.doubleValue();
        }

        @Override
        public Boolean visit(And and) {
            boolean lhs = Converters.booleanValue(evaluationContext, and.getLhs(), false);
            boolean rhs = Converters.booleanValue(evaluationContext, and.getRhs(), false);

            return lhs && rhs;
        }

        @Override
        public Boolean visit(Or or) {
            boolean lhs = Converters.booleanValue(evaluationContext, or.getLhs(), false);
            boolean rhs = Converters.booleanValue(evaluationContext, or.getRhs(), false);

            return lhs || rhs;
        }

        @Override
        public Boolean visit(Not not) {
            boolean operand = Converters.booleanValue(evaluationContext, not.getOperand(), false);
            return !operand;
        }

        @Override
        public Boolean visit(JsonPointerValue jsonPointerValue) {
            return null;
        }

    }

}

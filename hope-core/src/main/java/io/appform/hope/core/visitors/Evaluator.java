package io.appform.hope.core.visitors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.appform.hope.core.Evaluatable;
import io.appform.hope.core.VisitorAdapter;
import io.appform.hope.core.combiners.AndCombiner;
import io.appform.hope.core.combiners.OrCombiner;
import io.appform.hope.core.operators.*;
import io.appform.hope.core.values.*;

import java.util.Objects;

/**
 *
 */
public class Evaluator {
    private static final ObjectMapper mapper = new ObjectMapper();

    public boolean evaluate(Evaluatable evaluatable, JsonNode node) {
        try {
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(evaluatable));
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return evaluatable.accept(new LogicEvaluator(node));
    }

    private class LogicEvaluator extends VisitorAdapter<Boolean> {

        private final JsonNode node;

        public LogicEvaluator(JsonNode node) {
            super(true);
            this.node = node;
        }

        public boolean evaluate(Evaluatable evaluatable) {
            return evaluatable.accept(this);
        }

        @Override
        public Boolean visit(AndCombiner andCombiner) {
            return andCombiner.getExpressions()
                    .stream()
                    .allMatch(expression -> expression.accept(new LogicEvaluator(node)));
        }

        @Override
        public Boolean visit(OrCombiner orCombiner) {
            return orCombiner.getExpressions()
                    .stream()
                    .anyMatch(expression -> expression.accept(new LogicEvaluator(node)));
        }

        @Override
        public Boolean visit(And and) {
            return and.getLhs().getValue() && and.getRhs().getValue();
        }

        @Override
        public Boolean visit(Equals equals) {
            return Objects.equals(equals.getLhs().accept(new ObjectValueEvaluator(node)), equals.getRhs().accept(new ObjectValueEvaluator(
                    node)));
        }

        @Override
        public Boolean visit(Greater greater) {
            return greater.getLhs().getValue().doubleValue() > greater.getRhs().getValue().doubleValue();
        }

        @Override
        public Boolean visit(GreaterEquals greaterEquals) {
            return greaterEquals.getLhs().getValue().doubleValue() >= greaterEquals.getRhs().getValue().doubleValue(); //TODO
        }

        @Override
        public Boolean visit(Lesser lesser) {
            return lesser.getLhs().getValue().doubleValue() < lesser.getRhs().getValue().doubleValue();
        }

        @Override
        public Boolean visit(LesserEquals lesserEquals) {
            return lesserEquals.getLhs().getValue().doubleValue() <= lesserEquals.getRhs().getValue().doubleValue(); //TODO
        }

        @Override
        public Boolean visit(NotEquals notEquals) {
            return !Objects.equals(
                    notEquals.getLhs().accept(new ObjectValueEvaluator(node)),
                    notEquals.getRhs().accept(new ObjectValueEvaluator(node)));
        }

        @Override
        public Boolean visit(Or or) {
            return or.getLhs().getValue() || or.getRhs().getValue();
        }

    }

    private class ObjectValueEvaluator extends VisitorAdapter<Object> {
        private final JsonNode node;

        public ObjectValueEvaluator(JsonNode node) {
            super(null);
            this.node = node;
        }

        @Override
        public Object visit(JsonPathValue jsonPathValue) {
            final Object readValue = JsonPath.read(node.toString(), jsonPathValue.getPath());
            if(null != readValue) {
                final JsonNode extractedNode = mapper.valueToTree(readValue);
                if(extractedNode.isTextual()) {
                    return extractedNode.asText();
                }
                if(extractedNode.isBoolean()) {
                    return extractedNode.asBoolean();
                }
                if(extractedNode.isNumber()) {
                    return extractedNode.asDouble();
                }
                if(extractedNode.isPojo()) {
                    return extractedNode.isPojo();
                }
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public Object visit(ObjectValue objectValue) {
            return objectValue.getValue();
        }

        @Override
        public Object visit(NumericValue numericValue) {
            return numericValue.getValue();
        }

        @Override
        public Object visit(StringValue stringValue) {
            return stringValue.getValue();
        }

        @Override
        public Object visit(BooleanValue booleanValue) {
            return booleanValue.getValue();
        }

    }
}

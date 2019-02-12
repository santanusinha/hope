package io.appform.hope.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Strings;
import io.appform.hope.core.FunctionValue;
import io.appform.hope.core.TreeNode;
import io.appform.hope.core.Value;
import io.appform.hope.core.VisitorAdapter;
import io.appform.hope.core.functions.FunctionRegistry;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.values.*;
import io.appform.hope.core.visitors.Evaluator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 */
@Slf4j
public class Converters {
    public static String stringValue(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            String defaultValue) {
        return node.accept(new VisitorAdapter<String>(defaultValue) {
            @Override
            public String visit(JsonPathValue jsonPathValue) {
                final JsonNode value = evaluationContext.getJsonContext()
                        .read(jsonPathValue.getPath());
                if (value.isTextual()) {
                    return value.asText();
                }
                return defaultValue;
            }

            @Override
            public String visit(StringValue stringValue) {
                final String value = stringValue.getValue();
                if (null == value) {
                    final JsonPathValue pathValue = stringValue.getPathValue();
                    if (null != pathValue) {
                        return pathValue.accept(this);
                    }
                    final FunctionValue functionValue = stringValue.getFunction();
                    if (null != functionValue) {
                        return functionValue.accept(this);
                    }
                }
                return value;
            }

            @Override
            public String visit(FunctionValue functionValue) {
                return stringValue(evaluationContext, function(functionValue).apply(evaluationContext), defaultValue);
            }
        });
    }

    public static Number numericValue(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            Number defaultValue) {
        return node.accept(new VisitorAdapter<Number>(defaultValue) {
            @Override
            public Number visit(JsonPathValue jsonPathValue) {
                final JsonNode value = evaluationContext.getJsonContext()
                        .read(jsonPathValue.getPath());
                if (value.isNumber()) {
                    return value.asDouble();
                }
                return defaultValue;
            }

            @Override
            public Number visit(NumericValue numericValue) {
                final Number value = numericValue.getValue();
                if (null == value) {
                    final JsonPathValue pathValue = numericValue.getPathValue();
                    if (null != pathValue) {
                        return pathValue.accept(this);
                    }
                    final FunctionValue functionValue = numericValue.getFunction();
                    if (null != functionValue) {
                        return functionValue.accept(this);
                    }
                }
                return value;
            }

            @Override
            public Number visit(FunctionValue functionValue) {
                return numericValue(evaluationContext, function(functionValue).apply(evaluationContext), defaultValue);
            }
        });
    }

    public static Boolean booleanValue(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            boolean defaultValue) {
        return node.accept(new VisitorAdapter<Boolean>(defaultValue) {
            @Override
            public Boolean visit(JsonPathValue jsonPathValue) {
                final JsonNode value = evaluationContext.getJsonContext()
                        .read(jsonPathValue.getPath());
                if (value.isBoolean()) {
                    return value.asBoolean();
                }
                return super.visit(jsonPathValue);
            }

            @Override
            public Boolean visit(BooleanValue booleanValue) {
                final Boolean value = booleanValue.getValue();
                if (null == value) {
                    final JsonPathValue pathValue = booleanValue.getPathValue();
                    if (null != pathValue) {
                        return pathValue.accept(this);
                    }
                    final FunctionValue functionValue = booleanValue.getFunction();
                    if (null != functionValue) {
                        return functionValue.accept(this);
                    }
                }
                return value;
            }

            @Override
            public Boolean visit(FunctionValue functionValue) {
                return booleanValue(evaluationContext, function(functionValue).apply(evaluationContext), defaultValue);
            }
        });
    }

    public static Value jsonNodeToValue(JsonNode node) {
        if (node.isTextual()) {
            return new StringValue(node.asText());
        }
        if (node.isBoolean()) {
            return new BooleanValue(node.asBoolean());
        }
        if (node.isNumber()) {
            return new NumericValue(node.doubleValue());
        }
        if (node.isPojo()) {
            return new ObjectValue(node);
        }
        if (node.isArray()) {
            return new ArrayValue(StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(ArrayNode.class.cast(node)
                                                                .elements(), Spliterator.ORDERED),
                    false)
                                          .map(child -> jsonNodeToValue(node))
                                          .collect(Collectors.toList()));
        }
        throw new UnsupportedOperationException(node.getNodeType()
                                                        .name() + " is not supported");
    }

    public static List<Value> explodeArray(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            List<Value> defaultValue) {
        return node.accept(new VisitorAdapter<List<Value>>(defaultValue) {
            @Override
            public List<Value> visit(JsonPathValue jsonPathValue) {
                final JsonNode value = evaluationContext.getJsonContext()
                        .read(jsonPathValue.getPath());
                if (value.isArray()) {
                    return StreamSupport.stream(
                            Spliterators.spliteratorUnknownSize(
                                    ArrayNode.class.cast(value)
                                            .elements(),
                                    Spliterator.ORDERED),
                            false)
                            .map(Converters::jsonNodeToValue)
                            .collect(Collectors.toList());
                }
                return super.visit(jsonPathValue);
            }

            @Override
            public List<Value> visit(ArrayValue arrayValue) {
                final List<Value> value = arrayValue.getValue();
                if (null == value) {
                    final JsonPathValue pathValue = arrayValue.getPathValue();
                    if (null != pathValue) {
                        return pathValue.accept(this);
                    }
                    final FunctionValue functionValue = arrayValue.getFunction();
                    if (null != functionValue) {
                        return functionValue.accept(this);
                    }
                }
                return value;
            }

            @Override
            public List<Value> visit(FunctionValue functionValue) {
                return explodeArray(evaluationContext, function(functionValue).apply(evaluationContext), defaultValue);
            }
        });
    }

    public static List<Object> flatten(
            Evaluator.EvaluationContext evaluationContext,
            List<Value> values,
            Object defaultValue) {
        return values
                .stream()
                .map(value -> objectValue(evaluationContext, value, defaultValue))
                .collect(Collectors.toList());
    }

    public static List<Object> flattenArray(
            Evaluator.EvaluationContext evaluationContext,
            Value value,
            Object defaultValue) {
        return flatten(evaluationContext,
                       explodeArray(evaluationContext, value, Collections.emptyList()),
                       defaultValue);
    }

    public static String jsonPathValue(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            String defaultValue) {
        return node.accept(new VisitorAdapter<String>(defaultValue) {
            @Override
            public String visit(JsonPathValue jsonPathValue) {
                final String path = jsonPathValue.getPath();

                if (Strings.isNullOrEmpty(path)) {
                    final FunctionValue functionValue = jsonPathValue.getFunction();
                    return functionValue.accept(this);
                }
                return path;
            }

            @Override
            public String visit(FunctionValue functionValue) {
                return jsonPathValue(evaluationContext, function(functionValue).apply(evaluationContext), defaultValue);
            }
        });
    }

    public static Object objectValue(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            Object defaultValue) {
        return node.accept(new VisitorAdapter<Object>(defaultValue) {
            @Override
            public Object visit(JsonPathValue jsonPathValue) {
                final JsonNode extractedNode = evaluationContext.getJsonContext()
                        .read(jsonPathValue.getPath());
                if (null != extractedNode && ! extractedNode.isNull()) {
                    if (extractedNode.isTextual()) {
                        return extractedNode.asText();
                    }
                    if (extractedNode.isBoolean()) {
                        return extractedNode.asBoolean();
                    }
                    if (extractedNode.isNumber()) {
                        return extractedNode.asDouble();
                    }
                    if (extractedNode.isPojo()) {
                        return extractedNode.isPojo();
                    }
                }
                return defaultValue;
            }

            @Override
            public Object visit(ObjectValue objectValue) {
                return objectValue.getValue();
            }

            @Override
            public Object visit(NumericValue numericValue) {
                return numericValue(evaluationContext, numericValue, 0);
            }

            @Override
            public Object visit(StringValue stringValue) {
                return stringValue(evaluationContext, stringValue, "");
            }

            @Override
            public Object visit(BooleanValue booleanValue) {
                return booleanValue(evaluationContext, booleanValue, false);
            }

            @Override
            public Object visit(FunctionValue functionValue) {
                return objectValue(evaluationContext, function(functionValue).apply(evaluationContext), defaultValue);
            }
        });
    }

    private static HopeFunction function(FunctionValue functionValue) {
        final FunctionRegistry.FunctionMeta functionMeta = functionValue.getFunction();
        final List<Value> parameters = functionValue.getParameters();
        return createFunction(functionValue.getName(), functionMeta, parameters);
    }

    private static HopeFunction createFunction(
            String name,
            FunctionRegistry.FunctionMeta functionMeta,
            List<Value> parameters) {
        final List<Class<?>> paramTypes = functionMeta.getParamTypes();
        try {
            final Constructor<? extends HopeFunction> constructor = functionMeta.getFunctionClass()
                    .getDeclaredConstructor(paramTypes
                                                    .toArray(new Class<?>[paramTypes.size()]));
            log.info("Found constructor: {}", constructor);
            if (functionMeta.isArrayValue()) {
                return constructor
                        .newInstance(
                                new Object[]{parameters.toArray(new Value[parameters.size()])});
            }
            else {
                return constructor
                        .newInstance(
                                parameters.toArray(new Object[parameters.size()]));
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not create instance of function: '" + name + "'", e);
        }
    }
}

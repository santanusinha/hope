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

package io.appform.hope.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.base.Strings;
import io.appform.hope.core.TreeNode;
import io.appform.hope.core.Value;
import io.appform.hope.core.VisitorAdapter;
import io.appform.hope.core.exceptions.errorstrategy.ErrorHandlingStrategy;
import io.appform.hope.core.functions.FunctionRegistry;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.values.*;
import io.appform.hope.core.visitors.Evaluator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Evaluated {@link Value}, {@link JsonPathValue} and {@link HopeFunction} to leaf native types.
 */
@Slf4j
public class Converters {

    private Converters() {
    }

    /**
     * Evaluates a {@link TreeNode} to find eventual String value.
     *
     * @param evaluationContext Current eval context
     * @param node              Node to be evaluated
     * @param defaultValue      Default value if eval fails
     * @return Evaluated value on success, defaultValue or excption in case of failure depending on {@link ErrorHandlingStrategy}
     */
    public static String stringValue(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            String defaultValue) {
        final ErrorHandlingStrategy errorHandlingStrategy = evaluationContext.getEvaluator()
                .getErrorHandlingStrategy();
        return node.accept(new VisitorAdapter<String>(
                () -> errorHandlingStrategy.handleIllegalEval("String value eval", defaultValue)) {
            @Override
            public String visit(JsonPathValue jsonPathValue) {
                return extractNodeValue(jsonPathValue,
                                        evaluationContext,
                                        JsonNodeType.STRING,
                                        JsonNode::asText,
                                        defaultValue);
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

    /**
     * Evaluates a {@link TreeNode} to find eventual numeric value.
     *
     * @param evaluationContext Current eval context
     * @param node              Node to be evaluated
     * @param defaultValue      Default value if eval fails
     * @return Evaluated value on success, defaultValue or excption in case of failure depending on {@link ErrorHandlingStrategy}
     */
    public static Number numericValue(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            Number defaultValue) {
        final ErrorHandlingStrategy errorHandlingStrategy = evaluationContext.getEvaluator()
                .getErrorHandlingStrategy();
        return node.accept(new VisitorAdapter<Number>(() -> errorHandlingStrategy.handleIllegalEval("Number eval",
                                                                                                    defaultValue)) {
            @Override
            public Number visit(JsonPathValue jsonPathValue) {
                return extractNodeValue(jsonPathValue,
                                        evaluationContext,
                                        JsonNodeType.NUMBER,
                                        JsonNode::asDouble,
                                        defaultValue);

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


    /**
     * Evaluates a {@link TreeNode} to find eventual boolean value.
     *
     * @param evaluationContext Current eval context
     * @param node              Node to be evaluated
     * @param defaultValue      Default value if eval fails
     * @return Evaluated value on success, defaultValue or excption in case of failure depending on {@link ErrorHandlingStrategy}
     */
    public static Boolean booleanValue(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            boolean defaultValue) {
        final ErrorHandlingStrategy errorHandlingStrategy = evaluationContext.getEvaluator()
                .getErrorHandlingStrategy();
        return node.accept(new VisitorAdapter<Boolean>(
                () -> errorHandlingStrategy.handleIllegalEval("Boolean eval", defaultValue)) {
            @Override
            public Boolean visit(JsonPathValue jsonPathValue) {
                return extractNodeValue(jsonPathValue,
                                        evaluationContext,
                                        JsonNodeType.BOOLEAN,
                                        JsonNode::asBoolean,
                                        defaultValue);

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


    /**
     * Evaluates a {@link TreeNode} to find eventual array.
     *
     * @param evaluationContext Current eval context
     * @param node              Node to be evaluated
     * @param defaultValue      Default value if eval fails
     * @return Evaluated array on success, defaultValue or excption in case of failure depending on {@link ErrorHandlingStrategy}
     */
    public static List<Value> explodeArray(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            List<Value> defaultValue) {
        final ErrorHandlingStrategy errorHandlingStrategy = evaluationContext.getEvaluator()
                .getErrorHandlingStrategy();
        return node.accept(new VisitorAdapter<List<Value>>(() -> defaultValue) {
            @Override
            public List<Value> visit(JsonPathValue jsonPathValue) {
                final JsonNode value = evaluationContext.getJsonContext()
                        .at(toJsonPointer(jsonPathValue.getPath()));
                if(null == value || value.isNull() || value.isMissingNode()){
                    return errorHandlingStrategy.handleMissingValue(
                            jsonPathValue.getPath(),
                            defaultValue);
                }
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
                return errorHandlingStrategy.handleTypeMismatch(
                        jsonPathValue.getPath(),
                        JsonNodeType.ARRAY.name(),
                        value.getNodeType()
                                .name(),
                        defaultValue);
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

    /**
     * Flatten an {@link ArrayNode} into List of objects
     *
     * @param evaluationContext Current eval context
     * @param value             Value that evaluates to an array
     * @param defaultValue      Default value if eval fails
     * @return Evaluated list on success, defaultValue or excption in case of failure depending on {@link ErrorHandlingStrategy}
     */
    public static List<Object> flattenArray(
            Evaluator.EvaluationContext evaluationContext,
            Value value,
            Object defaultValue) {
        return arrayToObjectList(evaluationContext,
                                 explodeArray(evaluationContext, value, Collections.emptyList()),
                                 defaultValue);
    }


    /**
     * Evaluates a {@link TreeNode} to find eventual json path value.
     *
     * @param evaluationContext Current eval context
     * @param node              Node to be evaluated
     * @param defaultValue      Default value if eval fails
     * @return provided json path on success, defaultValue or excption in case of failure depending on {@link ErrorHandlingStrategy}
     */
    public static String jsonPathValue(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            String defaultValue) {
        return node.accept(new VisitorAdapter<String>(() -> defaultValue) {
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


    /**
     * Evaluates a {@link TreeNode} to find eventual object.
     *
     * @param evaluationContext Current eval context
     * @param node              Node to be evaluated
     * @param defaultValue      Default value if eval fails
     * @return Evaluated object on success, defaultValue or excption in case of failure depending on {@link ErrorHandlingStrategy}
     */
    public static Object objectValue(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            Object defaultValue) {
        final ErrorHandlingStrategy errorHandlingStrategy = evaluationContext.getEvaluator()
                .getErrorHandlingStrategy();
        return node.accept(new VisitorAdapter<Object>(() -> errorHandlingStrategy.handleIllegalEval("Object eval",
                                                                                                    defaultValue)) {
            @Override
            public Object visit(JsonPathValue jsonPathValue) {
                final JsonNode value = nodeForJsonPath(jsonPathValue, evaluationContext);
                if (null != value && !value.isNull() && !value.isMissingNode()) {
                    if (value.isTextual()) {
                        return value.asText();
                    }
                    if (value.isBoolean()) {
                        return value.asBoolean();
                    }
                    if (value.isNumber()) {
                        return value.asDouble();
                    }
                }
                return errorHandlingStrategy.handleMissingValue(jsonPathValue.getPath(), defaultValue);
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

    public static<T> T handleValue(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            Object defaultValue,
            RawTypeHandler<T> handler) {
        final ErrorHandlingStrategy errorHandlingStrategy = evaluationContext.getEvaluator()
                .getErrorHandlingStrategy();
        return node.accept(
                new VisitorAdapter<T>(() -> handler.handleObject(
                        errorHandlingStrategy.handleIllegalEval("Object eval", defaultValue))) {
            @Override
            public T visit(JsonPathValue jsonPathValue) {
                final JsonNode value = nodeForJsonPath(jsonPathValue, evaluationContext);
                if (null != value && !value.isNull() && !value.isMissingNode()) {
                    if (value.isTextual()) {
                        return handler.handleString(value.asText());
                    }
                    if (value.isBoolean()) {
                        return handler.handleBoolean(value.asBoolean());
                    }
                    if (value.isNumber()) {
                        return handler.handleNumber(value.asDouble());
                    }
                }
                return handler.handleObject(errorHandlingStrategy.handleMissingValue(jsonPathValue.getPath(), defaultValue));
            }

            @Override
            public T visit(ObjectValue objectValue) {
                return handler.handleObject(objectValue.getValue());
            }

            @Override
            public T visit(NumericValue numericValue) {
                return handler.handleNumber(numericValue(evaluationContext, numericValue, 0));
            }

            @Override
            public T visit(StringValue stringValue) {
                return handler.handleString(stringValue(evaluationContext, stringValue, ""));
            }

            @Override
            public T visit(BooleanValue booleanValue) {
                return handler.handleBoolean(booleanValue(evaluationContext, booleanValue, false));
            }

            @Override
            public T visit(FunctionValue functionValue) {
                return handler.handleObject(objectValue(evaluationContext,
                                                        function(functionValue).apply(evaluationContext), defaultValue));
            }
        });
    }

    private static HopeFunction function(FunctionValue functionValue) {
        final List<Value> parameters = functionValue.getParameters();
        return createFunction(functionValue.getName(),
                              functionValue.getSelectedConstructor(),
                              parameters);
    }

    private static HopeFunction createFunction(
            String name,
            FunctionRegistry.ConstructorMeta selectedConstructor,
            List<Value> parameters) {
        try {
            final Constructor<? extends HopeFunction> constructor = selectedConstructor.getConstructor();
            if (selectedConstructor.isHasVariableArgs()) {
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

    private static Value jsonNodeToValue(JsonNode node) {
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

    private static JsonNode nodeForJsonPath(
            JsonPathValue jsonPathValue,
            Evaluator.EvaluationContext evaluationContext) {
        final String path = jsonPathValue.getPath();
        final Map<String, JsonNode> jsonPathEvalCache = evaluationContext.getJsonPathEvalCache();
        final JsonNode existing = jsonPathEvalCache
                .getOrDefault(path, null);

        final JsonNode value;
        if (null == existing) {
            value = evaluationContext.getJsonContext()
                    .at(toJsonPointer(path));
            if(null == value) {
                jsonPathEvalCache.put(path, NullNode.getInstance());
            }
            else {
                jsonPathEvalCache.put(path, value);
            }
        }
        else {
            value = existing;
        }
        return value;
    }

    private static <T> T extractNodeValue(
            JsonPathValue jsonPathValue,
            Evaluator.EvaluationContext evaluationContext,
            JsonNodeType expectedType,
            Function<JsonNode, T> extractor,
            T defaultValue) {
        final JsonNode value = nodeForJsonPath(jsonPathValue, evaluationContext);
        final ErrorHandlingStrategy errorHandlingStrategy = evaluationContext.getEvaluator()
                .getErrorHandlingStrategy();
        if (null == value || value.isNull() || value.isMissingNode()) {
            return errorHandlingStrategy.handleMissingValue(jsonPathValue.getPath(), defaultValue);
        }
        final JsonNodeType nodeType = value.getNodeType();
        if (nodeType != expectedType) {
            return errorHandlingStrategy.handleTypeMismatch(
                    jsonPathValue.getPath(),
                    expectedType.name(),
                    nodeType.name(),
                    defaultValue);
        }
        return extractor.apply(value);
    }

    private static List<Object> arrayToObjectList(
            Evaluator.EvaluationContext evaluationContext,
            List<Value> values,
            Object defaultValue) {
        return values
                .stream()
                .map(value -> objectValue(evaluationContext, value, defaultValue))
                .collect(Collectors.toList());
    }


    public static String toJsonPointer(final String jsonPath) {
        if (jsonPath == null || jsonPath.trim().isEmpty()) {
            return jsonPath;
        }

        if (jsonPath.trim().startsWith("/")) {
            return jsonPath;
        }

        return jsonPath.trim().replaceAll("\\.", "/")
                .replace("$", "");
    }

}
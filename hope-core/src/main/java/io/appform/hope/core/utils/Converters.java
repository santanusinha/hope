package io.appform.hope.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import io.appform.hope.core.FunctionValue;
import io.appform.hope.core.TreeNode;
import io.appform.hope.core.Value;
import io.appform.hope.core.VisitorAdapter;
import io.appform.hope.core.functions.FunctionRegistry;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.values.BooleanValue;
import io.appform.hope.core.values.JsonPathValue;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.visitors.Evaluator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 *
 */
@Slf4j
public class Converters {
    public static Number numericValue(
            Evaluator.EvaluationContext evaluationContext,
            TreeNode node,
            Number defaultValue) {
        return node.accept(new VisitorAdapter<Number>(defaultValue) {
            @Override
            public Number visit(JsonPathValue jsonPathValue) {
                final JsonNode value = evaluationContext.getJsonContext()
                        .read(jsonPathValue.getPath());
                if(value.isNumber()) {
                    return value.asDouble();
                }
                return defaultValue;
            }

            @Override
            public Number visit(NumericValue numericValue) {
                final Number value = numericValue.getValue();
                if(null == value) {
                    final JsonPathValue pathValue = numericValue.getPathValue();
                    if(null != pathValue) {
                        return pathValue.accept(this);
                    }
                    final FunctionValue functionValue = numericValue.getFunction();
                    if(null != functionValue) {
                        return functionValue.accept(this);
                    }
                }
                return value;
            }

            @Override
            public Number visit(FunctionValue functionValue) {
                final FunctionRegistry.FunctionMeta functionMeta = functionValue.getFunction();
                final List<Value> parameters = functionValue.getParameters();
                final HopeFunction hopeFunction = createFunction(functionValue.getName(), functionMeta, parameters);
                return numericValue(evaluationContext, hopeFunction.apply(evaluationContext), defaultValue);
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
                if(value.isBoolean()) {
                    return value.asBoolean();
                }
                return super.visit(jsonPathValue);
            }

            @Override
            public Boolean visit(BooleanValue booleanValue) {
                final Boolean value = booleanValue.getValue();
                if(null == value) {
                    final JsonPathValue pathValue = booleanValue.getPathValue();
                    if(null != pathValue) {
                        return pathValue.accept(this);
                    }
                    final FunctionValue functionValue = booleanValue.getFunction();
                    if(null != functionValue) {
                        return functionValue.accept(this);
                    }
                }
                return value;
            }

            @Override
            public Boolean visit(FunctionValue functionValue) {
                final FunctionRegistry.FunctionMeta functionMeta = functionValue.getFunction();
                final List<Value> parameters = functionValue.getParameters();
                final HopeFunction hopeFunction = createFunction(functionValue.getName(), functionMeta, parameters);
                return booleanValue(evaluationContext, hopeFunction.apply(evaluationContext), defaultValue);
            }
        });
    }


    private static HopeFunction createFunction(String name, FunctionRegistry.FunctionMeta functionMeta, List<Value> parameters) {
        final List<Class<?>> paramTypes = functionMeta.getParamTypes();
        try {
            final Constructor<? extends HopeFunction> constructor = functionMeta.getFunctionClass()
                    .getDeclaredConstructor(paramTypes
                                                    .toArray(new Class<?>[paramTypes.size()]));
            log.info("Found constructor: {}", constructor);
            if(functionMeta.isArrayValue()) {
                return constructor
                        .newInstance(
                                new Object[] { parameters.toArray(new Value[parameters.size()]) });
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

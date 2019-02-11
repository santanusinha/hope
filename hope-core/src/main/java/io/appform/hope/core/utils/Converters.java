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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 *
 */
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
                final List<Class<? extends Value>> paramTypes = functionMeta.getParamTypes();
                final List<Value> parameters = functionValue.getParameters();
                try {
                    final HopeFunction hopeFunction = functionMeta.getFunctionClass()
                            .getDeclaredConstructor(paramTypes
                                                            .toArray(new Class<?>[paramTypes.size()]))
                            .newInstance(
                                    parameters.toArray(new Object[parameters.size()]));
                    return numericValue(evaluationContext, hopeFunction.apply(evaluationContext), defaultValue);
                }
                catch (InstantiationException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                return defaultValue;
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
                }
                return value;
            }
        });
    }
}

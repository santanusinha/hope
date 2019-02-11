package io.appform.hope.lang;

import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionRegistry;

import java.util.List;

/**
 *
 */
public class TypeUtils {
    public static FunctionRegistry.FunctionMeta function(String name, List<Value> values) {
        final FunctionRegistry.FunctionMeta functionMeta = FunctionRegistry.find(name)
                .orElse(null);
        if (null == functionMeta) {
            return null;
        }
        if(functionMeta.isArrayValue()) {
            return functionMeta;
        }
        final List<Class<?>> paramTypes = functionMeta.getParamTypes();
        if(!paramTypes.isEmpty()) {
            if (values.size() != paramTypes.size()) {
                throw new IllegalArgumentException(
                        String.format("Function '%s' expects '%d' arguments but '%d' provided",
                                      name, paramTypes.size(), values.size()));
            }
/*
            final List<Value> unmatched = IntStream.range(0, values.size())
                    .filter(i -> values.get(i).getClass().isAssignableFrom(paramTypes.get(i)))
                    .mapToObj(values::get)
                    .collect(Collectors.toList());
            Preconditions.checkArgument(
                    unmatched.isEmpty(),
                    String.format("Type mismatch for function params passed to %s. Required: %s, Provided: %s",
                                  name,
                                  paramTypes.stream()
                                          .map(Class::getSimpleName)
                                          .collect(Collectors.toList()),
                                  values.stream()
                                          .map(Object::getClass)
                                          .map(Class::getSimpleName)
                                          .collect(Collectors.toList())));
*/
        }
        return functionMeta;
    }
}


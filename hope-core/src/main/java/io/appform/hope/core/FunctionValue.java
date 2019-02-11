package io.appform.hope.core;

import io.appform.hope.core.functions.FunctionRegistry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FunctionValue extends Value {
    private final String name;
    private final List<Value> parameters;
    private final FunctionRegistry.FunctionMeta function;

    public FunctionValue(String name, List<Value> parameters, FunctionRegistry.FunctionMeta function) {
        this.name = name;
        this.parameters = parameters;
        this.function = function;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}

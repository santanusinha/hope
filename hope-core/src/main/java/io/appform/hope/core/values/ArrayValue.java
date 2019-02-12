package io.appform.hope.core.values;

import io.appform.hope.core.FunctionValue;
import io.appform.hope.core.Value;
import io.appform.hope.core.Visitor;

import java.util.List;

/**
 *
 */
public class ArrayValue extends EvaluatableValue<List<Value>> {

    public ArrayValue(List<Value> value) {
        super(value);
    }

    public ArrayValue(JsonPathValue pathValue) {
        super(pathValue);
    }

    public ArrayValue(FunctionValue function) {
        super(function);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}

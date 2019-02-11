package io.appform.hope.core.values;

import io.appform.hope.core.FunctionValue;
import io.appform.hope.core.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class EvaluatableValue<T> extends Value {
    private final T value;
    private final JsonPathValue pathValue;
    private final FunctionValue function;

    protected EvaluatableValue(T value) {
        this.value = value;
        this.pathValue = null;
        this.function = null;
    }

    protected EvaluatableValue(JsonPathValue pathValue) {
        this.value = null;
        this.pathValue = pathValue;
        this.function = null;
    }

    protected EvaluatableValue(FunctionValue function) {
        this.value = null;
        this.pathValue = null;
        this.function = function;
    }
}

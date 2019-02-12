package io.appform.hope.core.values;

import io.appform.hope.core.FunctionEvaluatableValue;
import io.appform.hope.core.FunctionValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class EvaluatableValue<T> extends FunctionEvaluatableValue {
    private final T value;
    private final JsonPathValue pathValue;

    protected EvaluatableValue(T value) {
        super();
        this.value = value;
        this.pathValue = null;
    }

    protected EvaluatableValue(JsonPathValue pathValue) {
        super();
        this.value = null;
        this.pathValue = pathValue;
    }

    protected EvaluatableValue(FunctionValue function) {
        super(function);
        this.value = null;
        this.pathValue = null;
    }
}

package io.appform.hope.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class FunctionEvaluatableValue extends Value {
    protected final FunctionValue function;

    protected FunctionEvaluatableValue() {
        this(null);
    }

    protected FunctionEvaluatableValue(FunctionValue function) {
        this.function = function;
    }
}

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
public abstract class UnaryOperator<T extends Value> extends Evaluatable {
    private final T operand;

    protected UnaryOperator(T operand) {
        this.operand = operand;
    }
}

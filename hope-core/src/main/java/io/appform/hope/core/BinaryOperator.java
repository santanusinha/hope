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
public abstract class BinaryOperator<T extends Value> extends Evaluatable {
    protected final T lhs;
    protected final T rhs;

    public BinaryOperator(T lhs, T rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
}

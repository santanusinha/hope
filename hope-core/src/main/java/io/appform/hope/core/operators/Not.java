package io.appform.hope.core.operators;

import io.appform.hope.core.UnaryOperator;
import io.appform.hope.core.Visitor;
import io.appform.hope.core.values.BooleanValue;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Not extends UnaryOperator<BooleanValue> {

    @Builder
    public Not(BooleanValue operand) {
        super(operand);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}

package io.appform.hope.core.operators;

import io.appform.hope.core.BinaryOperator;
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
public class And extends BinaryOperator<BooleanValue> {

    @Builder
    public And(BooleanValue lhs, BooleanValue rhs) {
        super(lhs, rhs);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }


}

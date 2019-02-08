package io.appform.hope.core.operators;

import io.appform.hope.core.BinaryOperator;
import io.appform.hope.core.Value;
import io.appform.hope.core.Visitor;
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
public class Equals extends BinaryOperator<Value> {

    @Builder
    public Equals(Value lhs, Value rhs) {
        super(lhs, rhs);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}

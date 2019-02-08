package io.appform.hope.core.operators;

import io.appform.hope.core.BinaryOperator;
import io.appform.hope.core.Visitor;
import io.appform.hope.core.values.NumericValue;
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
public class LesserEquals extends BinaryOperator<NumericValue> {

    @Builder
    public LesserEquals(NumericValue lhs, NumericValue rhs) {
        super(lhs, rhs);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }


}

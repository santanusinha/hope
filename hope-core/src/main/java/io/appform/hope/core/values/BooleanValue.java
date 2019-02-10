package io.appform.hope.core.values;

import io.appform.hope.core.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BooleanValue extends EvaluatableValue<Boolean> {

    public BooleanValue(Boolean value) {
        super(value);
    }

    public BooleanValue(JsonPathValue pathValue) {
        super(pathValue);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}

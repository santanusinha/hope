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
public class StringValue extends EvaluatableValue<String> {

    public StringValue(String value) {
        super(value);
    }

    public StringValue(JsonPathValue pathValue) {
        super(pathValue);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}

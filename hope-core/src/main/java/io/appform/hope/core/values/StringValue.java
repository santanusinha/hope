package io.appform.hope.core.values;

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
public class StringValue extends Value {
    private final String value;

    @Builder
    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}

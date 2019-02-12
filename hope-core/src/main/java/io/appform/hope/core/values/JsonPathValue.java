package io.appform.hope.core.values;

import io.appform.hope.core.FunctionEvaluatableValue;
import io.appform.hope.core.FunctionValue;
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
public class JsonPathValue extends FunctionEvaluatableValue {

    private final String path;

    public JsonPathValue(String path) {
        this.path = path;
    }

    public JsonPathValue(FunctionValue function) {
        super(function);
        this.path = null;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}

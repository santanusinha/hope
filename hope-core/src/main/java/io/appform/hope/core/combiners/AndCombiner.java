package io.appform.hope.core.combiners;

import io.appform.hope.core.Combiner;
import io.appform.hope.core.Evaluatable;
import io.appform.hope.core.Visitor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AndCombiner extends Combiner {
    @Builder
    public AndCombiner(List<Evaluatable> expressions) {
        super(expressions);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}

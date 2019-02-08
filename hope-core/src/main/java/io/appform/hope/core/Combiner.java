package io.appform.hope.core;

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
public abstract class Combiner extends Evaluatable {
    private final List<Evaluatable> expressions;

    protected Combiner(List<Evaluatable> expressions) {
        this.expressions = expressions;
    }
}

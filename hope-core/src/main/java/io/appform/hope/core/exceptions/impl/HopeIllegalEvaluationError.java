package io.appform.hope.core.exceptions.impl;

import io.appform.hope.core.exceptions.HopeException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HopeIllegalEvaluationError extends HopeException {
    private final String parserError;

    public HopeIllegalEvaluationError(String evalError) {
        super("Error during evaluation: " + evalError);
        this.parserError = evalError;
    }
}

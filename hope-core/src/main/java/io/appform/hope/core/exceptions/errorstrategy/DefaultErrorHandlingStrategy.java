package io.appform.hope.core.exceptions.errorstrategy;

import io.appform.hope.core.exceptions.impl.HopeIllegalEvaluationError;
import io.appform.hope.core.exceptions.impl.HopeMissingValueError;
import io.appform.hope.core.exceptions.impl.HopeTypeMismatchError;

/**
 *
 */
public class DefaultErrorHandlingStrategy implements ErrorHandlingStrategy {
    @Override
    public <T> T handleMissingValue(String path, T defaultValue) {
        throw new HopeMissingValueError(path);
    }

    @Override
    public <T> T handleTypeMismatch(String path, String expected, String actual, T defaultValue) {
        throw new HopeTypeMismatchError(path, expected, actual);
    }

    @Override
    public <T> T handleIllegalEval(String message, T defaultValue) {
        throw new HopeIllegalEvaluationError(message);
    }
}

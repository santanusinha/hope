package io.appform.hope.core.exceptions.errorstrategy;

/**
 *
 */
public class InjectValueErrorHandlingStrategy implements ErrorHandlingStrategy {

    public InjectValueErrorHandlingStrategy() {
    }

    @Override
    public <T> T handleMissingValue(String path, T defaultValue) {
        return defaultValue;
    }

    @Override
    public <T> T handleTypeMismatch(String path, String expected, String actual, T defaultValue) {
        return defaultValue;
    }

    @Override
    public <T> T handleIllegalEval(String message, T defaultValue) {
        return defaultValue;
    }

}

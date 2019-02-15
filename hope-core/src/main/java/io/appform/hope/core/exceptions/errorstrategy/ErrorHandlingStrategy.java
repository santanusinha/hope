package io.appform.hope.core.exceptions.errorstrategy;

/**
 *
 */
public interface ErrorHandlingStrategy {
    <T> T handleMissingValue(String path, T defaultValue);
    <T> T handleTypeMismatch(String path, String expected, String actual, T defaultValue);
    <T> T handleIllegalEval(String message, T defaultValue);

/*
    @SuppressWarnings("unchecked")
    static <ParamType> ErrorHandlingStrategy<ParamType> create(Evaluator.EvaluationContext evaluationContext) {
        try {
            return evaluationContext.getEvaluator()
                    .getErrorHandlerClass().newInstance();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new UnsupportedOperationException();
    }
*/
}

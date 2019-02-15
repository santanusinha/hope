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
public class HopeExpressionParserError extends HopeException {
    private final String parserError;

    public HopeExpressionParserError(String parserError) {
        super("Error parsing expression: " + parserError);
        this.parserError = parserError;
    }
}

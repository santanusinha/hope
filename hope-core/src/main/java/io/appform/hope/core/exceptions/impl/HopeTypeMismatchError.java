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
public class HopeTypeMismatchError extends HopeException {
    private final String path;
    private final String expected;
    private final String actual;

    public HopeTypeMismatchError(String path, String expected, String actual) {
        super(String.format("Type mismatch at path %s. Expected: %s Actual: %s",
                            path, expected, actual));
        this.path = path;
        this.expected = expected;
        this.actual = actual;
    }
}

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
public class HopeMissingValueError extends HopeException {
    private final String path;

    public HopeMissingValueError(String path) {
        super("Missing value at path: " + path);
        this.path = path;
    }
}

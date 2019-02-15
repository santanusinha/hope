package io.appform.hope.core.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class HopeException extends RuntimeException {
    public HopeException(String message) {
        super(message);
    }

    public HopeException(String message, Throwable cause) {
        super(message, cause);
    }
}

package org.showpage.springwebjwt.exceptions;

import lombok.NoArgsConstructor;

/**
 * Use this exception for user validation errors.
 */
@NoArgsConstructor
public class ValidationException extends Exception {
    /**
     * Constructor.
     *
     * @param msg The exception message forwarded to Exception.
     */
    public ValidationException(String msg) {
        super(msg);
    }
}

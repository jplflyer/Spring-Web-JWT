package org.showpage.springwebjwt.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ValidationException extends Exception {
    public ValidationException(String msg) {
        super(msg);
    }
}

package org.showpage.springwebjwt.uimodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Base class for all REST call responses.
 */
@Data
@AllArgsConstructor
@SuperBuilder
public class Response {
    /**
     * Default constructor.
     */
    public Response() {
    }

    /** Was the call successful? */
    private Boolean success;

    /** An appropriate message. */
    private String message;
}

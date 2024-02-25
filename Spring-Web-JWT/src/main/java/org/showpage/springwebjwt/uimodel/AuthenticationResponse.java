package org.showpage.springwebjwt.uimodel;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * This is returned by login and register and provides the Bearer auth token to use.
 * Other calls should have an Authorization header that looks like
 * 'Bearer this-token'.
 */
@NoArgsConstructor
@SuperBuilder
public class AuthenticationResponse extends Response {
    /** The signed JWT to begin using. */
    @Getter
    @Setter
    private String token;

    /**
     * Constructor.
     *
     * @param success  True if the call is successful
     * @param message  An appropriate message, probably null if a successful call
     * @param tokenValue The JWT to start using, null on an error
     */
    public AuthenticationResponse(Boolean success, String message, String tokenValue)
    {
        super(success, message);
        tokenValue = token;
    }
}

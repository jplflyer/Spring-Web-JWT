package org.showpage.springwebjwt.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Returned from some methods in AuthorizationUtil.
 */
@Data
@AllArgsConstructor
@Builder
public class UsernamePassword {
    /** Constructor. */
    public UsernamePassword() {
    }

    /** The username. */
    private String username;

    /** The password. */
    private String password;
}

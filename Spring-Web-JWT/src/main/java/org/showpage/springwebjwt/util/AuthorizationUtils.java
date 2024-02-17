package org.showpage.springwebjwt.util;

import org.showpage.springwebjwt.exceptions.ValidationException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utilities for parsing the Authentication header.
 */
public class AuthorizationUtils {
    /**
     * Parse the Authorization header when Basic authorization is expected.
     *
     * {@snippet :
     *     @GetMapping("/seed")
     *     public ResponseEntity<AuthenticationResponse> seed(@RequestHeader("Authorization") String authHeader ) {
     *          UsernamePassword usernamePassword = AuthorizationUtils.parseBasicAuthorization(authHeader);
     *          // The rest of your code.
     *     }
     * }
     *
     * @param header The Authorization header
     * @return A UsernamePassword with the decrypted values from the header
     * @throws ValidationException If the header isn't valid
     */
    public static UsernamePassword parseBasicAuthorization(String header) throws ValidationException {

        if (header == null || !header.startsWith("Basic ")) {
            throw new ValidationException("Please provide Basic credentials");
        }

        String base64Credentials = header.substring("Basic ".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        final String[] values = credentials.split(":", 2);

        if (values.length != 2) {
            throw new ValidationException("Please provide Basic credentials");
        }

        return UsernamePassword
                .builder()
                .username(values[0])
                .password(values[1])
                .build();
    }
}

package org.showpage.springwebjwt.uimodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuthenticationResponse extends Response {
    private String token;

    public AuthenticationResponse(Boolean success, String message, String tokenValue)
    {
        super(success, message);
        tokenValue = token;
    }
}

package org.showpage.demo.controller;

import lombok.RequiredArgsConstructor;
import org.showpage.demo.service.MemberService;
import org.showpage.springwebjwt.exceptions.ValidationException;
import org.showpage.springwebjwt.uimodel.AuthenticationResponse;
import org.showpage.springwebjwt.util.AuthorizationUtils;
import org.showpage.springwebjwt.util.UsernamePassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is our Login Controller. It does basics.
 */
@RequiredArgsConstructor
@RestController
public class LoginController {
    private final MemberService memberService;

    /**
     * This is a simple /ping. It's used to test the server is running. It requires no authorization.
     *
     * @return Our response is a simple String.
     */
    @GetMapping("/ping")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("Hello\n", HttpStatus.OK);
    }

    /**
     * Seed an empty database with an admin user.
     *
     * @param authHeader The authorization header, which should be Basic.
     * @return The Authentication Response with the JWT you can use.
     */
    @GetMapping("/seed")
    public ResponseEntity<AuthenticationResponse> seed(@RequestHeader("Authorization") String authHeader ) {
        System.out.println("/seed");
        try {
            UsernamePassword usernamePassword = AuthorizationUtils.parseBasicAuthorization(authHeader);
            AuthenticationResponse resp = memberService.seed( usernamePassword.getUsername(), usernamePassword.getPassword() );
            return ResponseEntity.ok(resp);
        }
        catch (ValidationException e) {
            AuthenticationResponse resp = new AuthenticationResponse(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(resp);
        }
    }


    /**
     * Register a new (non-admin) user.
     *
     * @param authHeader The authorization header, which should be Basic.
     * @return The Authentication Response with the JWT you can use.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestHeader("Authorization") String authHeader) {
        try {
            UsernamePassword usernamePassword = AuthorizationUtils.parseBasicAuthorization(authHeader);
            AuthenticationResponse resp = memberService.register( usernamePassword.getUsername(), usernamePassword.getPassword() );
            return ResponseEntity.ok(resp);
        }
        catch (ValidationException e) {
            AuthenticationResponse resp = new AuthenticationResponse(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(resp);
        }
        catch (AuthenticationException e) {
            AuthenticationResponse resp = new AuthenticationResponse(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * Login.
     *
     * @param authHeader The authorization header, which should be Basic.
     * @return The Authentication Response with the JWT you can use.
     */
    @GetMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestHeader("Authorization") String authHeader) {
        try {
            UsernamePassword usernamePassword = AuthorizationUtils.parseBasicAuthorization(authHeader);
            AuthenticationResponse resp = memberService.authenticate( usernamePassword.getUsername(), usernamePassword.getPassword() );
            return ResponseEntity.ok(resp);
        }
        catch (ValidationException e) {
            AuthenticationResponse resp = new AuthenticationResponse(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(resp);
        }
        catch (AuthenticationException e) {
            AuthenticationResponse resp = new AuthenticationResponse(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(resp);
        }
    }
}

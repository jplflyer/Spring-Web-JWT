package org.showpage.springwebjwt.controller;

import lombok.RequiredArgsConstructor;
import org.showpage.springwebjwt.dbmodel.Member;
import org.showpage.springwebjwt.exceptions.ValidationException;
import org.showpage.springwebjwt.repository.MemberRepository;
import org.showpage.springwebjwt.service.MemberService;
import org.showpage.springwebjwt.uimodel.AuthenticationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class Hello {
    private final MemberService memberService;

    /**
     * This is a simple /ping. It's used to test the server is running. It requires no authorization.
     */
    @GetMapping("/ping")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }

    /**
     * Seed an empty database with an admin user.
     */
    @GetMapping("/seed")
    public ResponseEntity<AuthenticationResponse> seed( @RequestHeader("Authorization") String authHeader ) {
        System.out.println("/seed");
        try {
            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                throw new ValidationException("Please provide Basic credentials");
            }

            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                throw new ValidationException("Please provide Basic credentials");
            }

            AuthenticationResponse resp = memberService.seed( values[0], values[1] );
            return ResponseEntity.ok(resp);
        }
        catch (ValidationException e) {
            AuthenticationResponse resp = new AuthenticationResponse(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(resp);
        }
    }

    /**
     * Register a new (non-admin) user.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                throw new ValidationException("Please provide Basic credentials");
            }

            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                throw new ValidationException("Please provide Basic credentials");
            }

            AuthenticationResponse resp = memberService.authenticate( values[0], values[1] );
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
     * This is a test I'll remove. It tests we can load users.
     */
    @GetMapping("/user")
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok("Hello");
    }
}

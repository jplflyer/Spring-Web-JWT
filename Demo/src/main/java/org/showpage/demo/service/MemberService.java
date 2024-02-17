package org.showpage.demo.service;

import lombok.RequiredArgsConstructor;
import org.showpage.demo.dbmodel.Member;
import org.showpage.demo.dbmodel.UserRole;
import org.showpage.demo.repository.MemberRepository;
import org.showpage.springwebjwt.config.JwtService;
import org.showpage.springwebjwt.exceptions.ValidationException;
import org.showpage.springwebjwt.uimodel.AuthenticationResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This provides User services.
 */
@Service
@RequiredArgsConstructor
public class MemberService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    /**
     * Seed the database with an admin user.
     *
     * @param username The username
     * @param password The password
     * @throws ValidationException on bad username/password or duplicate user
     * @return The Authentication Response with the JWT populated.
     */
    public AuthenticationResponse seed(String username, String password) throws ValidationException {
        if (memberRepository.count() > 0) {
            throw new ValidationException("Database is not empty");
        }
        return registerWorker(username, password, true);
    }

    /**
     * Register a new non-admin user.
     *
     * @param username The username
     * @param password The password
     * @throws ValidationException on bad username/password or duplicate user
     * @return The Authentication Response with the JWT populated.
     */
    public AuthenticationResponse register(String username, String password) throws ValidationException {
        if (username != null) {
            username = username.trim();
            Optional<Member> existing = memberRepository.findByUsername(username);
            if (existing.isPresent()) {
                throw new ValidationException("Duplicate user");
            }
        }
        return registerWorker(username, password, false);
    }

    /**
     * This is effectively a login.
     *
     * @param username The username.
     * @param password The plain-text password.
     * @throws AuthenticationException  ???
     * @throws ValidationException      On bad username/password or user not found
     *
     * @return The Authentication Response with the JWT populated.
     */
    public AuthenticationResponse authenticate(String username, String password) throws AuthenticationException, ValidationException {
        try {
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                throw new ValidationException("Username and password must be provided");
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username.trim(), password.trim()));
            Member member = memberRepository.findByUsername(username.trim()).orElse(null);
            if (member == null) {
                throw new ValidationException("User not found");
            }

            String jwtToken = jwtService.generateToken(null, member);
            return AuthenticationResponse.builder()
                    .success(true)
                    .token(jwtToken)
                    .build();
        }
        finally {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Register a user.
     *
     * @param username  The username
     * @param password  The password
     * @throws ValidationException On bad username/password
     *
     * @return The authentication response with the corresponding JWT populated
     */
    private AuthenticationResponse registerWorker(String username, String password, boolean isAdmin) throws ValidationException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new ValidationException("Username and password must be provided");
        }

        try {
            Member member = Member
                    .builder()
                    .username(username.trim().toLowerCase())
                    .password(password.trim())
                    .role(isAdmin ? UserRole.ADMIN : UserRole.USER)
                    .build();

            Member saved = memberRepository.save(member);

            String jwtToken = jwtService.generateToken(null, saved);

            return AuthenticationResponse.builder()
                    .success(true)
                    .token(jwtToken)
                    .build();
        }

        // We intentionally make this slow to slow down naughty behavior. We could get even smarter
        // and throttle all new user attempts.
        finally {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ignored) {
            }
        }
    }
}

package org.showpage.demo.config;

import lombok.RequiredArgsConstructor;
import org.showpage.demo.repository.MemberRepository;
import org.showpage.springwebjwt.config.URLWhiteListProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Define the UserDetailsService, authenticationProvider, passwordEncoder, and authenticationManager beans.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    /** We use a Member table. */
    private final MemberRepository memberRepository;

    private static final String[] WHITE_LIST_URL = {
            "/ping",
            "/seed",
            "/register",
            "/login"
    };

    /**
     * UserDetailsService is used by DaoAuthenticationProvider for retrieving a username,
     * a password, and other attributes for authenticating with a username and password.
     * Spring Security provides in-memory and JDBC implementations of UserDetailsService.
     *
     * @return  A simple lambda
     */
    @Bean
    public UserDetailsService userDetailsService() {
        logger.info("userDetailsService");
        return username -> memberRepository
                .findByUsername(username)
                .orElseThrow( () -> new UsernameNotFoundException("User not found") );
    }

    /**
     * This tells the default URL filter chain what URLs to whitelist.
     * @return The provider that hands it over.
     */
    @Bean
    public URLWhiteListProvider urlWhiteListProvider() {
        return () -> WHITE_LIST_URL;
    }

}

package org.showpage.springwebjwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Our Spring Application.
 */
@SpringBootApplication
public class SpringWebJwtApplication {
    /**
     * This exists just to get JavaDoc not to complain.
     */
    public SpringWebJwtApplication() {
    }

    /**
     * Main entrypoint.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringWebJwtApplication.class, args);
    }

}

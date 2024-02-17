package org.showpage.springwebjwt.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * I needed this just to keep the system from complaining.
 * But I fumbled around while trying to get this to work, so maybe this isn't actually needed.
 */
@AutoConfiguration
public class URLWhiteListDefault {
    /**
     * Provide the whitelist.
     *
     * @return The white list provider.
     */
    @Bean
    public URLWhiteListProvider urlWhiteListProvider() {
        return () -> new String[]{};
    }
}

package org.showpage.springwebjwt.config;

/**
 * Used to provide the white list for our filter chain.
 */
public interface URLWhiteListProvider {
    /**
     * Return the white list of URLs.
     *
     * {@snippet :
     *     @Bean
     *     public URLWhiteListProvider urlWhiteListProvider() {
     *         return () -> WHITE_LIST_URL;
     *     }
     * }
     * @return The white list.
     */
    String[] get();
}

package eu.stenlund.janus.ssr;

/**
 * The helper for various functions for Server Side rendering and use of unpoly and other frontend JS/CSS-packages.
 * 
 * @author Tomas Stenlund
 * @since 2022-08-12
 * 
 */
public class JanusSSRHelper {
    
    private static String UNPOLY_SUBMIT_START = "up-submit up-target=\"#workarea\" up-history=true up-location=\"";
    private static String UNPOLY_SUBMIT_END = "\"";

    private static String UNPOLY_FOLLOW = "up-follow up-history=\"true\" up-target=\"#workarea\"";

    /**
     * Create the unpoly attributes neede to follow a link and update the workarea with the result and the broswer location URL.
     * @return A HTML attribute sequence.
     */
    public static String unpolyFollow()
    {
        return UNPOLY_FOLLOW;
    }

    /**
     * Create the unpoly attributes needed to submit a form and update the workarea with the result and the browser location URL.
     * 
     * @param url The URL to update the browser with.
     * @return A HTML attribute sequence.
     */
    public static String unpolySubmit (String url)
    {
        return UNPOLY_SUBMIT_START + url + UNPOLY_SUBMIT_END;
    }
}

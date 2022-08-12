package eu.stenlund.janus.ssr;

/**
 * The helper for various functions for Server Side rendering and use of unpoly and other frontend JS/CSS-packages.
 * 
 * @author Tomas Stenlund
 * @since 2022-08-12
 * 
 */
public class JanusSSRHelper {
    
    /**
     * Unpoly attributes for submitting a form and update the workarea with the result and the browser URL.
     */
    private static String UNPOLY_SUBMIT_START = "up-submit up-target=\"#workarea\" up-history=true up-location=\"";
    private static String UNPOLY_SUBMIT_END = "\"";

    /**
     * Unpoly attributes for following a link and update the workarea with the result and the browser URL.
     */
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

    /**
     * HTML Attribute for inputs and other elements that supports required.
     * 
     * @return The HTML attribute.
     */
    public static String required()
    {
        return "required";
    }

    /**
     * HTML Attribute for inputs and other elements that supports readonly.
     * 
     * @return The HTML attribute.
     */
    public static String readonly()
    {
        return "readonly";
    }

    /**
     * Add attributes to igenore password managers whenever they are used, typically they should not bee activated
     * when chaning password for another user.
     * 
     * Currently only LastPass is supported, but more can be added if it is they are using a similar method.
     * @return The HTML attributes.
     */
    public static String ignorePasswordManagers()
    {
        return "data-lpignore=true";
    }
}

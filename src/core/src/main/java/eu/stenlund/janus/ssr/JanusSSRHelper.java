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
    private static String UNPOLY_SUBMIT_START_1 = "up-submit up-target=\"";
    private static String UNPOLY_SUBMIT_START_2 = "\" up-history=true up-location=\"";
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
        return unpolySubmit("#workare", url);
    }

    public static String unpolySubmit (String area, String url)
    {
        return UNPOLY_SUBMIT_START_1 + area + UNPOLY_SUBMIT_START_2 + url + UNPOLY_SUBMIT_END;
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
     * HTML Attribute for inputs and other elements that supports disabled.
     * 
     * @return The HTML attribute.
     */
    public static String disabled()
    {
        return "disabled";
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

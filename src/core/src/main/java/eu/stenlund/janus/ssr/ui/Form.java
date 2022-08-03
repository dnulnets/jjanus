package eu.stenlund.janus.ssr.ui;

/**
 * A form
 *
 * @author Tomas Stenlund
 * @since 2022-08-01
 * 
 */
public class Form {
    
    public static String GET = "get";
    public static String POST = "post";

    /**
     * The URL to be used when pressing submit.
     */
    public String action;

    /**
     * If the form whould be validated before submitting.
     */
    public boolean validate;

    /**
     * The method to use when submitting a form.
     */
    public String method;

    /**
     * Creates a form model.
     * 
     * @param method The method to use.
     * @param action The form action URL.
     * @param validate If it should be validated.
     */
    public Form (String method, String action, boolean validate)
    {
        this.method = method;
        this.action = action;
        this.validate = validate;
    }
}
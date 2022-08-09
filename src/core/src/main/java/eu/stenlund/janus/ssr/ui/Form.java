package eu.stenlund.janus.ssr.ui;

import io.quarkus.qute.RawString;

/**
 * A form
 *
 * @author Tomas Stenlund
 * @since 2022-08-01
 * 
 */
public class Form extends Base {
    
    public static String GET = "get";
    public static String POST = "post";

    /**
     * The URL to be used when pressing submit.
     */
    public RawString action;

    /**
     * If the form whould be validated before submitting.
     */
    public boolean validate;

    /**
     * Any extra attributes for the form tag.
     */
    public RawString extra;

    /**
     * The method to use when submitting a form.
     */
    public RawString method;

    @Override
    public String type()
    {
        return "form";
    }

    /**
     * Creates a form model.
     * 
     * @param method The method to use.
     * @param action The form action URL.
     * @param validate If it should be validated.
     */
    public Form (String method, String action, boolean validate)
    {
        this.method = new RawString(method);
        this.action = new RawString(action);
        this.validate = validate;
    }

        /**
     * Creates a form model.
     * 
     * @param method The method to use.
     * @param action The form action URL.
     * @param validate If it should be validated.
     */
    public Form (String method, String action, boolean validate, String extra)
    {
        this.method = new RawString(method);
        this.action = new RawString (action);
        this.validate = validate;
        this.extra = new RawString(extra);
    }
}

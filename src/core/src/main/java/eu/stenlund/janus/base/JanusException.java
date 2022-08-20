package eu.stenlund.janus.base;

/**
 * The Janus base exception that is caught by an exception mapper to produce an information page
 * for the user.
 *
 * @author Tomas Stenlund
 * @since 2022-08-20
 * 
 */
public class JanusException extends RuntimeException {

    /**
     * Which template to use when rendering this exception.
     */
    private String template;

    /**
     * Returns with this exceptions qute template.
     * 
     * @return The name of the qute template.
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets the qute template for this exeption.
     * 
     * @param template The name of the qute template.
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     *  Which return URL to use.
     */
    private String rtn;

    /**
     * Returns with the return URL for this error message if rendered via qute.
     * 
     * @return The return URL.
     */
    public String getReturn() {
        return rtn;
    }

    /**
     * Create the Janus exception.
     * 
     * @param errorMessage The error message.
     * @param rtn The return URL.
     */
    public JanusException(String errorMessage, String rtn) {
        super(errorMessage);
        this.rtn = rtn;
        template = "error.html";
    }

}

package eu.stenlund.janus.base;

public class JanusException extends RuntimeException {

    /**
     * Which template to use when rendering this exception.
     */
    private String template;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     *  Which return URL to use.
     */
    private String rtn;

    public String getReturn() {
        return rtn;
    }

    public JanusException(String errorMessage, String rtn) {
        super(errorMessage);
        this.rtn = rtn;
        template = "error.html";
    }

}

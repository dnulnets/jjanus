package eu.stenlund.janus.base;

public class JanusException extends RuntimeException {

    private String rtn;

    public String getReturn() {
        return rtn;
    }

    public JanusException(String errorMessage, String rtn) {
        super(errorMessage);
        this.rtn = rtn;
    }

}

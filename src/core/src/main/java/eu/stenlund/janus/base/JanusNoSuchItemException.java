package eu.stenlund.janus.base;

public class JanusNoSuchItemException extends JanusException {

    public String type;

    public String getType() {
        return type;
    }

    public String uuid;

    public String getUuid() {
        return uuid;
    }

    public JanusNoSuchItemException(String errorMessage, String itemType, String uuid, String rtn) {
        super(errorMessage, rtn);
        setTemplate("error_nosuchitem.html");
        this.type = itemType;
        this.uuid = uuid;
    }

}

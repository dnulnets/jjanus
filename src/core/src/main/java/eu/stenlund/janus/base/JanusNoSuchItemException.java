package eu.stenlund.janus.base;

/**
 * The Janus exception when the database do not have such an item.
 *
 * @author Tomas Stenlund
 * @since 2022-08-20
 * 
 */
public class JanusNoSuchItemException extends JanusException {

    /**
     * The type of the object tha is missing.
     */
    public String type;

    /**
     * The UUID of the missing object.
     */
    public String uuid;

    /**
     * Returns with the type of the missing object.
     * 
     * @return The type of the object.
     */
    public String getType() {
        return type;
    }

    /**
     * Return with the UUID of the missing object.
     * 
     * @return The UUID of the missing object.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Creates the Janus missing object exception.
     * 
     * @param errorMessage The error message.
     * @param itemType The type of the object.
     * @param uuid The UUID of the object.
     * @param rtn The return URL for the error page.
     */
    public JanusNoSuchItemException(String errorMessage, String itemType, String uuid, String rtn) {
        super(errorMessage, rtn);
        setTemplate("error_nosuchitem.html");
        this.type = itemType;
        this.uuid = uuid;
    }

}

package eu.stenlund.janus.base;

import java.io.Serializable;
import java.util.UUID;

/**
 * The session POJO used for serlizing the session to and from a cookie.
 *
 * @author Tomas Stenlund
 * @since 2022-07-16
 * 
 */
public class JanusSessionPOJO implements Serializable {

    /**
     * Version of the object
     */
    private static final long serialVersionUID = 3L;

    /**
     * ISO-standard locale <languag>-<COUNTRY>
     */
    public String locale;

    /**
     * Creation date of the session object
     */
    public long timeStamp;

    /**
     * User interface list size
     */
    public int listSize;

    /*
     * Selected product
     */
    public UUID product;

    /*
     * Selected team
     */
    public UUID team;
}

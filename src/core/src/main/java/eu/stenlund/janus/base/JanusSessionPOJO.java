package eu.stenlund.janus.base;

import java.io.Serializable;

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
    private static final long serialVersionUID = 1L;

    /**
     * ISO-standard locale <languag>-<COUNTRY>
     */
    public String locale;

    /**
     * Creation date of the session object
     */
    public long timeStamp;
}

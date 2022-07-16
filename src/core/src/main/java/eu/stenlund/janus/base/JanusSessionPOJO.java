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
    public String locale;
}

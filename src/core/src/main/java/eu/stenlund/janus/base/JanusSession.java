package eu.stenlund.janus.base;

import java.time.Instant;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.vertx.core.http.HttpServerRequest;

/**
 * The session object, created for each request. Usually from a cookie sent to
 * the server.
 *
 * @author Tomas Stenlund
 * @since 2022-07-16
 * 
 */
@RequestScoped
@Named("session")
public class JanusSession {

    /**
     * The UNIX timestamp for when the cookie was created
     */
    private long timeStamp;

    /**
     * The current locale for the user.
     */
    private String locale;

    /**
     * The host for the request, i.e. the host of the URL the user used for browsing
     * here. It gets set by the JanusFilter when the requests comes in.
     */
    public String host = "";

    /**
     * Telss if the state of the request has changed. It is used to determine if we
     * need to
     * update the cookie.
     */
    private boolean changed = false;

    /**
     * Creates a session for the server request, and sets default values.
     * 
     * @param request The request the session object is associated with.
     */
    public JanusSession(HttpServerRequest request) {
        locale = Locale.getDefault().toString();
        timeStamp = Instant.now().getEpochSecond();
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Resets the session objects timestamp to the current time.
     */
    public void newTimeStamp() {
        timeStamp = Instant.now().getEpochSecond();
    }

    /**
     * Returns with the age of the session object.
     * 
     * @return The age in seconds.
     */
    public long getAge() {
        return Instant.now().getEpochSecond() - timeStamp;
    }

    public String getLocale() {
        return locale;
    }

    public boolean getChanged() {
        return changed;
    }

    public void setChanged(boolean b) {
        changed = b;
    }

    public void setLocale(String l) {
        locale = l;
        changed = true;
    }

    /**
     * Copies all the values from the plain java object to the session object. Used
     * when we
     * are deseralizing the cookie.
     * 
     * @param js The deserialized cookie as a plain java object
     */
    public void createFromPOJO(JanusSessionPOJO js) {
        locale = js.locale;
        timeStamp = js.timeStamp;
        changed = false;
    }

    /**
     * Converts the session object the plain java object used for serializing the
     * data and store it in a
     * cookie.
     * 
     * @return A POJO for the session.
     */
    public JanusSessionPOJO convertToPOJO() {
        JanusSessionPOJO jsp = new JanusSessionPOJO();
        jsp.locale = locale;
        jsp.timeStamp = timeStamp;
        return jsp;
    }
}

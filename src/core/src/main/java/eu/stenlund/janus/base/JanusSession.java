package eu.stenlund.janus.base;

import java.util.Locale;

import javax.enterprise.context.RequestScoped;

import io.vertx.core.http.HttpServerRequest;

/**
 * The session object, created for each request. Usually from a cookie sent to the server.
 *
 * @author Tomas Stenlund
 * @since 2022-07-16
 * 
 */
@RequestScoped
public class JanusSession {

    /**
     * The current locale for the user.
     */
    private String locale = "en_US";

    /**
     * The host for the request, i.e. the host of the URL the user used for browsing here.
     */
    public String host = "";

    /**
     * Telss if the state of the request has changed. It is used to determine if we need to
     * update the cookie.
     */
    private boolean changed = false;

    /**
     * Creates a session for the server request, and sets default values.
     * 
     * @param request
     */
    public JanusSession(HttpServerRequest request) {
        locale = Locale.getDefault().toString();
    }

    public String getLocale() {
        return locale;
    }

    public boolean hasChanged() {
        return changed;
    }

    public void changed(boolean b)
    {
        changed = b;
    }

    public void setLocale (String l) {
        locale = l;
        changed = true;
    }

    public void createFrom (JanusSessionPOJO js)
    {
        locale = js.locale;
        changed = false;
    }

    /**
     * Converts the session object the plain jaba object used for serializing the data and store it in a
     * cookie.
     * 
     * @return A POJO for the session.
     */
    public JanusSessionPOJO convert ()
    {
        JanusSessionPOJO jsp = new JanusSessionPOJO();
        jsp.locale = locale;
        return jsp;
    }
}

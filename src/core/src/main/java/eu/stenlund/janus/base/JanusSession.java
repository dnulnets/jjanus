package eu.stenlund.janus.base;

import java.util.Locale;

import javax.enterprise.context.RequestScoped;

import io.vertx.core.http.HttpServerRequest;

@RequestScoped
public class JanusSession {

    private String locale = Locale.getDefault().toString();

    public transient String host = "";
    private transient boolean changed = false;

    public JanusSession(HttpServerRequest request) {
        locale = Locale.getDefault().toString();
    }

    public String getLocale() {
        return locale;
    }

    public boolean hasChanged() {
        return changed;
    }

    public void changed()
    {
        changed = true;
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

    public JanusSessionPOJO convert ()
    {
        JanusSessionPOJO jsp = new JanusSessionPOJO();
        jsp.locale = locale;
        return jsp;
    }
}

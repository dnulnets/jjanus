package eu.stenlund.janus.base;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

class JanusFilter {

    @Inject
    JanusSessionHelper jsh;

    @Inject
    JanusSession js;

    private static final Logger log = Logger.getLogger(JanusFilter.class);

    @ServerRequestFilter()
    public void inboundSessionFilter(ContainerRequestContext requestContext) {
        Cookie c = requestContext.getCookies().get(JanusSessionHelper.cookieName);
        if (c != null) {
            log.info ("Create JanusSession from Cookie");
            try {
                JanusSessionPOJO ljs = jsh.createSessionFromCookie(c.getValue());
                js.createFrom(ljs);
                js.host = requestContext.getUriInfo().getBaseUri().getHost();
            } catch (Exception e)
            {
                log.warn("Exception for decryption or parsing the cookie, se up for a rewrite of a new cookie");
                js.changed(); // Force a rewrite of a new cookie
            }
        } else {
            log.info ("No cookie to create JanusSession with");
        }
    }

    @ServerResponseFilter()
    public void outboundSessionFilter(ContainerResponseContext responseContext) {
        if (js.hasChanged()) {
            log.info ("Create cookie from JanusSession if it has changed");
            try {
                log.info ("Janussession has changed");
                NewCookie nc = jsh.createSessionCookie(js.convert(),"/janus", js.host);
                log.info ("Janussession Created as cookie");
                responseContext.getHeaders().add("Set-Cookie", nc);                
                log.info ("Cookie = " + nc.toString());
            } catch (Exception e)
            {
                log.error ("Exception for encryption or writing the cookie");
                e.printStackTrace();
            }
        }
    }

}
package eu.stenlund.janus.base;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

/**
 * The filters used for handling the state cookie for each request. Both parsing and unparsing.
 * I were not able to make it work with the @SessionScoped annotation for some reason. 
 *
 * @author Tomas Stenlund
 * @since 2022-07-16
 * 
 */
class JanusFilter {

    @Inject
    JanusSessionHelper jsh;

    @Inject
    JanusSession js;

    private static final Logger log = Logger.getLogger(JanusFilter.class);

    /**
     * Update the session object with the cookie.
     * 
     * @param requestContext The request
     */
    @ServerRequestFilter()
    public void inboundSessionFilter(ContainerRequestContext requestContext) {
        Cookie c = requestContext.getCookies().get(JanusSessionHelper.COOKIE_NAME_SESSION);
        Cookie i = requestContext.getCookies().get(JanusSessionHelper.COOKIE_NAME_IVP);
        js.host = requestContext.getUriInfo().getBaseUri().getHost();
        if (c != null && i != null) {
            log.info ("Create JanusSession from Cookie");
            try {
                JanusSessionPOJO ljs = jsh.createSessionFromCookie(c.getValue(), i.getValue());
                js.createFrom(ljs);
            } catch (Exception e)
            {
                log.warn("Exception for decryption or parsing the cookie, force rewrite of a new cookie");
                js.changed(true); // Force a rewrite of a new cookie
            }
        } else {
            log.info ("No cookie to create JanusSession with, force write of new cookie");
            js.changed(true); // Force rewrite of new cookie
        }
    }

    /**
     * The outbound request that stores the session object as a cookie if it has changed.
     * 
     * @param responseContext The response
     */
    @ServerResponseFilter()
    public void outboundSessionFilter(ContainerResponseContext responseContext) {
        if (js.hasChanged()) {
            log.info ("Create cookie from JanusSession if it has changed");
            try {
                log.info ("Janussession has changed");
                NewCookie nc[] = jsh.createSessionCookie(js.convert(), js.host);
                log.info ("Janussession Created as cookie");
                responseContext.getHeaders().add("Set-Cookie", nc[0]);                
                responseContext.getHeaders().add("Set-Cookie", nc[1]);                
                log.info ("Cookie = " + nc.toString());
                js.changed (false);
            } catch (Exception e)
            {
                log.error ("Exception for encryption or writing the cookie");
                e.printStackTrace();
            }
        }
    }

}
package eu.stenlund.janus.base;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

/**
 * The filters used for handling the state cookie for each request. Both parsing
 * and unparsing.
 * I were not able to make it work with the SessionScoped annotation for some
 * reason.
 *
 * @author Tomas Stenlund
 * @since 2022-07-16
 * 
 */
class JanusFilter {

    private static final Logger log = Logger.getLogger(JanusFilter.class);

    /**
     * The session helper object that holds keys, encrypt and decrypt cookies.
     */
    @Inject
    JanusSessionHelper jsh;

    /**
     * The session object for the application, it comes with every request as a cookie.
     */
    @Inject
    JanusSession js;

    /**
     * Reuse the samesite way of setting the cookie, we add the cookie by ourselves
     * and therefore it is not done by quarkus. This is because we cannot add a
     * cookie in a ContainerResponseContext.
     */
    @ConfigProperty(name = "quarkus.http.same-site-cookie.janus_session.value")
    String SAMESITE;

    /**
     * Extract the cookie from the request, decrypt and update the session object.
     * 
     * @param requestContext The request with the cookie
     */
    @ServerRequestFilter()
    public void inboundSessionFilter(ContainerRequestContext requestContext) {

        // Get hold of the host name from the request
        js.host = requestContext.getUriInfo().getBaseUri().getHost();

        // Get the cookie from the request, decrypt it and create the session
        Cookie c = requestContext.getCookies().get(JanusSessionHelper.COOKIE_NAME_SESSION);
        if (c != null) {
            log.info("Create JanusSession from Cookie");
            try {
                JanusSessionPOJO ljs = jsh.createSession(c.getValue());
                js.createFromPOJO(ljs);
            } catch (Exception e) {
                log.warn("Exception for decryption or parsing the cookie, force rewrite of a new cookie : "
                        + e.getLocalizedMessage());
                js.setChanged(true); // Force a rewrite of a new cookie
            }
        } else {
            log.info("No cookie to create JanusSession with, force write of new cookie");
            js.setChanged(true); // Force rewrite of new cookie
        }
    }

    /**
     * Convert the session object to a cookie, encrypt it and send it with the outbound request.
     * 
     * @param responseContext The response with added cookie.
     */
    @ServerResponseFilter()
    public void outboundSessionFilter(ContainerResponseContext responseContext) {
        if (js.getChanged()) {
            try {
                js.newTimeStamp();
                NewCookie nc = jsh.createSessionCookie(js.convertToPOJO(), js.host);
                String c = nc.toString();
                if (SAMESITE != null)
                    c = c + ";SameSite=" + SAMESITE;
                responseContext.getHeaders().add("Set-Cookie", c);
                js.setChanged(false);
            } catch (Exception e) {
                log.error("Exception for encryption or writing the cookie : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
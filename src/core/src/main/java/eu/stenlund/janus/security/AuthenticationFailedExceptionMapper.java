package eu.stenlund.janus.security;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import io.quarkus.security.AuthenticationFailedException;

/**
 * The authentication failed exception mapper, so we return a 401 and so we
 * redirect to the correct login form whenever it happens.
 *
 * @author Tomas Stenlund
 * @since 2022-07-11
 * 
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFailedExceptionMapper implements ExceptionMapper<AuthenticationFailedException> {

    @Context
    UriInfo uriInfo;

    private static final Logger log = Logger.getLogger(AuthenticationFailedExceptionMapper.class);

    /*
     * Make sure we notify the client that we were not authenticated.
     * 
     * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
     */
    @Override
    public Response toResponse(AuthenticationFailedException exception) {
        /*
        return Response.status(401).
            header("WWW-Authenticate", "Basic realm=\"janus\"").
            header("X-Up-Dismiss-Layer", "null").
            build();
            */
        return Response.status(401).
            header("WWW-Authenticate", "Basic realm=\"janus\"").
            header("X-Up-Dismiss-Layer", "null").
            build();

    }
}

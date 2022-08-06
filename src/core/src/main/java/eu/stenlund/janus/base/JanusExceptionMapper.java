package eu.stenlund.janus.base;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import io.quarkus.qute.Qute;

/**
 * The Janus exception mapper, so we return an error page with a return URL.
 *
 * @author Tomas Stenlund
 * @since 2022-08-06
 * 
 */
@Provider
@Priority(Priorities.USER)
public class JanusExceptionMapper implements ExceptionMapper<JanusException> {

    @Context
    UriInfo uriInfo;

    private static final Logger log = Logger.getLogger(JanusExceptionMapper.class);

    @Override
    public Response toResponse(JanusException exception) {

        String msg = Qute.fmt("{#include error.html/}")
            .data("exception", exception).render();

        return Response.ok(msg).
            header("X-Up-Dismiss-Layer", "null").
            build();
    }
}

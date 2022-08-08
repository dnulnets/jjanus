package eu.stenlund.janus.base;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Context
    UriInfo uriInfo;

    /**
     * The session object for the application, it comes with every request as a cookie.
     */
    @Inject
    JanusSession js;

    private static final Logger log = Logger.getLogger(RuntimeException.class);

    @Override
    public Response toResponse(RuntimeException exception) {;

        // Get hold of the sessions locale
        Locale tag = Locale.forLanguageTag(js.getLocale());

        // Get hold of the stack trace
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        // Decide what template to use and do formatting with qute
        String template = "{#include runtimeexception.html/}";
        String msg = Qute.fmt(template)
            .attribute("locale", tag)
            .data("back", uriInfo.getBaseUri().toString())
            .data("stack", sw.toString())
            .data("exception", exception)
            .render();
        
        /* OK message */
        return Response.ok(msg, MediaType.TEXT_HTML).
            header("X-Up-Dismiss-Layer", "null").
            build();

    }
}

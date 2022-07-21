package eu.stenlund.janus.base;

import java.util.Locale;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;

/**
 * A helper class for various qute template functions to make it simpler to
 * develop the Janus application.
 *
 * @author Tomas Stenlund
 * @since 2022-07-18
 * 
 */
public abstract class JanusTemplateHelper {

    /**
     * Creates a stream from a TemplateInstance
     * 
     * @param ti The template instance to render asynchronous, via a stream
     * @param cc Locale to use when rendering
     * @return A string stream for asynchronous rendering
     */
    public static Uni<String> createStringFrom(TemplateInstance ti, String cc) {
        Locale tag = Locale.forLanguageTag(cc);
        return ti.setAttribute("locale", tag).createUni();
    }

    /**
     * Creates a stream of RestResponses from a TemplateInstance via its completion
     * stage.
     * 
     * @param ti The template instance to render asynchronous, via a stream
     * @param cc Locale to use when rendering
     * @return A RestResponse stream for asynchronous rendering
     */
    public static Uni<RestResponse<String>> createResponseFrom(TemplateInstance ti, String cc) {
        return createStringFrom(ti, cc).map(item -> ResponseBuilder.ok(item).build());
    }

}

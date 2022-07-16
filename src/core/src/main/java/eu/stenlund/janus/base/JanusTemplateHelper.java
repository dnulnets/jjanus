package eu.stenlund.janus.base;

import java.util.Locale;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;

public abstract class JanusTemplateHelper {
    
    private static final Logger log = Logger.getLogger(JanusTemplateHelper.class);

    /**
     * Creates a stream from a TemplateInstance via its completion stage.
     * 
     * @param ti The template instance to render asynchronous, via a stream
     * @return A string stream for asynchronous rendering
     */
    public static Uni<String> createStringFrom(TemplateInstance ti, String cc)
    {             
        Locale tag = Locale.forLanguageTag(cc);
        log.info ("Locale = " + tag.toString());
        return Uni.createFrom()
            .completionStage(() -> ti.setAttribute("locale", tag).renderAsync());
    }

    /**
     * Creates a stream of RestResponses from a TemplateInstance via its completion stage.
     * 
     * @param ti The template instance to render asynchronous, via a stream
     * @return A RestResponse stream for asynchronous rendering
     */
    public static Uni<RestResponse<String>> createResponseFrom(TemplateInstance ti, String cc)
    {             
        return createStringFrom (ti, cc).onItem().transform(item -> ResponseBuilder.ok(item).build());
    }

}

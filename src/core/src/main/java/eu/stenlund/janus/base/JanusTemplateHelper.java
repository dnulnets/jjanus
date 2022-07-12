package eu.stenlund.janus.base;

import java.util.Locale;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;

public abstract class JanusTemplateHelper {
    
    /**
     * Creates a stream from a TemplateInstance via its completion stage.
     * 
     * @param ti The template instance to render asynchronous, via a stream
     * @return A string stream for asynchronous rendering
     */
    public static Uni<String> createStringFrom(TemplateInstance ti)
    {             
        Locale tag = Locale.forLanguageTag("en");
        return Uni.createFrom()
            .completionStage(() -> ti.setAttribute("locale", tag).renderAsync());
    }

    /**
     * Creates a stream of RestResponses from a TemplateInstance via its completion stage.
     * 
     * @param ti The template instance to render asynchronous, via a stream
     * @return A RestResponse stream for asynchronous rendering
     */
    public static Uni<RestResponse<String>> createResponseFrom(TemplateInstance ti)
    {             
        return createStringFrom (ti).onItem().transform(item -> ResponseBuilder.ok(item).build());
    }

}

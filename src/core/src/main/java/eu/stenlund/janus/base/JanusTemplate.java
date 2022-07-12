package eu.stenlund.janus.base;

import java.util.Locale;

import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;

public abstract class JanusTemplate {
    
    /**
     * Creates a stream from a TemplateInstance via its completion stage.
     * 
     * @param ti The template instance to render asynchronous, via a stream
     * @return A stream for asynchronous rendering
     */
    public static Uni<String> createFrom(TemplateInstance ti)
    {             
        Locale tag = Locale.forLanguageTag("en");
        return Uni.createFrom()
            .completionStage(() -> ti.setAttribute("locale", tag).renderAsync());
    }
}

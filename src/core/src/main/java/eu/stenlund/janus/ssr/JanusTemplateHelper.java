package eu.stenlund.janus.ssr;

import java.util.Locale;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import io.quarkus.arc.Arc;
import io.quarkus.arc.InstanceHandle;
import io.quarkus.qute.Qute;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.i18n.Localized;
import io.quarkus.qute.i18n.MessageBundle;
import io.smallrye.mutiny.Uni;

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
     * Fetch the message bundle and returns with it, if not found return with default bundle.
     * 
     * @param bundleInterface The class of the bundle.
     * @param locale The locale string.
     * @return
     */
    public static <T> T getMessageBundle(Class<T> bundleInterface, String locale) {
        Localized localized = Localized.Literal.of(locale);
        if (!bundleInterface.isInterface()) {
            throw new IllegalArgumentException("Not a message bundle interface: " + bundleInterface.getName());
        }
        if (!bundleInterface.isAnnotationPresent(MessageBundle.class)
                && !bundleInterface.isAnnotationPresent(Localized.class)) {
            throw new IllegalArgumentException(
                    "Message bundle interface must be annotated either with @MessageBundle or with @Localized: "
                            + bundleInterface.getName());
        }

        // Try to first get the full locale
        InstanceHandle<T> handle = Arc.container().instance(bundleInterface, localized);
        if (handle.isAvailable()) {
            return handle.get();
        }

        // Try with the language from the locale only
        Locale l = Locale.forLanguageTag(locale);
        localized = Localized.Literal.of(l.getLanguage());        
        handle = Arc.container().instance(bundleInterface, localized);
        if (handle.isAvailable()) {
            return handle.get();
        }

        // Get default fallback locale
        handle = Arc.container().instance(bundleInterface);
        if (handle.isAvailable()) {
            return handle.get();
        }
        throw new IllegalStateException(Qute.fmt(
                "Unable to obtain a message bundle for interface [{iface.name}]{#if loc} and locale [{loc.value}]{/if}")
                .data("iface", bundleInterface)
                .data("loc", localized)
                .render());
    }
    
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

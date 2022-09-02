package eu.stenlund.janus;

import java.util.function.Function;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import eu.stenlund.janus.base.JanusSession;
import eu.stenlund.janus.ssr.JanusTemplateHelper;
import eu.stenlund.janus.ssr.model.Base;
import eu.stenlund.janus.ssr.model.SearchList;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.smallrye.mutiny.Uni;

/**
 * The resource that handles searching of items.
 * 
 * @author Tomas Stenlund
 * @since 2022-08-18
 * 
 */
@Path("search")
@Produces(MediaType.TEXT_HTML)
@RequestScoped
public class Search {

    private static final Logger log = Logger.getLogger(Search.class);

    @Inject
    CurrentIdentityAssociation sia;

    @Inject
    Mutiny.SessionFactory sf;

    @Inject
    JanusSession js;

    /**
     * All of the checked templates for the product resource.
     */
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance search(Base base, SearchList workarea);
    }

    /**
     * List all items that fits the search pattern.
     * 
     * @return The list of all items.
     */
    @GET
    @RolesAllowed({"any"})
    public Uni<RestResponse<String>> search(@RestQuery("what") String what)
    {
        // Create the page
        return Uni.
            combine().all().unis(
                Base.createModel(sf, sia, js),
                SearchList.createModel(sf, what, js.getLocale())).
            combinedWith((base, model) -> JanusTemplateHelper.createResponseFrom(Templates.search(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

}

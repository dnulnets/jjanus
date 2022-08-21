package eu.stenlund.janus;

import java.net.URI;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.base.JanusSession;
import eu.stenlund.janus.ssr.JanusTemplateHelper;
import eu.stenlund.janus.ssr.workarea.Base;
import eu.stenlund.janus.ssr.workarea.ProductVersionManagementList;
import eu.stenlund.janus.ssr.workarea.ProductVersionManagementProductVersion;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.smallrye.mutiny.Uni;

/**
 * The resource for product version management. It handles product versions and products.
 * 
 * @author Tomas Stenlund
 * @since 2022-08-01
 * 
 */
@Path("productversion")
@Produces(MediaType.TEXT_HTML)
@RequestScoped
public class ProductVersionManagement {

    private static final Logger log = Logger.getLogger(ProductManagement.class);

    @Inject
    CurrentIdentityAssociation securityIdentityAssociation;

    @Inject
    Mutiny.SessionFactory sf;

    @Inject
    JanusSession js;

    /**
     * All of the checked templates for the product version resource.
     */
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance list(Base base, ProductVersionManagementList workarea);
        public static native TemplateInstance productversion(Base base, ProductVersionManagementProductVersion workarea);
    }

    /**
     * List all of the product versions in the database.
     * 
     * @return The list of all product versions
     */
    @GET
    @Path("list")
    @RolesAllowed({"admin"})
    public Uni<RestResponse<String>> list(  @RestQuery("six") String ssix,
                                            @RestQuery("max") String smax)
    {
        int six=0, max=js.getListSize();

        // Check validity and set default values if nessescary
        if (!JanusHelper.isBlank(ssix))
            try {
                six = Integer.parseUnsignedInt(ssix);
            } catch (NumberFormatException e) {
                six = 0;
            }
        if (!JanusHelper.isBlank(smax))
            try {
                max = Integer.parseUnsignedInt(smax);
            } catch (NumberFormatException e) {
                max = js.getListSize();
            }

        // Create the page
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                ProductVersionManagementList.createModel(sf, six, max, js.getLocale())
            ).combinedWith((base, model)->JanusTemplateHelper.createResponseFrom(Templates.list(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Show the product version data and a ui that allows you to change certain attributes of the product version.
     * 
     * @return The product page
     */
    @GET
    @Path("")
    @RolesAllowed({"admin"})
    public Uni<RestResponse<String>> product(@RestQuery("uuid") UUID id,
                                            @RestQuery("return") URI uri)
    {
        // Check that we got the id
        if (id==null || uri==null)
            throw new BadRequestException();

        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                ProductVersionManagementProductVersion.createModel(sf, id, uri, js.getLocale())).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.productversion(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Updates the product versions values.
     * 
     * @return The updated product versions list page
     */
    @POST
    @Path("")
    @RolesAllowed({"admin"})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<RestResponse<String>> product(@RestForm UUID uuid,
                                            @RestForm UUID product,
                                            @RestForm String version,
                                            @RestForm UUID state,
                                            @RestForm Boolean closed)
    {
        // We need data for all of the fields
        if (JanusHelper.isBlank(version) || uuid ==null)
            throw new IllegalArgumentException("Missing required data");
        
        // Clean up the datat
        if (closed==null)
            closed = Boolean.FALSE;

        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                ProductVersionManagementProductVersion.updateProductVersion(sf, uuid, version, product, state, closed).
                    chain(user -> ProductVersionManagementList.createModel(sf, 0, js.getListSize(), js.getLocale()))).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.list(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Creates a new prodyct version page.
     * 
     * @return The new product version page.
     */
    @GET
    @Path("create")
    @RolesAllowed({"admin"})
    public Uni<RestResponse<String>> create(@RestQuery("return") URI uri)

    {
        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                ProductVersionManagementProductVersion.createModel(sf, null, uri, js.getLocale())).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.productversion(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Creates a new product version and returns with the updated product versions list.
     * 
     * @return The updated product versions page
     */
    @POST
    @Path("create")
    @RolesAllowed({"admin"})
    public Uni<RestResponse<String>> create(@RestForm UUID product,
                                            @RestForm String version,
                                            @RestForm UUID state,
                                            @RestForm Boolean closed)
    {
        // We need data for all of the fields
        if (JanusHelper.isBlank(version))
            throw new BadRequestException("Missing required data");

        // Clean up data
        if (closed == null)
            closed = Boolean.FALSE;

        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                ProductVersionManagementProductVersion.createProductVersion(sf, null, version, product, state, closed).
                    chain(user->ProductVersionManagementList.createModel(sf, 0, js.getListSize(),js.getLocale()))).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.list(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Deletes a product version and returns with the updated product versions list.
     * 
     * @return The updated product versions list.
     */
    @POST
    @Path("delete")
    @RolesAllowed({"admin"})
    public Uni<RestResponse<String>> delete(@RestForm UUID uuid)
    {
        // Check that we got the id
        if (uuid==null)
            throw new BadRequestException("Missing required data");

        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                ProductVersionManagementProductVersion.deleteProductVersion(sf, uuid).
                    chain(()->ProductVersionManagementList.createModel(sf, 0, js.getListSize(), js.getLocale()))).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.list(base,model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }


}

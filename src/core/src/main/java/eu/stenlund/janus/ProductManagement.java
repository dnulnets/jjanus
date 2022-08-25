package eu.stenlund.janus;

import java.net.URI;
import java.util.Optional;
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
import eu.stenlund.janus.base.MaybeUUID;
import eu.stenlund.janus.ssr.JanusTemplateHelper;
import eu.stenlund.janus.ssr.workarea.Base;
import eu.stenlund.janus.ssr.workarea.ProductManagementList;
import eu.stenlund.janus.ssr.workarea.ProductManagementProduct;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.smallrye.mutiny.Uni;

/**
 * The resource for Product management. It handles products and versions.
 * 
 * @author Tomas Stenlund
 * @since 2022-08-18
 * 
 */
@Path("product")
@Produces(MediaType.TEXT_HTML)
@RequestScoped
public class ProductManagement {

    private static final Logger log = Logger.getLogger(ProductManagement.class);

    @Inject
    CurrentIdentityAssociation securityIdentityAssociation;

    @Inject
    Mutiny.SessionFactory sf;

    @Inject
    JanusSession js;

    /**
     * All of the checked templates for the product resource.
     */
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance list(Base base, ProductManagementList workarea);
        public static native TemplateInstance product(Base base, ProductManagementProduct workarea);
    }

    /**
     * List all of the products in the database.
     * 
     * @return The list of all products
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
                ProductManagementList.createModel(sf, six, max, js.getLocale())).
            combinedWith((base, model) -> JanusTemplateHelper.createResponseFrom(Templates.list(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Show the product data and a ui that allows you to change certain attributes of the product
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
                ProductManagementProduct.createModel(sf, id, uri, js.getLocale())).
            combinedWith((base, model) -> JanusTemplateHelper.createResponseFrom(Templates.product(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Updates the products values and returns with a list of all products.
     * 
     * @return The updated product list page.
     */
    @POST
    @Path("")
    @RolesAllowed({"admin"})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<RestResponse<String>> product(@RestForm UUID uuid,
                                            @RestForm String name,
                                            @RestForm String description,
                                            @RestForm UUID[] teams,
                                            @RestForm MaybeUUID current)
    {
        // We need data for all of the fields
        if (JanusHelper.isBlank(name) || uuid ==null)
            throw new IllegalArgumentException("Missing required data");

        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                ProductManagementProduct.updateProduct(sf, uuid, name, description, teams, current.orElse(null)).
                    chain(user -> ProductManagementList.createModel(sf, 0, js.getListSize(), js.getLocale()))).
            combinedWith((base, model) -> JanusTemplateHelper.createResponseFrom(Templates.list(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Creates a new product page.
     * 
     * @return The new product page
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
                ProductManagementProduct.createModel(sf, null, uri, js.getLocale())).
            combinedWith((base, model)->JanusTemplateHelper.createResponseFrom(Templates.product(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Creates a new product and returns with an updated product list.
     * 
     * @return The updated product list page.
     */
    @POST
    @Path("create")
    @RolesAllowed({"admin"})
    public Uni<RestResponse<String>> create(@RestForm String name,
                                            @RestForm String description,
                                            @RestForm UUID[] teams)
    {
        // We need data for all of the fields
        if (JanusHelper.isBlank(name))
            throw new BadRequestException("Missing required data");
        if (description == null)
            description = "";

        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                ProductManagementProduct.createProduct(sf, name, description, teams).
                    chain(user->ProductManagementList.createModel(sf, 0, js.getListSize(),js.getLocale()))).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.list(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Deletes a product and returns with an updated product list page.
     * 
     * @return The updated product list page.
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
                ProductManagementProduct.deleteProduct(sf, uuid).
                    chain(()->ProductManagementList.createModel(sf, 0, js.getListSize(), js.getLocale()))).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.list(base,model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());

    }

}

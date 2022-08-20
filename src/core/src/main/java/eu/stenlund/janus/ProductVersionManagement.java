package eu.stenlund.janus;

import java.net.URI;
import java.util.UUID;

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
import eu.stenlund.janus.ssr.workarea.ProductManagementList;
import eu.stenlund.janus.ssr.workarea.ProductManagementProduct;
import eu.stenlund.janus.ssr.workarea.ProductVersionManagementList;
import eu.stenlund.janus.ssr.workarea.ProductVersionManagementProductVersion;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.smallrye.mutiny.Uni;

/**
 * The resource for Team management. It handles teams and adding/removing users.
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
     * All of the checked templates for the Start resource.
     */
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance list(Base base, ProductVersionManagementList workarea);
        public static native TemplateInstance productversion(Base base, ProductVersionManagementProductVersion workarea);
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
                ProductVersionManagementList.createModel(sf, six, max, js.getLocale())
            ).asTuple().
            chain(t -> JanusTemplateHelper.createResponseFrom(Templates.list(t.getItem1(), t.getItem2()), js.getLocale())).
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
                ProductVersionManagementProductVersion.createModel(sf, id, uri, js.getLocale())
            ).asTuple().
            chain(t -> {
                log.info(t.getItem1().toString());
                log.info(t.getItem2().version.value);
                return JanusTemplateHelper.createResponseFrom(Templates.productversion(t.getItem1(), t.getItem2()), js.getLocale());
            }).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Updates the products values.
     * 
     * @return The team list page
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

        return Uni.combine().all().unis(
            securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
            ProductVersionManagementProductVersion.updateProductVersion(sf, uuid, version, product, state, closed).
                chain(user -> ProductVersionManagementList.createModel(sf, 0, js.getListSize(), js.getLocale()))
        ).asTuple().
        chain(t -> JanusTemplateHelper.createResponseFrom(Templates.list(t.getItem1(), t.getItem2()), js.getLocale())).
        onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Creates a new team page.
     * 
     * @return The team page
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
                ProductVersionManagementProductVersion.createModel(sf, null, uri, js.getLocale())
            ).asTuple().
            chain(t -> JanusTemplateHelper.createResponseFrom(Templates.productversion(t.getItem1(), t.getItem2()), js.getLocale())).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

        /**
     * Show the user data and a ui that allows you to change certain aspects of the user.
     * 
     * @return The user page
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
                    chain(user->ProductVersionManagementList.createModel(sf, 0, js.getListSize(),js.getLocale()))).asTuple().
            chain(t -> JanusTemplateHelper.createResponseFrom(Templates.list(t.getItem1(), t.getItem2()), js.getLocale())).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Show the user data and a ui that allows you to change certain aspects of the user.
     * 
     * @return The user page
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
                    chain(()->ProductVersionManagementList.createModel(sf, 0, js.getListSize(), js.getLocale()))).asTuple().
            chain(t -> JanusTemplateHelper.createResponseFrom(Templates.list(t.getItem1(),
                    t.getItem2()), js.getLocale())).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }


}

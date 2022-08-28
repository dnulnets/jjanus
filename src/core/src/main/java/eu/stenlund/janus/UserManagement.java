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
import eu.stenlund.janus.ssr.model.Base;
import eu.stenlund.janus.ssr.model.UserList;
import eu.stenlund.janus.ssr.model.UserPage;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.smallrye.mutiny.Uni;

/**
 * The resource for the User management. It handles users and roles.
 * 
 * @author Tomas Stenlund
 * @since 2022-07-19
 * 
 */
@Path("user")
@Produces(MediaType.TEXT_HTML)
@RequestScoped
public class UserManagement {

    private static final Logger log = Logger.getLogger(UserManagement.class);

    @Inject
    CurrentIdentityAssociation securityIdentityAssociation;

    @Inject
    Mutiny.SessionFactory sf;

    @Inject
    JanusSession js;

    /**
     * All of the checked templates for the user resource.
     */
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance list(Base base, UserList workarea);
        public static native TemplateInstance user(Base base, UserPage workarea);
    }

    /**
     * List all of the users in the database.
     * 
     * @return The list of all users
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
                UserList.createModel(sf, six, max, js.getLocale())).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.list(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Show the user data and a ui that allows you to change certain aspects of the user.
     * 
     * @return The user page
     */
    @GET
    @Path("")
    @RolesAllowed({"admin"})
    public Uni<RestResponse<String>> user(@RestQuery("uuid") UUID id,
                                            @RestQuery("return") URI uri)
    {
        // Check that we got the id
        if (id==null)
            throw new BadRequestException();

        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                UserPage.createModel(sf, id, uri, js.getLocale())).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.user(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Updates the user and return with an updated user list.
     * 
     * @return The updated user list page.
     */
    @POST
    @Path("")
    @RolesAllowed({"admin"})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<RestResponse<String>> user(@RestForm UUID uuid,
                                            @RestForm String username,
                                            @RestForm String name,
                                            @RestForm String email,
                                            @RestForm UUID[] roles,
                                            @RestForm UUID[] teams,
                                            @RestForm String password)
    {
        // We need data for all of the fields
        if (JanusHelper.isBlank(name) || JanusHelper.isBlank(username) || JanusHelper.isBlank(email) || uuid ==null)
            throw new IllegalArgumentException("Missing required data");

        return Uni.combine().all().unis(
            securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
            UserPage.updateUser(sf, uuid, username, name, email, roles, teams, password).
                chain(user -> UserList.createModel(sf, 0, js.getListSize(), js.getLocale()))).
        combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.list(base, model), js.getLocale())).
        flatMap(Function.identity()).
        onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Creates a new user page.
     * 
     * @return The user page
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
                UserPage.createModel(sf, null, uri, js.getLocale())).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.user(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Creates a new user and returns with an updated user list page.
     * 
     * @return The updated user list page.
     */
    @POST
    @Path("create")
    @RolesAllowed({"admin"})
    public Uni<RestResponse<String>> create(@RestForm String username,
                                            @RestForm String name,
                                            @RestForm String email,
                                            @RestForm UUID[] roles,
                                            @RestForm UUID[] teams,
                                            @RestForm String password)
    {
        // We need data for all of the fields
        if (JanusHelper.isBlank(password) || JanusHelper.isBlank(name) || JanusHelper.isBlank(username) || JanusHelper.isBlank(email))
            throw new IllegalArgumentException("Missing required data");

        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                UserPage.createUser(sf, username, name, email, roles, teams, password).
                    chain(user->UserList.createModel(sf, 0, js.getListSize(),js.getLocale()))).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.list(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Deletes a user and returns with the updated user list page.
     * 
     * @return The updated user list page.
     */
    @POST
    @Path("delete")
    @RolesAllowed({"admin"})
    public Uni<RestResponse<String>> delete(@RestForm UUID uuid)
    {
        // Check that we got the id
        if (uuid==null)
            throw new IllegalArgumentException("Missing required data");

        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                UserPage.deleteUser(sf, uuid).
                    chain(()->UserList.createModel(sf, 0, js.getListSize(), js.getLocale()))).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.list(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }
}

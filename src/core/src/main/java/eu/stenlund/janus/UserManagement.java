package eu.stenlund.janus;

import java.net.URI;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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
import eu.stenlund.janus.base.JanusTemplateHelper;
import eu.stenlund.janus.model.workarea.Base;
import eu.stenlund.janus.model.workarea.UserManagementList;
import eu.stenlund.janus.model.workarea.UserManagementUser;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.smallrye.mutiny.Uni;

/**
 * The resource for the User management.
 * 
 * @author Tomas Stenlund
 * @since 2022-07-19
 * 
 */
@Path("user")
@Produces(MediaType.TEXT_HTML)
@RequestScoped
public class UserManagement {

    private static final Logger log = Logger.getLogger(Start.class);

    @Inject
    CurrentIdentityAssociation securityIdentityAssociation;

    @Inject
    Mutiny.SessionFactory sf;

    @Inject
    JanusSession js;

    /**
     * Maximum numbr of rows for tables in the user interface, as a string, see MAX_LIST_SIZE
     */
    private static final String MAX_LIST_SIZE_STRING = "5";


    /**
     * Maximum number of rows for tables in the user interface, as an int, see MAX_LIST_SIZE_STRING.
     */
    private static final int MAX_LIST_SIZE = 5;

    /**
     * All of the checked templates for the Start resource.
     */
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance list(Base base, UserManagementList workarea);
        public static native TemplateInstance user(Base base, UserManagementUser workarea);
        public static native TemplateInstance error(Base base);
    }

    /**
     * List all of the users in the database.
     * 
     * @return The list of all users
     */
    @GET
    @Path("list")
    @RolesAllowed({"admin"})
    public Uni<RestResponse<String>> list(  @DefaultValue ("0") @RestQuery("start") int start,
                                            @DefaultValue (MAX_LIST_SIZE_STRING) @RestQuery("max") int max)
    {
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                UserManagementList.createUserManagementList(sf, start, max)
            ).asTuple().
            chain(t -> JanusTemplateHelper.createResponseFrom(Templates.list(t.getItem1(), t.getItem2()), js.getLocale())).
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
        if (id==null || uri==null)
            throw new BadRequestException();

        log.info ("URL = " + uri.toString());

        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                UserManagementUser.createUserManagementUser(sf, id, uri)
            ).asTuple().
            chain(t -> JanusTemplateHelper.createResponseFrom(Templates.user(t.getItem1(), t.getItem2()), js.getLocale())).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());

    }

    /**
     * Show the user data and a ui that allows you to change certain aspects of the user.
     * 
     * @return The user page
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
                                            @RestForm String password)
    {
        // We need data for all of the fields
        if (!(JanusHelper.isValid(name) && JanusHelper.isValid(username) && JanusHelper.isValid(email) && uuid !=null))
            throw new BadRequestException();

        return Uni.combine().all().unis(
            securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
            UserManagementUser.updateUser(sf, uuid, username, name, email, roles, password).
                chain(user -> UserManagementList.createUserManagementList(sf, 0, MAX_LIST_SIZE))
        ).asTuple().
        chain(t -> JanusTemplateHelper.createResponseFrom(Templates.list(t.getItem1(), t.getItem2()), js.getLocale())).
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
        log.info ("URL = " + uri.toString());

        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                UserManagementUser.createUserManagementUser(sf, null, uri)
            ).asTuple().
            chain(t -> JanusTemplateHelper.createResponseFrom(Templates.user(t.getItem1(), t.getItem2()), js.getLocale())).
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
    public Uni<RestResponse<String>> create(@RestForm String username,
                                            @RestForm String name,
                                            @RestForm String email,
                                            @RestForm UUID[] roles,
                                            @RestForm String password)
    {
        // We need data for all of the fields
        if (!(JanusHelper.isValid(password) && JanusHelper.isValid(name) && JanusHelper.isValid(username) && JanusHelper.isValid(email)))
            throw new BadRequestException();

        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                UserManagementUser.createUser(sf, username, name, email, roles, password).
                    chain(user->UserManagementList.createUserManagementList(sf, 0, MAX_LIST_SIZE))).asTuple().
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
            throw new BadRequestException();

        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                UserManagementUser.deleteUser(sf, uuid).
                    chain(()->UserManagementList.createUserManagementList(sf, 0, MAX_LIST_SIZE))).asTuple().
            chain(t -> JanusTemplateHelper.createResponseFrom(Templates.list(t.getItem1(),
                    t.getItem2()), js.getLocale())).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }
}

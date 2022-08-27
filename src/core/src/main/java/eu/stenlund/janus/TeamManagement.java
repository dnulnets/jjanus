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
import eu.stenlund.janus.ssr.workarea.TeamManagementList;
import eu.stenlund.janus.ssr.workarea.TeamManagementTeam;
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
@Path("team")
@Produces(MediaType.TEXT_HTML)
@RequestScoped
public class TeamManagement {

    private static final Logger log = Logger.getLogger(TeamManagement.class);

    @Inject
    CurrentIdentityAssociation securityIdentityAssociation;

    @Inject
    Mutiny.SessionFactory sf;

    @Inject
    JanusSession js;

    /**
     * All of the checked templates for the team resource.
     */
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance list(Base base, TeamManagementList workarea);
        public static native TemplateInstance team(Base base, TeamManagementTeam workarea);
    }

    /**
     * List all of the teams in the database.
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
                TeamManagementList.createModel(sf, six, max, js.getLocale())
            ).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.list(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Show the team data and a ui that allows you to change certain aspects of the team.
     * 
     * @return The team page
     */
    @GET
    @Path("")
    @RolesAllowed({"admin"})
    public Uni<RestResponse<String>> team(@RestQuery("uuid") UUID id,
                                            @RestQuery("return") URI uri)
    {
        // Check that we got the id
        if (id==null || uri==null)
            throw new BadRequestException();

        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                TeamManagementTeam.createModel(sf, id, uri, js.getLocale())).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.team(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Updates the teams values.
     * 
     * @return The team list page
     */
    @POST
    @Path("")
    @RolesAllowed({"admin"})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Uni<RestResponse<String>> team(@RestForm UUID uuid,
                                            @RestForm String name)
    {
        // We need data for all of the fields
        if (JanusHelper.isBlank(name) || uuid ==null)
            throw new IllegalArgumentException("Missing required data");

        return Uni.combine().all().unis(
            securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
            TeamManagementTeam.updateTeam(sf, uuid, name).
                chain(user -> TeamManagementList.createModel(sf, 0, js.getListSize(), js.getLocale()))).
        combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.list(base, model), js.getLocale())).
        flatMap(Function.identity()).
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
                TeamManagementTeam.createModel(sf, null, uri, js.getLocale())).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.team(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Creates a new team and return with an updated list of teams.
     * 
     * @return The updated team list page
     */
    @POST
    @Path("create")
    @RolesAllowed({"admin"})
    public Uni<RestResponse<String>> create(@RestForm String name)
    {
        // We need data for all of the fields
        if (JanusHelper.isBlank(name))
            throw new BadRequestException("Missing required data");

        // Return with a user interface
        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                TeamManagementTeam.createTeam(sf, name).
                    chain(user->TeamManagementList.createModel(sf, 0, js.getListSize(),js.getLocale()))).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.list(base, model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Delete a team and return with an updated team list.
     * 
     * @return The updated team list page.
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
                TeamManagementTeam.deleteTeam(sf, uuid).
                    chain(()->TeamManagementList.createModel(sf, 0, js.getListSize(), js.getLocale()))).
            combinedWith((base,model)->JanusTemplateHelper.createResponseFrom(Templates.list(base,model), js.getLocale())).
            flatMap(Function.identity()).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }
}

package eu.stenlund.janus;

import java.net.URI;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import eu.stenlund.janus.base.JanusSession;
import eu.stenlund.janus.base.JanusTemplateHelper;
import eu.stenlund.janus.model.User;
import eu.stenlund.janus.model.ui.Base;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;
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
     * All of the checked templates for the Start resource.
     */
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance list(Base navbar, List<User> users);
    }

    /**
     * List all of the users in the database.
     * 
     * @return The list of all users
     */
    @GET
    @Path("list")
    @RolesAllowed({"admin"})
    public Uni<RestResponse<String>> list() {

        return Uni.
            combine().all().unis(
                securityIdentityAssociation.getDeferredIdentity().map(si -> new Base(si)),
                sf.withSession(s -> User.getListOfUsers(s, 0, 5))).asTuple().
            chain(t -> JanusTemplateHelper.createResponseFrom(Templates.list(t.getItem1(),
                    t.getItem2()), js.getLocale())).
            onFailure().invoke(t -> ResponseBuilder.serverError().build());

    }

}

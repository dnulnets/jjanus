package eu.stenlund.janus;

import java.net.URI;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.CompletionStage;

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
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import eu.stenlund.janus.base.JanusTemplateHelper;
import eu.stenlund.janus.model.Role;
import eu.stenlund.janus.model.User;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;

@Path("janus")
@Produces(MediaType.TEXT_HTML)
@RequestScoped
public class Start {

    private static final Logger log = Logger.getLogger(Start.class);

    @ConfigProperty(name = "quarkus.http.auth.form.cookie-name")
    String COOKIE_NAME;

    @ConfigProperty(name = "quarkus.http.auth.form.location-cookie")
    String REDIRECT_COOKIE_NAME;

    @ConfigProperty(name = "quarkus.http.auth.form.login-page")
    String LOGIN_PAGE;

    @Inject
    public CurrentIdentityAssociation securityIdentityAssociation;

    @Inject
    Mutiny.SessionFactory sf;
    
    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance start();
        public static native TemplateInstance login();
        public static native TemplateInstance auth_error();
        public static native TemplateInstance fragment1();
        public static native TemplateInstance fragment2();
    }

    @GET
    @Path("start")
    @RolesAllowed({"user"})
    public Uni<RestResponse<String>> start() {

        Uni<SecurityIdentity> di = securityIdentityAssociation.getDeferredIdentity();
        
        return di.chain(item -> JanusTemplateHelper.createResponseFrom(Templates.start()))
                 .onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }
    
    /**
     * @return
     */
    @GET
    @Path("login")
    public Uni<RestResponse<String>> login() {
        return JanusTemplateHelper.createResponseFrom(Templates.login())
            .onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Remove the cookies associated with the auth form authentication and redirect to the
     * login page.
     * 
     * @return Response with a redirect to the login page
     */
    @GET
    @Path("logout")
    public RestResponse<Object> logout() {
        return ResponseBuilder.seeOther(URI.create(LOGIN_PAGE))
            .cookie(new NewCookie(REDIRECT_COOKIE_NAME, null, "/","","",0,true))
            .cookie(new NewCookie(COOKIE_NAME, null, "/", "", "", 0, true))
            .build();
    }

    @GET
    @Path("auth_error")
    public Uni<String> auth_error() {
        return JanusTemplateHelper.createStringFrom(Templates.auth_error());
    }

    @GET
    @Path("fragment1")
    public Uni<String> fragment1() {

        // Just testcreate something for hibernate
        User newUser = new User();
        newUser.username = "tomas";
        newUser.name = "Tomas Stenlund";
        newUser.email = "tomas.stenlund@telia.com";
        newUser.roles = new HashSet<Role>();
        newUser.setPassword("mandelmassa");

        // Return with the final asynch
        return sf.withTransaction((s,t) -> Role.findByName(s, "user")
                .chain(role -> {
                    newUser.roles.add(role);
                    return User.addUser(s, newUser);
                })
                .chain(item ->  JanusTemplateHelper.createStringFrom(Templates.fragment1())));
   }

    @GET
    @Path("fragment2")
    @RolesAllowed({"user"})
    public Uni<String> fragment2() {
        return JanusTemplateHelper.createStringFrom(Templates.fragment2());
    }

}

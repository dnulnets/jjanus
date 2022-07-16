package eu.stenlund.janus;

import java.net.URI;
import java.util.HashSet;
import java.util.Locale;

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
import eu.stenlund.janus.model.Role;
import eu.stenlund.janus.model.User;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;

@Path("")
@Produces(MediaType.TEXT_HTML)
@RequestScoped
public class Start {

    private static final Logger log = Logger.getLogger(Start.class);

    @ConfigProperty(name = "quarkus.http.auth.form.cookie-name")
    String COOKIE_NAME;

    @ConfigProperty(name = "quarkus.http.auth.form.location-cookie")
    String REDIRECT_COOKIE_NAME;

    @Inject
    public CurrentIdentityAssociation securityIdentityAssociation;

    @Inject
    Mutiny.SessionFactory sf;
    
    @Inject
    JanusSession js;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance start();
        public static native TemplateInstance login();
        public static native TemplateInstance auth_error();
        public static native TemplateInstance fragment_page1();
        public static native TemplateInstance fragment_page2();
    }

    @GET
    @Path("")
    public RestResponse<Object> redirect()
    {
        return ResponseBuilder.seeOther(URI.create("start")).build();
    }

    @GET
    @Path("start")
    @RolesAllowed({"user"})
    public Uni<RestResponse<String>> start() {

        Uni<SecurityIdentity> di = securityIdentityAssociation.getDeferredIdentity();
        
        log.info("Locale = " + js.getLocale());

        return di.map(si -> {
                log.info ("username: " + si.getPrincipal());
                log.info ("name: " + si.getAttribute("name"));
                log.info ("email: " + si.getAttribute("email"));
                log.info ("id: " + si.getAttribute("id"));
                return si;
            })
            .chain(item -> JanusTemplateHelper.createResponseFrom(Templates.start(), js.getLocale()))
            .onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }
    
    /**
     * @return
     */
    @GET
    @Path("login")
    public Uni<RestResponse<String>> login() {
        return JanusTemplateHelper.createResponseFrom(Templates.login(), js.getLocale())
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
        return ResponseBuilder.seeOther(URI.create("login"))
            .cookie(new NewCookie(REDIRECT_COOKIE_NAME, null, "/","","",0,true))
            .cookie(new NewCookie(COOKIE_NAME, null, "/", "", "", 0, true))
            .build();
    }

    @GET
    @Path("auth_error")
    public Uni<String> auth_error() {
        return JanusTemplateHelper.createStringFrom(Templates.auth_error(), js.getLocale());
    }

    @GET
    @Path("language")
    public RestResponse<Object> language(@RestQuery String country) {
        if (country != null) 
            js.setLocale(country);  
        return ResponseBuilder.seeOther(URI.create("login")).build();
    }

    @GET
    @Path("fragment_page1")
    @RolesAllowed ("user")
    public Uni<String> fragment_page1() {

        // Just testcreate something for hibernate
        /*
        User newUser = new User();
        newUser.username = "tomas";
        newUser.name = "Tomas Stenlund";
        newUser.email = "tomas.stenlund@telia.com";
        newUser.roles = new HashSet<Role>();
        newUser.setPassword("mandelmassa");
        return sf.withTransaction((s,t) -> Role.findByName(s, "user")
                .chain(role -> {
                    newUser.roles.add(role);
                    return User.addUser(s, newUser);
                })
                .chain(item ->  JanusTemplateHelper.createStringFrom(Templates.fragment1(), js.getLocale())));
                */

        return JanusTemplateHelper.createStringFrom(Templates.fragment_page1(), js.getLocale());

   }

    @GET
    @Path("fragment_page2")
    @RolesAllowed({"user"})
    public Uni<String> fragment_page2() {
        return JanusTemplateHelper.createStringFrom(Templates.fragment_page2(), js.getLocale());
    }

}

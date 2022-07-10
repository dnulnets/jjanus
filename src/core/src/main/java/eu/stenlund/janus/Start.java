package eu.stenlund.janus;

import java.net.URI;
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
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import eu.stenlund.janus.model.*;

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
        
        return di.onItem().transformToUni(item -> Uni.createFrom().completionStage(() -> Templates.start().setAttribute("locale", Locale.forLanguageTag("en")).renderAsync()))
                 .onItem().transform(item -> ResponseBuilder.ok(item).build())
                 .onFailure().invoke(t -> ResponseBuilder.serverError().build());
    }
    
    /**
     * @return
     */
    @GET
    @Path("login")
    public CompletionStage<String> login() {        
        return Templates.login().setAttribute("locale", Locale.forLanguageTag("en")).renderAsync();
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
            .cookie(new NewCookie(COOKIE_NAME, null, "/", "", "", 0, true)).build();
    }

    @GET
    @Path("auth_error")
    public CompletionStage<String> auth_error() {
        return Templates.auth_error().setAttribute("locale", Locale.forLanguageTag("en")).renderAsync();
    }

    @GET
    @Path("fragment1")
    @RolesAllowed({"user"})
    @ReactiveTransactional
    public Uni<String> fragment1() {

        // Just testcreate something for hibernate
        Identity newId = new Identity();
        newId.identity = "tomas";
        Identity newId2 = new Identity();
        newId2.identity = "gurkan";
        Person newUser = new Person();
        newUser.name = "Tomas Stenlund";
        newUser.email = "tomas.stenlund@telia.com";
        newUser.credential = "dehifewhidi";
        newId.user = newUser;
        newId2.user = newUser;

        // Create the asynch operations we want to do
        Uni<Person> dd = Panache.withTransaction(newUser::persist);
        Uni<Identity> d = Panache.withTransaction(newId::persist);
        Uni<Identity> d2 = Panache.withTransaction(newId2::persist);

        // Return with the final asynch
        return dd.chain(item -> d).chain(item->d2)
            .chain(item -> Uni.createFrom().completionStage(() -> Templates.fragment1().setAttribute("locale", Locale.forLanguageTag("en")).renderAsync()));

   }

    @GET
    @Path("fragment2")
    @RolesAllowed({"user"})
    public CompletionStage<String> fragment2() {
        return Templates.fragment2().renderAsync();
    }

}

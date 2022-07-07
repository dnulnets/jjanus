package eu.stenlund.janus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CompletionStage;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.CookieSameSite;
import io.quarkus.qute.Location;

@Path("janus")
@Produces(MediaType.TEXT_HTML)
@RequestScoped
public class Start {

    private static final Logger log = Logger.getLogger(Start.class);

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance start();
        public static native TemplateInstance login();
        public static native TemplateInstance auth_error();
        @Location("fragments/fragment1.html")
        public static native TemplateInstance fragment1();
        @Location("fragments/fragment2.html")
        public static native TemplateInstance fragment2();
    }

    @GET
    @Path("start")
    @RolesAllowed({"user"})
    public CompletionStage<String> start() {
        return Templates.start().renderAsync();
    }
    
    @GET
    @Path("login")
    public CompletionStage<String> login() {
        return Templates.login().renderAsync();
    }

    @GET
    @Path("auth_error")
    public CompletionStage<String> auth_error() {
        return Templates.auth_error().renderAsync();
    }

    @GET
    @Path("fragment1")
    @RolesAllowed({"user"})
    public CompletionStage<String> fragment1() {
        return Templates.fragment1().renderAsync();
    }

    @GET
    @Path("fragment2")
    @RolesAllowed({"user"})
    public CompletionStage<String> fragment2() {
        return Templates.fragment2().renderAsync();
    }

}

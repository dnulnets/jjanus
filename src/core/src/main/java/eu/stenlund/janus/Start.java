package eu.stenlund.janus;

import java.net.URI;

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
import eu.stenlund.janus.ssr.JanusTemplateHelper;
import eu.stenlund.janus.ssr.model.Base;
import eu.stenlund.janus.ssr.model.StartLogin;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.smallrye.mutiny.Uni;

/**
 * The resource for the Janus start pages, user login handling and language
 * support.
 * 
 * @author Tomas Stenlund
 * @since 2022-07-11
 * 
 */
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
    CurrentIdentityAssociation sia;

    @Inject
    Mutiny.SessionFactory sf;

    @Inject
    JanusSession js;

    /**
     * All of the checked templates for the Start resource.
     */
    @CheckedTemplate
    public static class Templates {

        /* Example pages, to be removed */
        public static native TemplateInstance start1(Base base);
        public static native TemplateInstance start2(Base base);

        /* Real pages, to be kept */
        public static native TemplateInstance login(StartLogin workarea);
        public static native TemplateInstance auth_error();
        public static native TemplateInstance extra();
    }

    /**
     * Redirect the browser to the start point of the application if they just use
     * the root URL.
     * 
     * @return A redirect response to root/start
     */
    @GET
    @Path("")
    public RestResponse<Object> redirect() {
        return ResponseBuilder.seeOther(URI.create("start1")).build();
    }

    /**
     * The start page of the application.
     * 
     * @return The start page
     */
    @GET
    @Path("start1")
    @RolesAllowed({ "any" })
    public Uni<RestResponse<String>> start1() {

        return Base.createModel(sf, sia, js)
                .chain(nb -> JanusTemplateHelper.createResponseFrom(Templates.start1(nb), js.getLocale()))
                .onFailure()
                    .invoke(t -> ResponseBuilder.serverError().build());
    }

    @GET
    @Path("start2")
    @RolesAllowed({ "any" })
    public Uni<RestResponse<String>> start2() {

        return Base.createModel(sf, sia, js)
                .chain(nb -> JanusTemplateHelper.createResponseFrom(Templates.start2(nb), js.getLocale()))
                .onFailure()
                    .invoke(t -> ResponseBuilder.serverError().build());
    }

    @GET
    @Path("extra")
    public Uni<String> extra() {
        return JanusTemplateHelper.createStringFrom(Templates.extra(), js.getLocale());
    }

    /**
     * The login page of the application
     * 
     * @return The login page
     */
    @GET
    @Path("login")
    public Uni<RestResponse<String>> login() {
        return JanusTemplateHelper
                .createResponseFrom(Templates.login(new StartLogin(js.getLocale())), js.getLocale())
                .onFailure()
                .invoke(t -> ResponseBuilder.serverError().build());
    }

    /**
     * Remove the cookies associated with the auth form authentication and redirect
     * to the login page.
     * 
     * @return Response with a redirect to the login page
     */
    @GET
    @Path("logout")
    public RestResponse<Object> logout() {
        return ResponseBuilder.seeOther(URI.create("login"))
                .cookie(new NewCookie(REDIRECT_COOKIE_NAME, null, "/", "", "", 0, true))
                .cookie(new NewCookie(COOKIE_NAME, null, "/", "", "", 0, true))
                .build();
    }

    /**
     * A authentication error page.
     * 
     * @return The error page
     */
    @GET
    @Path("auth_error")
    public Uni<String> auth_error() {
        return JanusTemplateHelper.createStringFrom(Templates.auth_error(), js.getLocale());
    }

    /**
     * Change the locale of the application, and redirect to return URI.
     * 
     * NOTE! Should not be a GET should be made a POST instead.
     * 
     * @param code   Language code
     * @param backTo Return URI
     * @return Returns with the redirect to the return URI
     */
    @GET
    @Path("locale")
    public RestResponse<Object> locale(@RestQuery("code") String code,
                                        @RestQuery("return") String backTo)
    {
        if (code != null)
            js.setLocale(code);
        if (backTo != null)
            return ResponseBuilder.seeOther(URI.create(backTo)).build();
        else
            return ResponseBuilder.seeOther(URI.create("")).build();
    }

}

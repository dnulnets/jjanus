package eu.stenlund.janus.security;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hibernate.reactive.mutiny.Mutiny;

import eu.stenlund.janus.model.User;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.TrustedAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ReactiveTrustedAuthentication implements IdentityProvider<TrustedAuthenticationRequest> {

    @Inject
    Mutiny.SessionFactory sf;
    
    @Override
    public Class<TrustedAuthenticationRequest> getRequestType() {
        return TrustedAuthenticationRequest.class;
    }

    protected QuarkusSecurityIdentity.Builder checkPrincipal(User user,
        TrustedAuthenticationRequest request) {

        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder();
        builder.setPrincipal(new QuarkusPrincipal(request.getPrincipal()));
        Set<String> ss = new HashSet<String>();
        user.roles.forEach(r -> ss.add(r.name));
        builder.addRoles(ss);
        builder.addAttribute("email", user.email);
        builder.addAttribute("name", user.name);
        builder.addAttribute("id", user.id);
        return builder;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(TrustedAuthenticationRequest request,
        AuthenticationRequestContext context) {
        return sf.withSession(s -> User.findByUsername(s, request.getPrincipal()))
            .onFailure().transform(t-> new AuthenticationFailedException(t))
            .onItem().transform(entity -> checkPrincipal(entity, request))
            .onItem().transform(QuarkusSecurityIdentity.Builder::build);
    }
}

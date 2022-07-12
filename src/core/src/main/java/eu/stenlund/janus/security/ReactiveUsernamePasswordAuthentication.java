package eu.stenlund.janus.security;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.logging.Logger;

import eu.stenlund.janus.model.User;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class ReactiveUsernamePasswordAuthentication implements IdentityProvider<UsernamePasswordAuthenticationRequest> {

    @Inject
    Mutiny.SessionFactory sf;

    private static final Logger log = Logger.getLogger(ReactiveUsernamePasswordAuthentication.class);

    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    protected QuarkusSecurityIdentity.Builder checkPassword(User user,
            UsernamePasswordAuthenticationRequest request) {
        if (!BcryptUtil.matches(String.valueOf(request.getPassword().getPassword()), user.password)) {
            throw new AuthenticationFailedException();
        }
        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder();
        builder.setPrincipal(new QuarkusPrincipal(request.getUsername()));
        builder.addCredential(request.getPassword());
        Set<String> ss = new HashSet<String>();
        user.roles.forEach(r -> ss.add(r.name));
        builder.addRoles(ss);
        return builder;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(
            UsernamePasswordAuthenticationRequest request,
            AuthenticationRequestContext context) {

        return sf.withSession(s -> User.findByUsername(s, request.getUsername())
            .onFailure().transform(t-> new AuthenticationFailedException(t))
            .onItem().transform(entity -> checkPassword(entity, request))
            .onItem().transform(QuarkusSecurityIdentity.Builder::build));

    }
 
}

package eu.stenlund.janus.security;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.logging.Logger;

import eu.stenlund.janus.model.User;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.TrustedAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;

/**
 * Based on the trusted authentication (cookie, jwt, etc.) fetch data on the
 * user in the database and add it to the SecurityIdentity.
 *
 * @author Tomas Stenlund
 * @since 2022-07-14
 * 
 */
@ApplicationScoped
public class ReactiveTrustedAuthentication implements IdentityProvider<TrustedAuthenticationRequest> {

    private static final Logger log = Logger.getLogger(ReactiveTrustedAuthentication.class);

    @Inject
    Mutiny.SessionFactory sf;

    @Override
    public Class<TrustedAuthenticationRequest> getRequestType() {
        return TrustedAuthenticationRequest.class;
    }

    /**
     * Populates the SecurityIdentifier with information from the user.
     * 
     * @param user    The user information from the database, if null the user is
     *                anonymous
     * @param request The request for authentication
     * @return A SecurityIdentity populated with user information relevant to the
     *         application
     */
    protected QuarkusSecurityIdentity.Builder populateSecurityIdentifier(User user) {
        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder();
        if (user != null) {
            builder.setPrincipal(new QuarkusPrincipal(user.username));
            Set<String> ss = new HashSet<String>();
            user.roles.forEach(r -> ss.add(r.name));
            builder.addRoles(ss);
            builder.addAttribute("email", user.email);
            builder.addAttribute("name", user.name);
            builder.addAttribute("id", user.id);
            builder.setAnonymous(false);
        } else {
            builder.setAnonymous(true);
        }
        return builder;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(TrustedAuthenticationRequest request,
            AuthenticationRequestContext context) {
        String username = request.getPrincipal();
        return sf.withSession(session -> User.findByUsername(session, username))
                .onFailure().recoverWithItem(t -> null)
                .map(user -> populateSecurityIdentifier(user))
                .map(QuarkusSecurityIdentity.Builder::build);
    }
}

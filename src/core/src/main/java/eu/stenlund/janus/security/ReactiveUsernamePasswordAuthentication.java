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

/**
 * Based on the username/password authentication request fetch data on the user
 * in the database, compare it to the hashed password and add information to the
 * SecurityIdentity.
 *
 * @author Tomas Stenlund
 * @since 2022-07-14
 * 
 */
@ApplicationScoped
public class ReactiveUsernamePasswordAuthentication implements IdentityProvider<UsernamePasswordAuthenticationRequest> {

    private static final Logger log = Logger.getLogger(ReactiveUsernamePasswordAuthentication.class);

    @Inject
    Mutiny.SessionFactory sf;

    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    /**
     * Check the password from the request with the password stored in the database.
     * If they match
     * populate the SecurityIdentifier with information.
     * 
     * @param user    The user as stored in the database
     * @param request The request from e.g. form authentication, basic
     *                authentication etc.
     * @return A populated SecurityIdentifier
     */
    protected QuarkusSecurityIdentity.Builder checkPassword(User user, String password) {

        if (!BcryptUtil.matches(password, user.password)) {
            throw new AuthenticationFailedException();
        }
        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder();
        builder.setPrincipal(new QuarkusPrincipal(user.username));
        Set<String> ss = new HashSet<String>();
        user.roles.forEach(r -> ss.add(r.name));
        builder.addRoles(ss);
        builder.addAttribute("email", user.email);
        builder.addAttribute("name", user.name);
        builder.addAttribute("id", user.id);
        return builder;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(
            UsernamePasswordAuthenticationRequest request,
            AuthenticationRequestContext context) {
        String username = request.getUsername();
        log.info("Username:" + username);
        String password = String.valueOf(request.getPassword().getPassword());
        return sf.withSession(session -> User.findByUsername(session, username)
                .onFailure().transform(t -> new AuthenticationFailedException(t))
                .onItem().transform(user -> checkPassword(user, password))
                .onItem().transform(QuarkusSecurityIdentity.Builder::build));

    }

}

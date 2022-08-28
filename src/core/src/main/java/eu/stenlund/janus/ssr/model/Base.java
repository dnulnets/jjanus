package eu.stenlund.janus.ssr.model;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import eu.stenlund.janus.base.JanusSession;
import eu.stenlund.janus.model.Product;
import eu.stenlund.janus.model.Team;
import eu.stenlund.janus.model.base.JanusEntity;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;

/**
 * The Base object used by Qute for the user interface. Holds information for the
 * navbar and other generic items. Used by all pages.
 *
 * @author Tomas Stenlund
 * @since 2022-07-20
 * 
 */
public class Base {
    
    /**
     * If the user has the specific hardcoded roles
     */
    public boolean adminRole = false;
    public boolean productRole = false;
    public boolean userRole = false;
    public boolean teamRole = false;

    public String username;

    /*
     * The selected product, if any
     */
    public Product product = null;

    /*
     * The selected team, if any
     */
    public Team team = null;

    /**
     * Constructor that initializes the object.
     * 
     * @param si The SecurityIdentity of the user that has logged in
     */
    public Base(SecurityIdentity si, JanusSession js, Product product, Team team) {

        /* Get data from the security identity */
        adminRole = si.hasRole("admin");
        productRole = si.hasRole("product");
        teamRole = si.hasRole("team");
        userRole = si.hasRole("user");
        username = si.getAttribute("name")+" (" + si.getPrincipal().getName() + ")";

        this.product = product;
        this.team = team;
    }

    /**
     * Createsd the base ui model.
     * 
     * @param sf The session factory.
     * @param cia The Security association.
     * @param js The session cookie.
     * @return The base model for the web page.
     */
    public static Uni<Base> createModel (SessionFactory sf, CurrentIdentityAssociation sia, JanusSession js)
    {
        return Uni.combine().all().unis(
            sf.withSession(s->JanusEntity.get(Product.class, s, js.getProduct())),
            sf.withSession(s->JanusEntity.get(Team.class, s, js.getTeam())),
            sia.getDeferredIdentity()).
        combinedWith((product, team, si) -> new Base (si, js, product, team));
    }
}

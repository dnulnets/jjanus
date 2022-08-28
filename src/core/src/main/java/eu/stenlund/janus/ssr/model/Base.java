package eu.stenlund.janus.ssr.model;

import io.quarkus.security.identity.SecurityIdentity;

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
     * If the user has the admin role.
     */
    public boolean admin = false;

    /**
     * Constructor that initializes the object.
     * 
     * @param si The SecurityIdentity of the user that has logged in
     */
    public Base(SecurityIdentity si) {

        if(si.hasRole("admin"))
            admin = true;
    }

}

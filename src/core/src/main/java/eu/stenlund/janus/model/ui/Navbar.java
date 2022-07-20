package eu.stenlund.janus.model.ui;

import io.quarkus.security.identity.SecurityIdentity;

public class Navbar {
    
    public boolean admin = false;

    public Navbar(SecurityIdentity si) {

        if(si.hasRole("admin"))
            admin = true;
    }

}

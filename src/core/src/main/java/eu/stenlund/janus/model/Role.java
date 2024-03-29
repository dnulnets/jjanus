package eu.stenlund.janus.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.reactive.mutiny.Mutiny.Session;

import eu.stenlund.janus.model.base.JanusEntity;
import io.smallrye.mutiny.Uni;

/**
 * The role of a person.
 * 
 * Please see
 * <a href="https://github.com/dnulnets/janus/wiki/Users-and-roles">Users and
 * Roles</a>
 * for logical information model.
 *
 * @author Tomas Stenlund
 * @since 2022-07-11
 * 
 */
@Entity
@Table(name = "\"role\"")
@NamedQueries({
    @NamedQuery(name = "Role_ListOfRoles", query = "from Role r order by r.longName"),
    @NamedQuery(name = "Role_FindByName", query = "from Role r where r.name = :name")
})
public class Role extends JanusEntity {

    /**
     * Constants for static roles in the system
     */
    public static String ADMIN = "admin";
    public static String USER = "user";
    public static String PRODUCT = "product";

    /**
     * Long name of the role, e.g. "Product Owner"
     */
    @Column(unique = true, nullable = false, updatable = false)
    public String longName;

    /**
     * Short name of the role, e.g. "product"
     */
    @Column(unique = true, nullable = false, updatable = false)
    public String name;

    /**
     * Description of the role
     */
    @Column(unique = false, nullable = true, updatable = true)
    public String description;

    /**
     * Locate a role based on its name.
     * 
     * @param s The session
     * @param name The name of the role to locate.
     * @return The found role or null
     */
    public static Uni<Role> getByName(Session s, String name) {
        return s.createNamedQuery("Role_FindByName", Role.class)
                .setParameter("name", name).getSingleResult();

    }

    /**
     * Retrieve the list of all available roles in the application.
     * 
     * @param s The session.
     * @return A list of all the roles.
     */
    public static Uni<List<Role>> getList(Session s)
    {
        return s.createNamedQuery("Role_ListOfRoles", Role.class).getResultList();
    }

    /**
     * Utility function to find a Role in a list of roles based on the uuid.
     * 
     * @param roles The list of roles.
     * @param uuid The UUID.
     * @return The found role or null.
     */
    public static Role findRoleById(List<Role> roles, UUID uuid)
    {
        Optional<Role> role = roles.stream().filter(t-> t.id.compareTo(uuid)==0).findFirst();
        return role.orElse(null);
    }
}

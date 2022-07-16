package eu.stenlund.janus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.reactive.mutiny.Mutiny.Session;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

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
@Table(name = "role")
public class Role extends JanusEntity {

    @Column(unique = true, length = 64, nullable = false, updatable = false)
    public String name;

    @Column(unique = false, length = 256, nullable = true, updatable = true)
    public String description;

    /**
     * Locate a role based on its name.
     * 
     * @param name The name of the role to locate.
     * @return The found role or null
     */
    public static Uni<Role> findByName(Session s, String name) {
        return s.createQuery("from Role role where role.name = :name", Role.class)
        .setParameter("name", name).getSingleResult();

    }

    public static CriteriaQuery<Role> findByNameC (SessionFactory sf)
    {
        CriteriaQuery<Role> cr = sf.getCriteriaBuilder().createQuery(Role.class);
        return cr;
    }
}

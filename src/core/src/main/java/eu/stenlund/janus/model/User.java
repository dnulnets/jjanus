package eu.stenlund.janus.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.criterion.Projections;

import eu.stenlund.janus.model.base.JanusEntity;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.mutiny.Uni;

import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.reactive.mutiny.Mutiny.Session;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.jboss.logging.Logger;

/**
 * The information about a person.
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
@Table(name = "\"user\"")
@NamedQueries({
    @NamedQuery(name = "User_FindByUsername", query = "from User u where u.username = :name"),
    @NamedQuery(name = "User_ListOfUsers", query = "from User u order by u.name"),
    @NamedQuery(name = "User_NumberOfUsers", query = "Select count (u.id) from User u")
})
public class User extends JanusEntity {

    private static final Logger log = Logger.getLogger(JanusEntity.class);

    /**
     * The name of the user.
     */
    @Column(nullable = false, updatable = true)
    public String name;

    /**
     * The email to the user.
     */
    @Column(nullable = false, updatable = true)
    public String email;

    /**
     * The username used when logging in.
     */
    @Column(nullable = false, updatable = true, unique = true)
    public String username;

    /**
     * The password for the user, bcrypted.
     */
    @Column(nullable = false, updatable = true)
    public String password;

    /**
     * All of the roles for the user.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role"
        , joinColumns = { @JoinColumn(name = "\"user\"") }
        , inverseJoinColumns = {@JoinColumn(name = "role") })
    public Set<Role> roles = new HashSet<Role>();

    /**
     * Returns true if the user has a specific role.
     * 
     * @param role The role that you want to check.
     * @return True if the user has the role.
     */
    public boolean hasRole(String role)
    {
        Iterator<Role> i = roles.iterator();
        boolean has = false;
        while (i.hasNext())
            has |= (i.next().name.compareTo(role)==0);
        return has;
    }

    /**
     * Add a user to the database.
     * 
     * @param s    A mutiny session.
     * @param user The user to add.
     * @return An asynchronous result.
     */
    public static Uni<User> addUser(Session s, User user) {
        return s.persist(user).replaceWith(user);   
    }

    /**
     * Retrieves a specific user based on its key identity.
     * 
     * @param s The session.
     * @param id The users UUID as a string.
     * @return The user or null.
     */
    public static Uni<User> getUser(Session s, UUID uuid) {
        return s.find(User.class, uuid);
    }

    /**
     * Finds a user by its username.
     * 
     * @param s   A mutiny session.
     * @param uid The username of the user.
     * @return An synchronous result.
     */
    public static Uni<User> findByUsername(Session s, String uid) {
        return s.createNamedQuery("User_FindByUsername", User.class)
            .setParameter("name", uid).getSingleResult();
    }

    public static Uni<Long> getNumberOfUsers(Session s)
    {
        return s.createNamedQuery("User_NumberOfUsers", Long.class)
            .getSingleResult();
    }

    public static Uni<List<User>> getListOfUsers(Session s, int start, int max)
    {
        return s.createNamedQuery("User_ListOfUsers", User.class)
            .setFirstResult(start).setMaxResults(max).getResultList();
    }

    public static Uni<Void> deleteUser(Session s, UUID uuid)
    {
        return getUser(s, uuid).chain(u -> s.remove(u));
    }

    /**
     * Sets the password for the user and bcrypts it.
     * 
     * @param pwd The cleartext password.
     */
    public void setPassword(String pwd) {
        password = BcryptUtil.bcryptHash(pwd);
    }
}

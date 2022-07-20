package eu.stenlund.janus.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.reactive.mutiny.Mutiny.Session;

import eu.stenlund.janus.model.base.JanusEntity;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.mutiny.Uni;

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
    @NamedQuery(name = "User_FindByUsername", query = "from User user where user.username = :name")
})
public class User extends JanusEntity {

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
    public Set<Role> roles;

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
     * Finds a user by its username.
     * 
     * @param s   A mutiny session.
     * @param uid The username of the user.
     * @return An syncrhonous result.
     */
    public static Uni<User> findByUsername(Session s, String uid) {
        return s.createNamedQuery("User_FindByUsername", User.class)
            .setParameter("name", uid).getSingleResult();
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

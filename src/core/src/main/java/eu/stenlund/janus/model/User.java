package eu.stenlund.janus.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

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
public class User extends JanusEntity {

    @Column(length = 64, nullable = false, updatable = true)
    public String name;

    @Column(length = 64, nullable = false, updatable = true)
    public String email;

    @Column(length = 64, nullable = false, updatable = true, unique = true)
    public String username;

    @Column(length = 128, nullable = false, updatable = true)
    public String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "\"user\"") }, inverseJoinColumns = {
            @JoinColumn(name = "role") })
    public Set<Role> roles;

    public static Uni<User> addUser(Session s, User user) {

        return s.persist(user).replaceWith(user)
            .onFailure()
            .transform(t -> new IllegalStateException(t));
    }

    public static Uni<User> findByUsername(Session s, String uid) {
        return s.createQuery("from User user where user.username = :name", User.class)
        .setParameter("name", uid).getSingleResult();
    }

    public void setPassword(String pwd) { 
        password = BcryptUtil.bcryptHash(pwd);
    }

}

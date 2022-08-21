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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.reactive.mutiny.Mutiny.Session;
import org.jboss.logging.Logger;

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
        @NamedQuery(name = "User_FindByUsername", query = "from User u where u.username = :name"),
        @NamedQuery(name = "User_ListOfUsers", query = "from User u order by u.name"),
        @NamedQuery(name = "User_NumberOfUsers", query = "Select count (u.id) from User u")
})
public class User extends JanusEntity {

    private static final Logger log = Logger.getLogger(JanusEntity.class);

    /**
     * The name of the user.
     */
    @Column(unique=false, nullable = false, updatable = true)
    public String name;

    /**
     * The email to the user.
     */
    @Column(unique=false, nullable = false, updatable = true)
    public String email;

    /**
     * The username used when logging in.
     */
    @Column(nullable = false, updatable = true, unique = true)
    public String username;

    /**
     * The password for the user, bcrypted.
     */
    @Column(unique=false, nullable = false, updatable = true)
    public String password;

    /**
     * All of the roles for the user.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "\"user\"") }, inverseJoinColumns = {
            @JoinColumn(name = "role") })
    public Set<Role> roles;

    /**
     * All the teams the user belongs to.
     */
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "members")
    public Set<Team> teams;

    /**
     * Default constructor, initialize all fields that requires it.
     */
    public User()
    {
        super();
        teams = new HashSet<Team>();
        roles = new HashSet<Role>();
    }

    /**
     * Add a team to the user and also to the owning relation holder.
     * 
     * @param team Team to add.
     */
    public void addTeam(Team team) {
        teams.add(team);
        team.members.add(this);
    }

    /**
     * Remove the team from the user and also on the owning relation holder.
     * 
     * @param team The team to remove.
     */
    public void removeTeam(Team team) {
        teams.remove(team);
        team.members.remove(this);
    }

    /**
     * Remove all teams from the user and also from the owning relation holder.
     */
    public void clearTeams() {
        teams.forEach(team -> team.members.remove(this));
        teams.clear();
    }

    /**
     * Returns true if the user has a specific role.
     * 
     * @param role The role that you want to check.
     * @return True if the user has the role.
     */
    public boolean hasRole(String role) {
        if (roles != null) {
            Iterator<Role> i = roles.iterator();
            boolean has = false;
            while (i.hasNext())
                has |= (i.next().name.compareTo(role) == 0);
            return has;
        } else
            return false;

    }

    /**
     * Returns true if the user belongs to a specific team.
     * 
     * @param uuid The uuid of the team.
     * @return True if the user has the team.
     */
    public boolean belongsToTeam(UUID uuid) {
        if (teams != null) {
            Iterator<Team> i = teams.iterator();
            boolean has = false;
            while (i.hasNext())
                has |= (i.next().id.compareTo(uuid) == 0);
            return has;
        } else
            return false;

    }

    /**
     * Finds a user by its username.
     * 
     * @param s   A mutiny session.
     * @param uid The username of the user.
     * @return An synchronous result.
     */
    public static Uni<User> getByUsername(Session s, String uid) {
        return s.createNamedQuery("User_FindByUsername", User.class)
                .setParameter("name", uid).getSingleResult();
    }

    /**
     * Returns with the number of users.
     * 
     * @param s The session.
     * @return Number of users in the database.
     */
    public static Uni<Long> getCount(Session s) {
        return s.createNamedQuery("User_NumberOfUsers", Long.class)
                .getSingleResult();
    }

    /**
     * Returns with the list of users based on start and max number of users. It is
     * mainly used
     * for tables in the gui.
     * 
     * @param s     The session.
     * @param start Start index tio search from.
     * @param max   Max number of items to return.
     * @return List of users.
     */
    public static Uni<List<User>> getList(Session s, int start, int max) {
        return s.createNamedQuery("User_ListOfUsers", User.class)
                .setFirstResult(start).setMaxResults(max).getResultList();
    }

    /**
     * Returns with the list of all users.
     * 
     * @param s The session.
     * @return List of users.
     */
    public static Uni<List<User>> getList(Session s) {
        return s.createNamedQuery("User_ListOfUsers", User.class)
                .getResultList();
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

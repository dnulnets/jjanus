package eu.stenlund.janus.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.reactive.mutiny.Mutiny.Session;

import eu.stenlund.janus.model.base.JanusEntity;
import io.smallrye.mutiny.Uni;

/**
 * The team in the system, consists of users.
 * 
 * Please see <a href="https://github.com/dnulnets/janus/wiki/Team-and-backlogs">Teams and backlogs</a>
 * for logical information model.
 *
 * @author Tomas Stenlund
 * @since 2022-07-31
 * 
 */
@Entity
@Table(name = "team")
@NamedQueries({
    @NamedQuery(name = "Team_ListOfTeams", query = "from Team t order by t.name"),
    @NamedQuery(name = "Team_NumberOfTeams", query = "Select count (t.id) from Team t")
})
public class Team extends JanusEntity {

    /**
     * Short name of the team, e.g. "Codestorm"
     */
    @Column(unique = true, nullable = false, updatable = true)
    public String name;

    
    /**
     * All of the members in a team
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "team_user"
        , joinColumns = { @JoinColumn(name = "team") }
        , inverseJoinColumns = {@JoinColumn(name = "\"user\"") })
    public Set<User> members;

    /**
     * The teams backlog.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable=false, name = "backlog", referencedColumnName = "id")
    public Backlog backlog;
    
    /**
     * Returns with the number of users.
     * 
     * @param s The session.
     * @return Number of users in the database.
     */
    public static Uni<Long> getNumberOfTeams(Session s)
    {
        return s.createNamedQuery("Team_NumberOfTeams", Long.class)
            .getSingleResult();
    }

    /**
     * Retrieves a specific team based on its key identity.
     * 
     * @param s The session.
     * @param id The teams UUID as a string.
     * @return The team or null.
     */
    public static Uni<Team> getTeam(Session s, UUID uuid) {
        return s.find(Team.class, uuid);
    }

    /**
     * Delete a team given the UUID.
     * 
     * @param s The session.
     * @param uuid The UUID of the team.
     * @return Nothing.
     */
    public static Uni<Void> deleteTeam(Session s, UUID uuid)
    {
        return getTeam(s, uuid).chain(u -> s.remove(u));
    }

    /**
     * Add a team to the database.
     * 
     * @param s    A mutiny session.
     * @param user The team to add.
     * @return An asynchronous result.
     */
    public static Uni<Team> addTeam(Session s, Team team) {
        return s.persist(team).replaceWith(team);   
    }

    /**
     * Returns with the list of users based on start and max number of users. It is mainly used
     * for tables in the gui.
     * 
     * @param s The session.
     * @param start Start index tio search from.
     * @param max Max number of items to return.
     * @return List of users.
     */
    public static Uni<List<Team>> getListOfTeams(Session s, int start, int max)
    {
        return s.createNamedQuery("Team_ListOfTeams", Team.class)
            .setFirstResult(start).setMaxResults(max).getResultList();
    }
}

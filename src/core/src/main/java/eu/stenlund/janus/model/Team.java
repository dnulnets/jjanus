package eu.stenlund.janus.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @JoinColumn(name = "backlog", referencedColumnName = "id")
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

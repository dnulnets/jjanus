package eu.stenlund.janus.model;

import java.util.HashSet;
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

import eu.stenlund.janus.model.base.JanusEntity;

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
    
}

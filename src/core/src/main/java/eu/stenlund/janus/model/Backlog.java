package eu.stenlund.janus.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "backlog")
public class Backlog extends JanusEntity {

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "backlog_backlogitem"
        , joinColumns = { @JoinColumn(name = "\"backlog\"") }
        , inverseJoinColumns = {@JoinColumn(name = "backlogitem") })
    public Set<BacklogItem> items = new HashSet<BacklogItem>();

}

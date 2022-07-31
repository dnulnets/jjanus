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
import javax.persistence.Table;

import eu.stenlund.janus.model.base.JanusEntity;

/**
 * The backlog item in the backlog, with ordering.
 * 
 * Please see <a href="https://github.com/dnulnets/janus/wiki/Team-and-backlogs">Teams and backlogs</a>
 * for logical information model.
 *
 * @author Tomas Stenlund
 * @since 2022-07-31
 * 
 */
@Entity
@Table(name = "backlogitem")
public class BacklogItem extends JanusEntity {

    /**
     * The ordinal of the item.
     */
    @Column(unique = true, nullable = false, updatable = true)
    public int position;

}

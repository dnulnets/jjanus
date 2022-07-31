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
 * The team in the system, consists of users.
 * 
 * Please see <a href="https://github.com/dnulnets/janus/wiki/Services-and-Products">Services and products</a>
 * for logical information model.
 *
 * @author Tomas Stenlund
 * @since 2022-07-31
 * 
 */
@Entity
@Table(name = "product")
public class Product extends JanusEntity {

    /**
     * Short name of the product, e.g. "SMP"
     */
    @Column(unique = true, nullable = false, updatable = true)
    public String name;

}

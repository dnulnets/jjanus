package eu.stenlund.janus.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import eu.stenlund.janus.model.base.JanusEntity;

/**
 * The version of a product
 * 
 * Please see <a href="https://github.com/dnulnets/janus/wiki/Services-and-Products">Services and products</a>
 * for logical information model.
 *
 * @author Tomas Stenlund
 * @since 2022-08-13
 * 
 */
@Entity
@Table(name = "productversion")
public class ProductVersion extends JanusEntity {

    /**
     * Version of the product, e.g. R1.0.0
     */
    @Column(unique = true, nullable = false, updatable = true)
    public String version;

    @ManyToOne()
    @JoinColumn(name="product", nullable = false)
    public Product product;

    /**
     * All the teams that handles this version of the product.
     */
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "productversions")
    public Set<Team> teams;

    /**
     * Default constructor
     */
    public ProductVersion()
    {
        super();
        product = null;
        teams = new HashSet<Team>();
    }
}

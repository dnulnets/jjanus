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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import eu.stenlund.janus.model.base.JanusEntity;

/**
 * The product.
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

    /**
     * Description
     */
    @Column(unique=false, nullable = true, updatable = true)
    public String description;

    /**
     * All available version of the product
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product")
    public Set<ProductVersion> versions;
    
    /**
     * Current version of the product
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="current", nullable = true)
    public ProductVersion current;

    /**
     * Default constructor for product and initialize fields that need it.
     */
    public Product()
    {
        super();
        versions = new HashSet<ProductVersion>();
        current = null;
    }
}

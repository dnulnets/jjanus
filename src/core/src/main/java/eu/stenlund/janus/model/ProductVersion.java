package eu.stenlund.janus.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.reactive.mutiny.Mutiny.Session;

import eu.stenlund.janus.model.base.JanusEntity;
import io.smallrye.mutiny.Uni;

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
@Table( name = "productversion",
        uniqueConstraints=@UniqueConstraint(columnNames={"version", "product"}))
@NamedQueries({
    @NamedQuery(name = "ProductVersion_ListOfProductVersions", query = "From ProductVersion m ORDER BY m.product.name, m.version"),
    @NamedQuery(name = "ProductVersion_NumberOfProductVersions", query = "Select count (v.id) from ProductVersion v")
})
public class ProductVersion extends JanusEntity {

    /**
     * Version of the product, e.g. R1.0.0
     */
    @Column(unique = false, nullable = false, updatable = true)
    public String version;

    /**
     * If the version is closed for changes
     */
    @Column(unique = false, nullable = false, updatable = true)
    public boolean closed;

    /**
     * The product that this version is meant for.
     */
    @ManyToOne()
    @JoinColumn(unique=false, name="product", nullable = false, updatable = true)
    public Product product;

    /**
     * State of the product version.
     */
    @ManyToOne()
    @JoinColumn(unique=false, name="state", nullable = true, updatable = true)
    public ProductState state;

    /**
     * Default constructor
     */
    public ProductVersion()
    {
        super();
        product = null;
        state = null;
    }

    /**
     * Returns with the number of products in total.
     * 
     * @param s The session.
     * @return Number of products in the database.
     */
    public static Uni<Long> getCount(Session s)
    {
        return s.createNamedQuery("ProductVersion_NumberOfProductVersions", Long.class)
            .getSingleResult();
    }

    /**
     * Returns with the list of products based on start and max number of users. It is mainly used
     * for tables in the gui.
     * 
     * @param s The session.
     * @param start Start index tio search from.
     * @param max Max number of items to return.
     * @return List of products.
     */
    public static Uni<List<ProductVersion>> getList(Session s, int start, int max)
    {
        return s.createNamedQuery("ProductVersion_ListOfProductVersions", ProductVersion.class)
            .setFirstResult(start).setMaxResults(max).getResultList();
    }

}

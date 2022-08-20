package eu.stenlund.janus.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
@Table(name = "productversion")
@NamedQueries({
    @NamedQuery(name = "ProductVersion_ListOfProductVersions", query = "From ProductVersion m ORDER BY m.product.name, m.version"),
    @NamedQuery(name = "ProductVersion_NumberOfProductVersions", query = "Select count (v.id) from ProductVersion v")
})
public class ProductVersion extends JanusEntity {

    /**
     * Version of the product, e.g. R1.0.0
     */
    @Column(unique = true, nullable = false, updatable = true)
    public String version;

    /**
     * If the version is closed for new features
     */
    @Column(unique = false, nullable = false, updatable = true)
    public boolean closed;

    /**
     * The product that this version is meant for.
     */
    @ManyToOne()
    @JoinColumn(unique=true, name="product", nullable = false)
    public Product product;

    /**
     * State of the product version.
     */
    @ManyToOne()
    @JoinColumn(name="state", nullable = false)
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
    public static Uni<Long> getNumberOfProductVersions(Session s)
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
    public static Uni<List<ProductVersion>> getListOfProductVersions(Session s, int start, int max)
    {
        return s.createNamedQuery("ProductVersion_ListOfProductVersions", ProductVersion.class)
            .setFirstResult(start).setMaxResults(max).getResultList();
    }

    /**
     * Retrieves a specific team based on its key identity.
     * 
     * @param s The session.
     * @param id The product version UUID as a string.
     * @return The product version or null.
     */
    public static Uni<ProductVersion> getProductVersion(Session s, UUID uuid) {
        return uuid!=null?s.find(ProductVersion.class, uuid):null;
    }

    public static Uni<ProductVersion> createProductVersion(Session s, ProductVersion pv) {
        return s.persist(pv).replaceWith(pv);   
    }

    public static Uni<Void> deleteProductVersion(Session s, UUID uuid)
    {
        return getProductVersion(s, uuid).chain(u -> s.remove(u));
    }
}

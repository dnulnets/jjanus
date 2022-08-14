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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.reactive.mutiny.Mutiny.Session;

import eu.stenlund.janus.model.base.JanusEntity;
import io.smallrye.mutiny.Uni;

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
@NamedQueries({
    @NamedQuery(name = "Product_ListOfProducts", query = "from Product p order by p.name"),
    @NamedQuery(name = "Product_NumberOfProducts", query = "Select count (p.id) from Product p")
})
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

    /**
     * Returns with the number of products in total.
     * 
     * @param s The session.
     * @return Number of products in the database.
     */
    public static Uni<Long> getNumberOfProducts(Session s)
    {
        return s.createNamedQuery("Product_NumberOfProducts", Long.class)
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
    public static Uni<List<Product>> getListOfProducts(Session s, int start, int max)
    {
        return s.createNamedQuery("Product_ListOfProducts", Product.class)
            .setFirstResult(start).setMaxResults(max).getResultList();
    }

    /**
     * Retrieves a specific team based on its key identity.
     * 
     * @param s The session.
     * @param id The products UUID as a string.
     * @return The product or null.
     */
    public static Uni<Product> getProduct(Session s, UUID uuid) {
        return s.find(Product.class, uuid);
    }

    /**
     * Delete a product given the UUID.
     * 
     * @param s The session.
     * @param uuid The UUID of the product.
     * @return Nothing.
     */
    public static Uni<Void> deleteProduct(Session s, UUID uuid)
    {
        return getProduct(s, uuid).chain(u -> s.remove(u));
    }

    /**
     * Add a product to the database.
     * 
     * @param s    A mutiny session.
     * @param user The product to add.
     * @return An asynchronous result.
     */
    public static Uni<Product> createProduct(Session s, Product product) {
        return s.persist(product).replaceWith(product);   
    }

}

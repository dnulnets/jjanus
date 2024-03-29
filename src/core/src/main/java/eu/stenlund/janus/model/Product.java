package eu.stenlund.janus.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
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
    @NamedQuery(name = "Product_NumberOfProducts", query = "Select count (p.id) from Product p"),
    @NamedQuery(name = "Product_Search", query = "from Product p where p.name like :what order by p.name")
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
     * All the teams that handles this product.
     */
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "products")
    public Set<Team> teams;

    /**
     * Default constructor for product and initialize fields that need it.
     */
    public Product()
    {
        super();
        versions = new HashSet<ProductVersion>();
        teams = new HashSet<Team>();
        current = null;
    }

    /**
     * Add a team to the product and also to the owning relation holder.
     * 
     * @param team Team to add.
     */
    public void addTeam(Team team) {
        teams.add(team);
        team.products.add(this);
    }

    /**
     * Remove the team from the product and also on the owning relation holder.
     * 
     * @param team The team to remove.
     */
    public void removeTeam(Team team) {
        teams.remove(team);
        team.products.remove(this);
    }

    /**
     * Remove all teams from the user and also from the owning relation holder.
     */
    public void clearTeams() {
        teams.forEach(team -> team.products.remove(this));
        teams.clear();
    }

    /**
     * Returns with the number of products in total.
     * 
     * @param s The session.
     * @return Number of products in the database.
     */
    public static Uni<Long> getCount(Session s)
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
    public static Uni<List<Product>> getList(Session s, int start, int max)
    {
        return s.createNamedQuery("Product_ListOfProducts", Product.class)
            .setFirstResult(start).setMaxResults(max).getResultList();
    }

    /**
     * Returns with the list of all products.
     * 
     * @param s The session.
     * @return List of products.
     */
    public static Uni<List<Product>> getList(Session s)
    {
        return s.createNamedQuery("Product_ListOfProducts", Product.class)
            .getResultList();
    }

    /**
     * Returns a list of products that contains the string what in the name or in the description.
     * 
     * @param s The session,
     * @param what The search string that must be conatined.
     * @return List of products.
     */
    public static Uni<List<Product>> search(Session s, String what)
    {
        return s.createNamedQuery("Product_Search", Product.class).
            setParameter("what", "%"+what+"%").
            getResultList();
    }

    /**
     * Utility function to find a Product in a list of products based on the uuid.
     * 
     * @param roles The list of products.
     * @param uuid The UUID.
     * @return The found product or null.
     */
    public static Product findProductById(Collection<Product> products, UUID uuid)
    {
        Optional<Product> product = products.stream().filter(t-> t.id.compareTo(uuid)==0).findFirst();
        return product.orElse(null);
    }
}

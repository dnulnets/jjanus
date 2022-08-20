package eu.stenlund.janus.model;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "productstate")
@NamedQueries({
    @NamedQuery(name = "ProductState_ListOfProductState", query = "From ProductState ps ORDER BY ps.display")
})
public class ProductState extends JanusEntity {

    /**
     * Displayname for the state
     */
    @Column(unique = false, nullable = false, updatable = true)
    public String display;

    /**
     * Default constructor
     */
    public ProductState()
    {
        super();
        display = "";
    }

    /**
     * Resturns with a list of all available states.
     * 
     * @param s The session.
     * @return A list of all available product versions.
     */
    public static Uni<List<ProductState>> getListOfProductStates(Session s) {
        return s.createNamedQuery("ProductState_ListOfProductState", ProductState.class)
                .getResultList();
    }

    /**
     * Gets a product state based on its uuid.
     * 
     * @param s The session factory.
     * @param uuid The product states uuid.
     * @return The product state or null.
     */
    public static Uni<ProductState> getProductState(Session s, UUID uuid) {
        return uuid!=null?s.find(ProductState.class, uuid):null;
    }

}

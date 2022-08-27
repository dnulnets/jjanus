package eu.stenlund.janus.ssr.workarea;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.jboss.logging.Logger;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.model.Product;
import eu.stenlund.janus.model.ProductState;
import eu.stenlund.janus.model.ProductVersion;
import eu.stenlund.janus.model.base.JanusEntity;
import eu.stenlund.janus.msg.ProductVersionManagement;
import eu.stenlund.janus.ssr.JanusSSRHelper;
import eu.stenlund.janus.ssr.JanusTemplateHelper;
import eu.stenlund.janus.ssr.ui.Button;
import eu.stenlund.janus.ssr.ui.Checkbox;
import eu.stenlund.janus.ssr.ui.Form;
import eu.stenlund.janus.ssr.ui.Select;
import eu.stenlund.janus.ssr.ui.TextInput;
import io.smallrye.mutiny.Uni;

/**
 * The data model for the pages for team edit, create and delete routes.
 *
 * @author Tomas Stenlund
 * @since 2022-07-29
 * 
 */
public class ProductVersionManagementProductVersion {

    private static final Logger log = Logger.getLogger(ProductManagementProduct.class);

    /**
     * The form used.
     */
    public Form form;

    /*
     * All the buttons for the page.
     */
    public Button deleteButton;
    public Button saveButton;
    public Button cancelButton;

    /*
     * All the text inputs for the page.
     */
    public TextInput version;
    public TextInput uuid;
    public Checkbox closed;

    /*
     * The product
     */
    public Select product;

    /*
     * The state of this version
     */
    public Select state;

    /*
     * The URL:s for create and delete of the product.
     */
    public String createURL;
    public String updateURL;

    /**
     * Flag so we know if it is a new product that we want to create.
     */
    public boolean newProductVersion;


    public ProductVersionManagementProductVersion(ProductVersion productVersion, List<Product> products, List<ProductState> states, URI back, boolean newProductVersion, String locale) {

        // Create action URL:s
        String ROOT_PATH = JanusHelper.getConfig(String.class, "janus.http.root-path","/");

        String deleteURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("productversion")
            .segment("delete")
            .build().toString();

        createURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("productversion")
            .segment("create")
            .build().toString();

        updateURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("productversion")
            .build().toString();

        String backURL = ROOT_PATH; 
        if (back != null)
            backURL = back.toString();

        // Get hold of the message bundle
        ProductVersionManagement msg = JanusTemplateHelper.getMessageBundle(ProductVersionManagement.class, locale);

        // Set the new user flag if we are creating a new user
        this.newProductVersion = newProductVersion;

        // Create the buttons for delete, save and cancel
        if (newProductVersion)
            deleteButton = null;
        else
            deleteButton = new Button (msg.productversion_delete(), deleteURL, null);
        saveButton = new Button (msg.productversion_save(), null);
        cancelButton = new Button(msg.productversion_cancel(), backURL, JanusSSRHelper.unpolyFollow());

        // Create the form's text inputs
        version = new TextInput(msg.productversion_version(), "version", "id-version", productVersion.version, msg.productversion_must_have_version(), JanusSSRHelper.required());
        uuid = new TextInput("UUID", "uuid", "id-uuid", productVersion.id!=null?productVersion.id.toString():null, null, JanusSSRHelper.readonly());
        closed = new Checkbox(msg.productversion_closed(), "closed", "id-closed", "true", productVersion.closed, "");

        // Create the product selection
        List<Select.Item> l = 
            products.stream().map(p -> new Select.Item(p.name, productVersion.product!=null?productVersion.product.id.compareTo(p.id)==0:false, false, p.id.toString())).toList();
        product = new Select(msg.productversion_product(),"product", "id-product", l, true, null);

        // Create the product state selection
        List<Select.Item> m = 
            states.stream().map(s -> new Select.Item(s.display, productVersion.state!=null?productVersion.state.id.compareTo(s.id)==0:false, false, s.id.toString())).toList();
        state = new Select(msg.productversion_state(),"state", "id-state", m, false, null);

        // Create the form
        form = new Form(Form.POST, newProductVersion?createURL:updateURL, true, JanusSSRHelper.unpolySubmit(backURL));
    }

    /**
     * Creates the model from data in the database that can be used for the page.
     * 
     * @param sf The session factory.
     * @param uuid The UUID of the user.
     * @param uri The URI of the cancel or return URL.
     * @return A populated UserManagementUser.
     */
    public static Uni<ProductVersionManagementProductVersion> createModel (SessionFactory sf, UUID uuid, URI uri, String locale)
    {
        if (uuid == null) {
            return Uni.combine().all().unis(
                sf.withSession(s -> Product.getList(s)),
                sf.withSession(s -> ProductState.getList(s)))
                .combinedWith((products, states) -> new ProductVersionManagementProductVersion(
                        new ProductVersion(),
                        products,
                        states,
                        uri, true, locale));

        } else {
            return Uni.combine().all().unis(
                    sf.withSession(s -> Product.getList(s)),
                    sf.withSession(s -> ProductState.getList(s)),
                    sf.withSession(s -> JanusEntity.get (ProductVersion.class, s, uuid)))
                .combinedWith((products, states, version) -> new ProductVersionManagementProductVersion(
                        version,
                        products,
                        states,
                        uri, false, locale));
        }
    }

    public static Uni<ProductVersion> updateProductVersion(SessionFactory sf, 
        UUID uuid,
        String version,
        UUID product,
        UUID state,
        Boolean closed)
    {
        log.info ("State: " + state);
        log.info ("UUID: " + uuid);
        log.info ("Product: " + product);

        return sf.withTransaction((ss, tt) -> Uni.combine().all().unis(
                    sf.withSession(s -> JanusEntity.get (ProductVersion.class, s, uuid)),
                    sf.withSession(s -> JanusEntity.get(ProductState.class, s, state)),
                    sf.withSession(s -> JanusEntity.get(Product.class, s, product)))
                .combinedWith((pv, ps, p) -> {
                    pv.closed = closed.booleanValue();
                    pv.version = version;
                    pv.product = p;
                    pv.state = ps;
                    return pv;
                }));
    }

    public static Uni<ProductVersion> createProductVersion(SessionFactory sf,
        UUID uuid,
        String version,
        UUID product,
        UUID state,
        Boolean closed)
    {
        return sf.withTransaction((ss, tt) -> Uni.combine().all().unis(
            sf.withSession(s -> JanusEntity.get(ProductState.class, s, state)),
            sf.withSession(s -> JanusEntity.get(Product.class, s, product)))
        .combinedWith((ps, p) -> {
            ProductVersion pv = new ProductVersion();
            pv.closed = closed.booleanValue();
            pv.version = version;
            pv.product = p;
            pv.state = ps;
            return pv;})
        .chain(pv -> JanusEntity.create(ss, pv)));

    }

    /**
     * Deletes a product version based on the UUID.
     * 
     * @param sf The session factory.
     * @param uuid The UUID of the product version.
     * @return A void.
     */
    public static Uni<Void> deleteProductVersion(SessionFactory sf,
                                            UUID uuid)
    {
        return sf.withTransaction((s,t)->JanusEntity.delete (ProductVersion.class, s, uuid));
    }
}

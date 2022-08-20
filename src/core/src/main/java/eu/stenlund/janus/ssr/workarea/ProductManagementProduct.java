package eu.stenlund.janus.ssr.workarea;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.jboss.logging.Logger;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.base.JanusNoSuchItemException;
import eu.stenlund.janus.model.Product;
import eu.stenlund.janus.msg.ProductManagement;
import eu.stenlund.janus.ssr.JanusSSRHelper;
import eu.stenlund.janus.ssr.JanusTemplateHelper;
import eu.stenlund.janus.ssr.ui.Button;
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
public class ProductManagementProduct {

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
    public TextInput name;
    public TextInput description;
    public TextInput uuid;

    /*
     * The current version of the product as well as all versions available.
     */
    public Select current;

    /*
     * The URL:s for create and delete of the product.
     */
    public String createURL;
    public String updateURL;

    /**
     * Flag so we know if it is a new product that we want to create.
     */
    public boolean newProduct;

    /**
     * Creates the workarea for the user interface based on existing user and roles.
     * 
     * @param product The user for this page.
     * @param roles The available roles in the application.
     * @param back The URL for sending the user back to the page before, e.g. when pressing cancel, save and delete.
     * @param newProduct Flag telling if it is a new user page or an update/edit page.
     * @param locale The locale of the page.
     */
    public ProductManagementProduct(Product product, URI back, boolean newProduct, String locale) {

        // Create action URL:s
        String ROOT_PATH = JanusHelper.getConfig(String.class, "janus.http.root-path","/");

        String deleteURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("product")
            .segment("delete")
            .build().toString();

        createURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("product")
            .segment("create")
            .build().toString();

        updateURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("product")
            .build().toString();

        String backURL = ROOT_PATH; 
        if (back != null)
            backURL = back.toString();

        // Get hold of the message bundle
        ProductManagement msg = JanusTemplateHelper.getMessageBundle(ProductManagement.class, locale);

        // Set the new user flag if we are creating a new user
        this.newProduct = newProduct;

        // Create the buttons for delete, save and cancel
        if (newProduct)
            deleteButton = null;
        else
            deleteButton = new Button (msg.product_delete(), deleteURL, null);
        saveButton = new Button (msg.product_save(), null);
        cancelButton = new Button(msg.product_cancel(), backURL, JanusSSRHelper.unpolyFollow());

        // Create the form's text inputs
        name = new TextInput(msg.product_name(), "name", "id-name", product.name, msg.product_must_have_name(), JanusSSRHelper.required());
        description = new TextInput (msg.product_description(), "description", "id-description", product.description, null, null);
        uuid = new TextInput("UUID", "uuid", "id-uuid", product.id!=null?product.id.toString():null, null, JanusSSRHelper.readonly());

        // Create the versions
        List<Select.Item> l = 
            product.versions.stream().map(v -> new Select.Item(
                v.version + " (" + (v.state!=null?v.state.display:"No state") + ")",
                product.current!=null?product.current.id.compareTo(v.id)==0:false, v.id.toString())).toList();
        current = new Select(msg.product_current_versions(),"current", "id-current", l, null);

        // Create the form
        form = new Form(Form.POST, newProduct?createURL:updateURL, true, JanusSSRHelper.unpolySubmit(backURL));
    }

    /**
     * Creates the model from data in the database that can be used for the page.
     * 
     * @param sf The session factory.
     * @param uuid The UUID of the user.
     * @param uri The URI of the cancel or return URL.
     * @return A populated UserManagementUser.
     */
    public static Uni<ProductManagementProduct> createModel (SessionFactory sf, UUID uuid, URI uri, String locale)
    {
        if (uuid == null) {
            return Uni.createFrom().item(new ProductManagementProduct(new Product(), uri, true, locale));
        } else {
            return sf.withSession(s -> Product.getProduct(s, uuid)
                        .onItem()
                            .ifNull()
                                .failWith(new JanusNoSuchItemException("Failed to read the product from the database using the given uuid."
                                    , "product"
                                    , uuid.toString()
                                    , uri.toString())))
            .map(p -> new ProductManagementProduct(p, uri, false, locale));
        }
    }

    /**
     * Update a team based on the user uuid and a new set of roles and attributes.
     * 
     * @param sf The session factory.
     * @param uuid The UUID of the user.
     * @param username The new username.
     * @param name The new name.
     * @param email The new email.
     * @param roles The list of new roles UUID.
     * @param password A new password, can be null if password is not be be changed.
     * @return A void.
     */
    public static Uni<Product> updateProduct(SessionFactory sf,
                                        UUID uuid, 
                                        String name,
                                        String description,
                                        UUID current)
    {
        return sf.withTransaction((s,t)->
            Product.getProduct(s, uuid).
            map(product-> {
                    // Update the user
                    product.name = name;
                    product.description = description;
                    product.current = null;
                    product.versions.forEach(v -> product.current = (v.id.compareTo(current)==0)?v:product.current);
                    return product;
                })
            );
    }

    /**
     * Creates a new user.
     * 
     * @param sf The session factory.
     * @param name The name of the product.
     * @param description The description of the product.
     * @return A new product.
     */
    public static Uni<Product> createProduct(SessionFactory sf,
                                        String name,
                                        String description)
    {
        return sf.withTransaction((s,t)-> {
                    Product product = new Product();

                    // Update the user
                    product.name = name;
                    product.description = description;
                    
                    // Return with data
                    return Product.createProduct(s, product);
                }
        );
    }

    /**
     * Deletes a user based on the UUID.
     * 
     * @param sf The session factory.
     * @param uuid The UUID of the product.
     * @return A void.
     */
    public static Uni<Void> deleteProduct(SessionFactory sf,
                                            UUID uuid)
    {
        return sf.withTransaction((s,t)->Product.deleteProduct(s, uuid));
    }
}

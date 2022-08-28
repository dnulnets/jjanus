package eu.stenlund.janus.ssr.model;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.jboss.logging.Logger;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.base.JanusNoSuchItemException;
import eu.stenlund.janus.model.Product;
import eu.stenlund.janus.model.Team;
import eu.stenlund.janus.model.base.JanusEntity;
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
public class ProductPage {

    private static final Logger log = Logger.getLogger(ProductPage.class);

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
     * The team that are responsible for the product
     */
    public Select teams;

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
     * @param teams List of available teams.
     * @param roles The available roles in the application.
     * @param back The URL for sending the user back to the page before, e.g. when pressing cancel, save and delete.
     * @param newProduct Flag telling if it is a new user page or an update/edit page.
     * @param locale The locale of the page.
     */
    public ProductPage(Product product, List<Team> teams, URI back, boolean newProduct, String locale) {

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
                product.current!=null?product.current.id.compareTo(v.id)==0:false, false, v.id.toString())).toList();
        this.current = new Select(msg.product_current_versions(),"current", "id-current", l, false, null);

        // Create the teams
        List<Select.Item> m = 
            teams.stream().map(v -> {
                Team t = Team.findTeamById(product.teams, v.id);
                return new Select.Item(v.name, t!=null?v.id.compareTo(t.id)==0:false, false, v.id.toString());
            }).toList();
        this.teams = new Select(msg.product_teams(),"teams", "id-teams", m, false, null);

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
    public static Uni<ProductPage> createModel (SessionFactory sf, UUID uuid, URI uri, String locale)
    {
        if (uuid == null) {
            return sf.withSession(s -> Team.getList(s)).
                chain(teams -> Uni.createFrom().item(new ProductPage(new Product(), teams, uri, true, locale)));
        } else {
            return Uni.combine().all().unis(
                sf.withSession(s -> JanusEntity.get(Product.class, s, uuid)).
                    onItem().
                        ifNull().
                            failWith(new JanusNoSuchItemException("Failed to read the product from the database using the given uuid."
                                , "product"
                                , uuid.toString()
                                , uri.toString())),
                sf.withSession(s -> Team.getList(s))).
            combinedWith((product, teams)->new ProductPage(product, teams, uri, false, locale));
        }
    }

    /**
     * Updates a product.
     * 
     * @param sf Session factory.
     * @param uuid The UUID of the product.
     * @param name The name of the product.
     * @param description Description of the product.
     * @param teams The teams responsible or the product.
     * @param current The current version of the product.
     * @return An updated product.
     */
    public static Uni<Product> updateProduct(SessionFactory sf,
                                        UUID uuid, 
                                        String name,
                                        String description,
                                        UUID[] teams,
                                        UUID current)
    {

        return sf.withTransaction((ss,tt) -> Uni.combine().all().unis(
                sf.withSession(s->JanusEntity.get(Product.class, s, uuid)),
                sf.withSession(s->Team.getList(s))).
            combinedWith((product, tl) -> {
                
                    // Update the user
                    product.name = name;
                    product.description = description;
                    product.current = null;
                    if (current != null)
                        product.versions.forEach(v -> product.current = (v.id.compareTo(current)==0)?v:product.current);
                    product.clearTeams();
                    for(UUID tid : teams) {
                        Team team = Team.findTeamById(tl, tid);
                        if (team != null)
                            product.addTeam(team);
                    }
                    return product;
            }));
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
                                        String description,
                                        UUID[] teams)
    {
        return sf.withTransaction((ss,tt) -> Team.getList(ss).
        map(tl-> { 
            Product product = new Product();

            // Update the user
            product.name = name;
            product.description = description;
            product.current = null;
            for(UUID tid : teams) {
                Team team = Team.findTeamById(tl, tid);
                if (team != null)
                    product.addTeam(team);
            }
            return product;
        }).chain(product -> JanusEntity.create(ss, product)));
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
        return sf.withTransaction((ss, tt) ->
            JanusEntity.get(Product.class, ss, uuid).
            map (p -> {
                p.clearTeams();
                return p;
            }).
            chain (p->ss.remove(p)));
    }
}

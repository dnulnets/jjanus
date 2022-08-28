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
import eu.stenlund.janus.model.Team;
import eu.stenlund.janus.model.User;
import eu.stenlund.janus.model.base.JanusEntity;
import eu.stenlund.janus.msg.TeamManagement;
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
public class TeamManagementTeam {

    private static final Logger log = Logger.getLogger(TeamManagementTeam.class);

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
    public TextInput uuid;

    /*
     * All users in the team
     */
    public Select members;

    /*
     * Available and chosen products for the team.
     */
    public Select products;

    /*
     * The URL:s for create and delete of the user.
     */
    public String createURL;
    public String updateURL;

    /**
     * Flag so we know if it is a new user that we want to create.
     */
    public boolean newTeam;


    /**
     * Creates the workarea for the user interface based on existing user and roles.
     * 
     * @param team The user for this page.
     * @param roles The available roles in the application.
     * @param back The URL for sending the user back to the page before, e.g. when pressing cancel, save and delete.
     * @param newUser Flag telling if it is a new user page or an update/edit page.
     * @param locale The locale of the page.
     */
    public TeamManagementTeam(Team team, List<Product> products, URI back, boolean newUser, String locale) {

        // Create action URL:s
        String ROOT_PATH = JanusHelper.getConfig(String.class, "janus.http.root-path","/");

        String deleteURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("team")
            .segment("delete")
            .build().toString();

        createURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("team")
            .segment("create")
            .build().toString();

        updateURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("team")
            .build().toString();

        String backURL = ROOT_PATH; 
        if (back != null)
            backURL = back.toString();

        // Get hold of the message bundle
        TeamManagement msg = JanusTemplateHelper.getMessageBundle(TeamManagement.class, locale);

        // Set the new user flag if we are creating a new user
        this.newTeam = newUser;

        // Create the buttons for delete, save and cancel
        if (newUser)
            deleteButton = null;
        else
            deleteButton = new Button (msg.team_delete(), deleteURL, null);
        saveButton = new Button (msg.team_save(), null);
        cancelButton = new Button(msg.team_cancel(), backURL, JanusSSRHelper.unpolyFollow());

        // Create the form's text inputs
        name = new TextInput(msg.team_name(), "name", "id-name", team.name, msg.team_must_have_name(), JanusSSRHelper.required());
        uuid = new TextInput("UUID", "uuid", "id-uuid", team.id!=null?team.id.toString():null, null, JanusSSRHelper.readonly());

    
        // Create the products
        List<Select.Item> m = 
            products.stream().map(v -> {
                Product t = Product.findProductById(team.products, v.id);
                return new Select.Item(v.name, t!=null?v.id.compareTo(t.id)==0:false, false, v.id.toString());
            }).toList();
        this.products = new Select(msg.team_products(),"products", "id-products", m, false, null);

        // Create the members
        List<Select.Item> n = 
            team.members.stream().map(v -> new Select.Item(v.name+" ("+v.username + ")", false, true, v.id.toString())).toList();
        this.members = new Select (msg.team_members(), "members", "id-members", n, false, JanusSSRHelper.disabled());
        
        // Create the form
        form = new Form(Form.POST, newUser?createURL:updateURL, true, JanusSSRHelper.unpolySubmit(backURL));
    }

    /**
     * Creates the model from data in the database that can be used for the page.
     * 
     * @param sf The session factory.
     * @param uuid The UUID of the team.
     * @param uri The URI of the cancel or return URL.
     * @return A populated TeamMangagementTeam.
     */
    public static Uni<TeamManagementTeam> createModel (SessionFactory sf, UUID uuid, URI uri, String locale)
    {
        if (uuid == null) {
            return sf.withSession (s -> Product.getList(s)).
                chain (pl -> Uni.createFrom().item(new TeamManagementTeam(new Team(),pl,uri, true, locale)));

        } else {
            return Uni.combine().all().unis(
                sf.withSession(s->Product.getList(s)),
                sf.withSession(s -> JanusEntity.get (Team.class, s, uuid)
                    .onItem()
                        .ifNull()
                            .failWith(new JanusNoSuchItemException("Failed to read the team from the database using the given uuid."
                                , "team"
                                , uuid.toString()
                                , uri.toString())))).
            combinedWith((list, team)-> new TeamManagementTeam(team, list, uri, false, locale));
        }
    }

    /**
     * Update a team based on the team uuid.
     * 
     * @param sf The session factory.
     * @param uuid The UUID of the team.
     * @param name The new name.
     * @return an updated team.
     */
    public static Uni<Team> updateTeam(SessionFactory sf,
                                        UUID uuid, 
                                        String name,
                                        UUID [] products)
    {
        return sf.withTransaction((ss,tt) -> Uni.combine().all().unis(
            sf.withSession(s -> JanusEntity.get (Team.class, s, uuid)),
            sf.withSession(s -> Product.getList(s))).
        combinedWith ((team, pl) -> {

            // Update the user
            team.name = name;

            team.clearProducts();
            for(UUID pid : products) {
                Product product = Product.findProductById(pl, pid);
                if (product != null)
                    team.addProduct (product);
            }

            // Return with data
            return team;
        }));
    }

    /**
     * Creates a new team.
     * 
     * @param sf The session factory.
     * @param name The name of the team.
     * @return A new team.
     */
    public static Uni<Team> createTeam(SessionFactory sf,
                                        String name,
                                        UUID [] products)
    {
        return sf.withTransaction((s,t)-> 
            Product.getList(s).chain(pl -> {
                    Team team = new Team();

                    // Update the user
                    team.name = name;

                    for(UUID pid : products) {
                        Product product = Product.findProductById(pl, pid);
                        if (product != null)
                            team.addProduct (product);
                    }
                            
                    // Return with data
                    return JanusEntity.create (s, team);
                }
        ));
    }

    /**
     * Deletes a team based on the UUID.
     * 
     * @param sf The session factory.
     * @param uuid The UUID of the team.
     * @return A void.
     */
    public static Uni<Void> deleteTeam(SessionFactory sf,
                                        UUID uuid)
    {
        return sf.withTransaction((ss, tt) ->
            JanusEntity.get(Team.class, ss, uuid).
            map (t -> {
                t.clearMembers();
                t.clearProducts();
                return t;
            }).
            chain (t->ss.remove(t)));
    }
}

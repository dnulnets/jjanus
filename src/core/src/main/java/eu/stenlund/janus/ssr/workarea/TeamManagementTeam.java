package eu.stenlund.janus.ssr.workarea;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.jboss.logging.Logger;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.base.JanusNoSuchItemException;
import eu.stenlund.janus.model.Team;
import eu.stenlund.janus.model.User;
import eu.stenlund.janus.model.base.JanusEntity;
import eu.stenlund.janus.msg.TeamManagement;
import eu.stenlund.janus.ssr.JanusSSRHelper;
import eu.stenlund.janus.ssr.JanusTemplateHelper;
import eu.stenlund.janus.ssr.ui.Button;
import eu.stenlund.janus.ssr.ui.Form;
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
    public TeamManagementTeam(Team team, List<User> users, URI back, boolean newUser, String locale) {

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

        // Create the form
        form = new Form(Form.POST, newUser?createURL:updateURL, true, JanusSSRHelper.unpolySubmit(backURL));
    }

    /**
     * Creates the model from data in the database that can be used for the page.
     * 
     * @param sf The session factory.
     * @param uuid The UUID of the user.
     * @param uri The URI of the cancel or return URL.
     * @return A populated UserManagementUser.
     */
    public static Uni<TeamManagementTeam> createModel (SessionFactory sf, UUID uuid, URI uri, String locale)
    {
        if (uuid == null) {
            return sf.withSession(s -> User.getList(s))
            .map(lr -> new TeamManagementTeam(
                    new Team(),
                    lr,
                    uri, true, locale));        
        } else {
            return Uni.combine().all().unis(
                sf.withSession(s -> JanusEntity.get (Team.class, s, uuid)
                    .onItem()
                        .ifNull()
                            .failWith(new JanusNoSuchItemException("Failed to read the team from the database using the given uuid."
                                , "team"
                                , uuid.toString()
                                , uri.toString()))),
                sf.withSession(s -> User.getList(s))).
            combinedWith((team, list)-> new TeamManagementTeam(
                    team,
                    list,
                    uri, false, locale));
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
    public static Uni<Team> updateTeam(SessionFactory sf,
                                        UUID uuid, 
                                        String name,
                                        UUID[] users)
    {
        return sf.withTransaction((s,t)->
        JanusEntity.get (Team.class, s, uuid).
        map(team-> {
                // Update the user
                team.name = name;
                // Return with data
                return team;
            }
        ));
    }

    /**
     * Creates a new user.
     * 
     * @param sf The session factory.
     * @param username The username of the user.
     * @param name The name of the user.
     * @param email The email of the user.
     * @param roles The UUID of the roles for the user.
     * @param password The password of the user.
     * @return A new user.
     */
    public static Uni<Team> createTeam(SessionFactory sf,
                                        String name,
                                        UUID[] users)
    {
        return sf.withTransaction((s,t)-> {
                    Team team = new Team();

                    // Update the user
                    team.name = name;
                    
                    // Return with data
                    return JanusEntity.create (s, team);
                }
        );
    }

    /**
     * Deletes a user based on the UUID.
     * 
     * @param sf The session factory.
     * @param uuid The UUID of the user.
     * @return A void.
     */
    public static Uni<Void> deleteTeam(SessionFactory sf,
                                        UUID uuid)
    {
        return sf.withTransaction((s,t)->JanusEntity.delete (Team.class, s, uuid));
    }
}

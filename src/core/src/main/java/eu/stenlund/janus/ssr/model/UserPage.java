package eu.stenlund.janus.ssr.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.jboss.logging.Logger;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.model.Role;
import eu.stenlund.janus.model.Team;
import eu.stenlund.janus.model.User;
import eu.stenlund.janus.model.base.JanusEntity;
import eu.stenlund.janus.msg.UserManagement;
import eu.stenlund.janus.ssr.JanusSSRHelper;
import eu.stenlund.janus.ssr.JanusTemplateHelper;
import eu.stenlund.janus.ssr.ui.Button;
import eu.stenlund.janus.ssr.ui.Form;
import eu.stenlund.janus.ssr.ui.Select;
import eu.stenlund.janus.ssr.ui.TextInput;
import io.smallrye.mutiny.Uni;

/**
 * The data model for the pages for user edit, create and delete routes.
 *
 * @author Tomas Stenlund
 * @since 2022-07-29
 * 
 */
public class UserPage {

    /**
     * Get hold of the applications URI info and builder
     */
    @Context
    UriInfo uriInfo;

    private static final Logger log = Logger.getLogger(UserPage.class);

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
    public TextInput username;
    public TextInput email;
    public TextInput password;
    public TextInput uuid;

    /*
     * The URL:s for create and delete of the user.
     */
    public String createURL;
    public String updateURL;

    /**
     * The roles list
     */
    public Select roles;

    /**
     * The teams list
     */
    public Select teams;

    /**
     * Flag so we know if it is a new user that we want to create.
     */
    public boolean newUser;

    /**
     * Creates the workarea for the user interface based on existing user and roles.
     * 
     * @param user    The user for this page.
     * @param roles   The available roles in the application.
     * @param back    The URL for sending the user back to the page before, e.g.
     *                when pressing cancel, save and delete.
     * @param newUser Flag telling if it is a new user page or an update/edit page.
     * @param locale  The locale of the page.
     */
    public UserPage(User user, List<Role> roles, List<Team> teams, URI back, boolean newUser, String locale) {

        // Create action URL:s
        String ROOT_PATH = JanusHelper.getConfig(String.class, "janus.http.root-path", "/");

        String deleteURL = UriBuilder.fromPath(ROOT_PATH)
                .segment("user")
                .segment("delete")
                .build().toString();

        createURL = UriBuilder.fromPath(ROOT_PATH)
                .segment("user")
                .segment("create")
                .build().toString();

        updateURL = UriBuilder.fromPath(ROOT_PATH)
                .segment("user")
                .build().toString();

        String backURL = ROOT_PATH;
        if (back != null)
            backURL = back.toString();

        // Get hold of the message bundle
        UserManagement msg = JanusTemplateHelper.getMessageBundle(UserManagement.class, locale);

        // Set the new user flag if we are creating a new user
        this.newUser = newUser;

        // Create the buttons for delete, save and cancel
        if (newUser)
            deleteButton = null;
        else
            deleteButton = new Button(msg.user_delete(), deleteURL, null);
        saveButton = new Button(msg.user_save(), null);
        cancelButton = new Button(msg.user_cancel(), backURL, JanusSSRHelper.unpolyFollow());

        // Create the form's text inputs
        name = new TextInput(msg.user_name(), "name", "id-name", user.name, msg.user_must_have_name(), JanusSSRHelper.required());
        username = new TextInput(msg.user_username(), "username", "id-username", user.username,
                msg.user_must_have_username(), JanusSSRHelper.required() + " " + JanusSSRHelper.ignorePasswordManagers());
        uuid = new TextInput("UUID", "uuid", "id-uuid", user.id != null ? user.id.toString() : null, null, JanusSSRHelper.readonly());
        email = new TextInput(msg.user_email(), "email", "id-email", user.email, msg.user_must_have_email(),JanusSSRHelper.required());
        password = new TextInput(msg.user_password(), "password", "id-password", null,
                newUser ? msg.user_must_have_password() : null,
                JanusSSRHelper.ignorePasswordManagers()+ " " + (newUser ? JanusSSRHelper.required() : ""));

        // Create the form's multiselect role
        List<Select.Item> ritems = new ArrayList<Select.Item>(roles.size());
        roles.forEach(
                role -> ritems.add(new Select.Item(role.longName, user.hasRole(role.name), false, role.id.toString())));
        this.roles = new Select(msg.user_roles(), "roles", "id-roles", ritems, true, null);

        // Create the forms multiselect teams
        List<Select.Item> items = new ArrayList<Select.Item>(teams.size());
        teams.forEach(
                team -> items.add(new Select.Item(team.name, user.belongsToTeam(team.id), false, team.id.toString())));
        this.teams = new Select(msg.user_teams(), "teams", "id-teams", items, false, null);

        // Create the form
        form = new Form(Form.POST, newUser?createURL:updateURL, true, JanusSSRHelper.unpolySubmit(backURL));

    }

    /**
     * Creates the model from data in the database that can be used for the page.
     * 
     * @param sf   The session factory.
     * @param uuid The UUID of the user.
     * @param uri  The URI of the cancel or return URL.
     * @return A populated UserManagementUser.
     */
    public static Uni<UserPage> createModel(SessionFactory sf, UUID uuid, URI uri, String locale) {
        if (uuid == null)
            return Uni.combine().all().unis(
                    sf.withSession(s -> Role.getList(s)),
                    sf.withSession(s -> Team.getList(s)))
                    .combinedWith((roles, teams) -> new UserPage(
                            new User(),
                            roles,
                            teams,
                            uri, true, locale));
        else
            return Uni.combine().all().unis(
                    sf.withSession(s -> JanusEntity.get (User.class, s, uuid)),
                    sf.withSession(s -> Role.getList(s)),
                    sf.withSession(s -> Team.getList(s)))
                    .combinedWith((user, roles, teams) -> new UserPage(
                            user, roles, teams,
                            uri, false, locale));
    }

    /**
     * Update a user based on the user uuid and a new set of roles and attributes.
     * 
     * @param sf       The session factory.
     * @param uuid     The UUID of the user.
     * @param username The new username.
     * @param name     The new name.
     * @param email    The new email.
     * @param roles    The list of new roles UUID.
     * @param password A new password, can be null if password is not be be changed.
     * @return A void.
     */
    public static Uni<User> updateUser(SessionFactory sf,
            UUID uuid,
            String username,
            String name,
            String email,
            UUID[] roles,
            UUID[] teams,
            String password) {
        return sf.withTransaction((ss, tt) ->
            Uni.combine().all().unis(
                    sf.withSession(s->JanusEntity.get (User.class, s, uuid)), // Get the user
                    sf.withSession(s->Role.getList(s)), // Get all available roles
                    sf.withSession(s->Team.getList(s))). // Get all available teams

                    // Update the user with all new values
                    combinedWith((user, listOfRoles, listOfTeams) -> {

                        // Update the user
                        user.name = name;
                        user.username = username;
                        user.email = email;
                        if (!JanusHelper.isBlank(password))
                            user.setPassword(password);

                        // Add roles
                        user.roles.clear();
                        for (UUID ruid : roles) {
                            Role role = Role.findRoleById(listOfRoles, ruid);
                            if (role != null)
                                user.roles.add(role);
                        }

                        // Add teams
                        user.clearTeams();
                        for (UUID tuid : teams) {
                            Team team = Team.findTeamById(listOfTeams, tuid);
                            if (team != null) {
                                user.teams.add(team);
                                team.members.add(user);
                            }
                        }

                        // Return with data
                        return user;
                    }));
    }

    /**
     * Creates a new user.
     * 
     * @param sf       The session factory.
     * @param username The username of the user.
     * @param name     The name of the user.
     * @param email    The email of the user.
     * @param roles    The UUID of the roles for the user.
     * @param password The password of the user.
     * @return A new user.
     */
    public static Uni<User> createUser(SessionFactory sf,
            String username,
            String name,
            String email,
            UUID[] roles,
            UUID[] teams,
            String password) {
        return sf.withTransaction((ss, tt) ->

        Uni.combine().all().unis(
                sf.withSession(s->Role.getList(s)), // Get all available roles
                sf.withSession(s->Team.getList(s))). // Get all available teams

                // Create the user with all new values
                combinedWith((listOfRoles, listOfTeams) -> {

                    // Create a new user
                    User user = new User();
                    
                    // Update the user
                    user.name = name;
                    user.username = username;
                    user.email = email;
                    if (!JanusHelper.isBlank(password))
                        user.setPassword(password);

                    // Add roles
                    for (UUID ruid : roles) {
                        Role role = Role.findRoleById(listOfRoles, ruid);
                        if (role != null)
                            user.roles.add(role);
                    }

                    // Add teams
                    for (UUID tuid : teams) {
                        Team team = Team.findTeamById(listOfTeams, tuid);
                        if (team != null)
                            user.addTeam(team);
                    }

                    // Return with data
                    return user;
                }).

                // Add it to the database
                chain(u->JanusEntity.create(ss, u)));

    }

    /**
     * Deletes a user based on the UUID.
     * 
     * @param sf   The session factory.
     * @param uuid The UUID of the user.
     * @return A void.
     */
    public static Uni<Void> deleteUser(SessionFactory sf,
            UUID uuid) {

        return sf.withTransaction((ss, tt) ->
            JanusEntity.get(User.class, ss, uuid).
            map (u -> {
                u.clearTeams();
                return u;
            }).
            chain (u->ss.remove(u)));
    }
}

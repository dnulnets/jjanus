package eu.stenlund.janus.ssr.workarea;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.config.ConfigProvider;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.base.JanusTemplateHelper;
import eu.stenlund.janus.model.Role;
import eu.stenlund.janus.model.User;
import eu.stenlund.janus.msg.UserManagement;
import eu.stenlund.janus.ssr.ui.Button;
import eu.stenlund.janus.ssr.ui.Checkbox;
import eu.stenlund.janus.ssr.ui.Form;
import eu.stenlund.janus.ssr.ui.TextInput;
import io.smallrye.mutiny.Uni;

/**
 * The data model for the pages for user edit, create and delete routes.
 *
 * @author Tomas Stenlund
 * @since 2022-07-29
 * 
 */
public class UserManagementUser {

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
     * The checkboxes for the available roles to choose from in the application.
     */
    public List<Checkbox> roles;

    /**
     * Flag so we know if it is a new user that we want to create.
     */
    public boolean newUser;


    /**
     * Creates the workarea for the user interface based on existing user and roles.
     * 
     * @param user The user for this page.
     * @param roles The available roles in the application.
     * @param back The URL for sending the user back to the page before, e.g. when pressing cancel, save and delete.
     * @param newUser Flag telling if it is a new user page or an update/edit page.
     * @param locale The locale of the page.
     */
    public UserManagementUser(User user, List<Role> roles, URI back, boolean newUser, String locale) {

        // Create the URL:s
        String ROOT_PATH = ConfigProvider.getConfig().getValue("janus.http.root-path", String.class);
        String deleteURL = ROOT_PATH + "/user/delete";
        createURL = ROOT_PATH + "/user/create";
        updateURL = ROOT_PATH + "/user";

        // Get hold of the message bundle
        UserManagement msg = JanusTemplateHelper.getMessageBundle(UserManagement.class, locale);

        // Set the new user flag if we are creating a new user
        this.newUser = newUser;

        // Create the buttons for delete, save and cancel
        if (newUser)
            deleteButton = null;
        else
            deleteButton = new Button (msg.user_delete(), deleteURL, null);
        saveButton = new Button (msg.user_save(), null);
        cancelButton = new Button(msg.user_cancel(), back.toString(), "up-follow up-history=\"true\" up-target=\"#workarea\"");

        // Create the form's text inputs
        name = new TextInput(msg.user_name(), "name", "id-name", user.name, msg.user_must_have_name(), "required");
        username = new TextInput(msg.user_username(), "username", "id-username", user.username, msg.user_must_have_username(), "required");
        uuid = new TextInput("UUID", "uuid", "id-uuid", user.id!=null?user.id.toString():null, null, "readonly");
        email = new TextInput(msg.user_email(), "email", "id-email", user.email, msg.user_must_have_email(), "required");
        password = new TextInput(msg.user_password(), "password", "id-password", null, newUser?msg.user_must_have_password():null, newUser?"required":null);

        // Create the form's role checkboxes
        this.roles = new ArrayList<Checkbox>(roles.size());
        roles.forEach(role -> this.roles.add(new Checkbox(role.longName, "roles", "id-" + role.name, role.id.toString(), user.hasRole(role.name), null)));

        // Create the form
        if (newUser)
            form = new Form(Form.POST, createURL, true);
        else
            form = new Form(Form.POST, updateURL, true);
    }

    /**
     * Creates the model from data in the database that can be used for the page.
     * 
     * @param sf The session factory.
     * @param uuid The UUID of the user.
     * @param uri The URI of the cancel or return URL.
     * @return A populated UserManagementUser.
     */
    public static Uni<UserManagementUser> createModel (SessionFactory sf, UUID uuid, URI uri, String locale)
    {
        if (uuid == null)
            return sf.withSession(s -> Role.getListOfRoles(s))
            .map(lr -> new UserManagementUser(
                    new User(),
                    lr,
                    uri, true, locale));        
        else
            return Uni.combine().all().unis(
                sf.withSession(s -> User.getUser(s, uuid)),
                sf.withSession(s -> Role.getListOfRoles(s))).asTuple()
            .map(lu -> new UserManagementUser(
                    lu.getItem1(),
                    lu.getItem2(),
                    uri, false, locale));
    }

    /**
     * Update a user based on the user uuid and a new set of roles and attributes.
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
    public static Uni<User> updateUser(SessionFactory sf,
                                        UUID uuid, 
                                        String username,
                                        String name,
                                        String email,
                                        UUID[] roles,
                                        String password)
    {
        return sf.withTransaction((s,t)->
        Uni.combine().all().unis(
            User.getUser(s, uuid),
            Role.getListOfRoles(s)
        ).asTuple().
        map(lu-> {
                User user = lu.getItem1();
                List<Role> listOfRoles = lu.getItem2();

                // Update the user
                user.name = name;
                user.username = username;
                user.email = email;
                if (JanusHelper.isValid(password))
                    user.setPassword(password);

                // Add roles
                user.roles.clear();
                for (UUID ruid : roles) {
                    Role r = Role.findRoleById(listOfRoles, ruid);
                    if (r!=null)
                        user.roles.add(r);
                }

                // Return with data
                return user;
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
    public static Uni<User> createUser(SessionFactory sf,
                                        String username,
                                        String name,
                                        String email,
                                        UUID[] roles,
                                        String password)
    {
        return sf.withTransaction((s,t)->
            Role.getListOfRoles(s).
            chain(lr-> {
                    User user = new User();

                    // Update the user
                    user.name = name;
                    user.username = username;
                    user.email = email;
                    if (password != null) {
                        if (password.length() > 0)
                            user.setPassword(password);
                    }

                    // Add roles
                    user.roles = new HashSet<Role>();
                    for (UUID ruid : roles) {
                        Role r = Role.findRoleById(lr, ruid);
                        if (r!=null)
                            user.roles.add(r);
                    }

                    // Return with data
                    return User.addUser(s, user);
                }
            ));
    }

    /**
     * Deletes a user based on the UUID.
     * 
     * @param sf The session factory.
     * @param uuid The UUID of the user.
     * @return A void.
     */
    public static Uni<Void> deleteUser(SessionFactory sf,
                                        UUID uuid)
    {
        return sf.withTransaction((s,t)->User.deleteUser(s, uuid));
    }
}

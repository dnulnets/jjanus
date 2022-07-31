package eu.stenlund.janus.model.workarea;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.config.ConfigProvider;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import eu.stenlund.janus.model.Role;
import eu.stenlund.janus.model.User;
import io.smallrye.mutiny.Uni;

/**
 * The Workarea for UserManagement user, create, delete route
 *
 * @author Tomas Stenlund
 * @since 2022-07-29
 * 
 */
public class UserManagementUser {

    /**
     * The root path, comes from configuration.
     */
    private String ROOT_PATH;

    /**
     * Flag so we know if it is a new user that we want to create.
     */
    public boolean newUser;

    /**
     * The current user for the user interface.
     */
    public User user;

    /**
     * The available roles to choose from in the application.
     */
    public List<Role> roles;

    /**
     * Return url when the user presses cancel/back.
     */
    public URI uri;

    /**
     * Returns true if the user has a specific role.
     * 
     * @param role The role that you want to check.
     * @return True if the user has the role.
     */
    public boolean hasRole(String role) {
        return user.hasRole(role);
    }

    /**
     * Creates the URL used to delete users.
     * 
     * @return The URL.
     */
    public String deleteURL()
    {
        return ROOT_PATH + "/user/delete";
    }

    /**
     * Create the URL for creating a user.
     * 
     * @return The URL.
     */
    public String createURL()
    {
        return ROOT_PATH + "/user/create";
    }

    /**
     * Create the URL for updating a user.
     * 
     * @return The URL.
     */
    public String updateURL()
    {
        return ROOT_PATH + "/user";
    }

    /**
     * Creates the workarea for the user interface based on existing user and roles.
     * 
     * @param u    The current user.
     * @param r    The list of available roles in the application.
     * @param back The return URI if the user presses cancel.
     */
    public UserManagementUser(User u, List<Role> r, URI back, boolean nu) {
        user = u;
        roles = r;
        uri = back;
        newUser = nu;
        ROOT_PATH = ConfigProvider.getConfig().getValue("janus.http.root-path", String.class);
    }

    /**
     * Creates a UserManagementUser from data in the database.
     * 
     * @param sf The session factory.
     * @param uuid The UUID of the user.
     * @param uri The URI of the cancel or return URL.
     * @return A populated UserManagementUser.
     */
    public static Uni<UserManagementUser> createUserManagementUser (SessionFactory sf, UUID uuid, URI uri)
    {
        if (uuid == null)
            return sf.withSession(s -> Role.getListOfRoles(s))
            .map(lr -> new UserManagementUser(
                    new User(),
                    lr,
                    uri, true));        
        else
            return Uni.combine().all().unis(
                sf.withSession(s -> User.getUser(s, uuid)),
                sf.withSession(s -> Role.getListOfRoles(s))).asTuple()
            .map(lu -> new UserManagementUser(
                    lu.getItem1(),
                    lu.getItem2(),
                    uri, false));
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
                List<Role> lr = lu.getItem2();

                // Update the user
                user.name = name;
                user.username = username;
                user.email = email;
                if (password != null) {
                    if (password.length() > 0)
                        user.setPassword(password);
                }

                // Add roles
                user.roles.clear();
                for (UUID ruid : roles) {
                    Role r = Role.findRoleById(lr, ruid);
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

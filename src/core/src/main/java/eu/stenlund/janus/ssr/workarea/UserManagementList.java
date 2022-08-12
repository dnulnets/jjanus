package eu.stenlund.janus.ssr.workarea;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.base.JanusTemplateHelper;
import eu.stenlund.janus.model.User;
import eu.stenlund.janus.msg.UserManagement;
import eu.stenlund.janus.ssr.JanusSSRHelper;
import eu.stenlund.janus.ssr.ui.Base;
import eu.stenlund.janus.ssr.ui.Button;
import eu.stenlund.janus.ssr.ui.Table;
import eu.stenlund.janus.ssr.ui.Text;
import io.smallrye.mutiny.Uni;

/**
 * The Workarea for UserManagement list route /user/list
 *
 * @author Tomas Stenlund
 * @since 2022-07-21
 * 
 */
public class UserManagementList {

    /**
     * The user table
     */
    public Table table;
 
    /**
     * Create a new workarea from the users inthe database.
     * 
     * @param users The users for this page.
     * @param total Total number of users in the database.
     * @param six The index for the first row in the table.
     * @param max The max number of users in the table.
     * @param locale The locale to render.
     */
    private UserManagementList(List<User> users, int total, int six, int max, String locale) {

        // Get hold of the message bundle and root path
        String ROOT_PATH = JanusHelper.getConfig(String.class, "janus.http.root-path", "/");
        UserManagement msg = JanusTemplateHelper.getMessageBundle(UserManagement.class, locale);

        // Create the action URL:s
        String returnURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("user")
            .segment("list")
            .queryParam("six", six)
            .queryParam("max", max)
            .build().toString();
        
        String tableURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("user")
            .segment("list")
            .build().toString();

        String createURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("user")
            .segment("create")
            .queryParam("return", returnURL)
            .build().toString();

        // Create the table header
        List<String> columns = new ArrayList<String>(4);
        columns.add(msg.list_name());
        columns.add(msg.list_username());
        columns.add(msg.list_email());
        columns.add(msg.list_teams());
        columns.add(msg.list_roles());
        columns.add(msg.list_action());

        // Create the table data matrix
        List<List<Base>> data = new ArrayList<List<Base>>(users.size());
        users.forEach(user -> {
            List<Base> row = new ArrayList<Base>(columns.size());
            row.add(new Text (user.name));
            row.add(new Text (user.username));
            row.add(new Text (user.email));
            String teams = user.teams.stream().map(t -> t.name).collect(Collectors.joining("<br/>"));
            row.add (new Text(teams, true));
            String roles = user.roles.stream().map(r -> r.longName).collect(Collectors.joining("<br/>"));
            row.add(new Text (roles, true));
            String actionURL  = UriBuilder.fromPath(ROOT_PATH)
                .segment("user")
                .queryParam("uuid", user.id)
                .queryParam("return", returnURL)
                .build().toString();
            row.add(new Button (msg.list_edit(), actionURL, JanusSSRHelper.unpolyFollow()));
            data.add(row);
        });

        table = new Table(columns, data, tableURL, msg.list_add(), createURL, six, max, total);
    }

    /**
     * Factory function for a UserManagementList based on data in the database.
     * 
     * @param sf The mutiny session factory.
     * @param start Start index of the ist
     * @param max Max number of items to retrieve
     * @return A user management list
     */
    public static Uni<UserManagementList> createModel(SessionFactory sf, int start, int max, String locale)
    {
        return Uni.combine().all().unis(
            sf.withSession(s -> User.getListOfUsers(s, start, max)),
            sf.withSession(s -> User.getNumberOfUsers(s)))
        .combinedWith((users, nof) -> new UserManagementList(
                users,
                nof.intValue(),
                start,
                max,
                locale));
    }  

}

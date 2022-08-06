package eu.stenlund.janus.ssr.workarea;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.ConfigProvider;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.base.JanusTemplateHelper;
import eu.stenlund.janus.base.URLBuilder;
import eu.stenlund.janus.model.User;
import eu.stenlund.janus.msg.UserManagement;
import eu.stenlund.janus.ssr.ui.Table;
import eu.stenlund.janus.ssr.ui.Text;
import eu.stenlund.janus.ssr.ui.Base;
import eu.stenlund.janus.ssr.ui.Button;
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
        String returnURL = URLBuilder.root(ROOT_PATH)
            .addSegment("user")
            .addSegment("list")
            .addQueryParameter("six", String.valueOf(six))
            .addQueryParameter("max", String.valueOf(max))
            .build();
        
        String tableURL = URLBuilder.root(ROOT_PATH)
            .addSegment("user")
            .addSegment("list")
            .build();

        String createURL = URLBuilder.root(ROOT_PATH)
            .addSegment("user")
            .addSegment("create")
            .addQueryParameter("return", returnURL)
            .build();

        // Create the table header
        List<String> columns = new ArrayList<String>(4);
        columns.add(msg.list_name());
        columns.add(msg.list_username());
        columns.add(msg.list_email());
        columns.add(msg.list_roles());
        columns.add(msg.list_action());

        // Create the table data matrix
        List<List<Base>> data = new ArrayList<List<Base>>(users.size());
        users.forEach(user -> {
            List<Base> row = new ArrayList<Base>(columns.size());
            row.add(new Text (user.name));
            row.add(new Text (user.username));
            row.add(new Text (user.email));
            String s = user.roles.stream().map(r -> r.longName).collect(Collectors.joining("<br/>"));
            row.add(new Text (s, true));
            String actionURL  = URLBuilder.root(ROOT_PATH)
                .addSegment("user")
                .addQueryParameter("uuid", user.id.toString())
                .addQueryParameter("return", returnURL)
                .build();
            row.add(new Button (msg.list_edit(), actionURL, ""));
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
            sf.withSession(s -> User.getNumberOfUsers(s))).asTuple()
        .map(lu -> new UserManagementList(
                lu.getItem1(),
                lu.getItem2().intValue(),
                start,
                max,
                locale));
    }  

}

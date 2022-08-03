package eu.stenlund.janus.ssr.workarea;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.ConfigProvider;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import eu.stenlund.janus.base.JanusTemplateHelper;
import eu.stenlund.janus.model.User;
import eu.stenlund.janus.msg.UserManagement;
import eu.stenlund.janus.ssr.ui.TableAction;
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
    public TableAction table;
 
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
        String ROOT_PATH = ConfigProvider.getConfig().getValue("janus.http.root-path", String.class);
        UserManagement msg = JanusTemplateHelper.getMessageBundle(UserManagement.class, locale);
        String returnURL = URLEncoder.encode(ROOT_PATH + "/user/list?six=" + String.valueOf(six) + "&max="+String.valueOf(max),
            Charset.defaultCharset());
        String tableURL = ROOT_PATH + "/user/list";
        String createURL = ROOT_PATH + "/user/create?return=" + returnURL;

        // Create the table header
        List<String> columns = new ArrayList<String>(4);
        columns.add(msg.list_name());
        columns.add(msg.list_username());
        columns.add(msg.list_email());
        columns.add(msg.list_roles());

        // Create the table data matrix
        List<List<String>> data = new ArrayList<List<String>>(users.size());
        List<String> actionURLs = new ArrayList<String>(users.size());
        users.forEach(user -> {
            List<String> row = new ArrayList<String>(4);
            row.add(user.name);
            row.add(user.username);
            row.add(user.email);
            String s = user.roles.stream().map(r -> r.longName).collect(Collectors.joining(", "));
            row.add(s);
            data.add(row);
            actionURLs.add(ROOT_PATH + "/user/?uuid=" + URLEncoder.encode(user.id.toString(), Charset.defaultCharset()) 
                + "&return=" + returnURL);
        });

        table = new TableAction(columns, data, tableURL, msg.list_add(), createURL, msg.list_edit(), actionURLs, six, max, total);
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

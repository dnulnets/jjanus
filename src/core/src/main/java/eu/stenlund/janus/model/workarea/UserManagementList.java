package eu.stenlund.janus.model.workarea;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.config.ConfigProvider;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import eu.stenlund.janus.model.User;
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
     * The root path, comes from configuration.
     */
    private String ROOT_PATH;

    /**
     * The list of users
     */
    public List<User> users;

    /**
     * The start index of the first user, zero based.
     */
    public int start;

        /**
     * Max number of users per page
     */ 
    public int max;

    /**
     * Number of items
     */
    public int total;

    /**
     * Calculate the number of users in the current table.
     * 
     * @return The number of users.
     */
    public int calculateCount()
    {
        return users.size();
    }

    /**
     * Calculates the one based index of the user that is the first element in the table.
     * 
     * @return The index, one-based.
     */
    public int calculateListStart()
    {
        return start + 1;
    }

    /**
     * Calculates the one based index of the user that is the last element in the table.
     * 
     * @return The index, one-based.
     */
    public int calculateListEnd()
    {
        return start + users.size();
    }

    public int calculateCurrentPage()
    {
        return start/max + 1;
    }

    /**
     * True if the table are at the first page.
     * 
     * @return True if first page.
     */
    public boolean isFirstPage()
    {
        return calculateCurrentPage() == 1;
    }

    /**
     * True if the table are at the last page.
     * 
     * @return True if last page.
     */
    public boolean isLastPage()
    {
        return calculateCurrentPage() == calculatePages();
    }

    /**
     * Calculates number of pages, one based.
     * 
     * @return The number of pages in total.
     */
    public int calculatePages()
    {
        return total/max + 1;
    }

    /**
     * Create the URL for a specific page for the list user interface.
     * 
     * @param n The page.
     * @return An URL to the page.
     */
    public String pageURL(int n)
    {
        return ROOT_PATH + "/user/list?start=" + String.valueOf((n-1) * max) + "&max="+String.valueOf(max);
    }

    /**
     * Calculates the next page URL.
     * 
     * @return The next page URL.
     */
    public String nextPageURL()
    {
        return pageURL(calculateCurrentPage()+1);
    }

    /**
     * Calculates the URL for the previous page.
     * 
     * @return The previous URL.
     */
    public String previousPageURL()
    {
        return pageURL(calculateCurrentPage()-1);
    }

    /**
     * The URL to the user edit page for this specific user.
     * 
     * @param uuid The user id.
     * @return The URL.
     */
    public String userPageURL(UUID uuid)
    {
        return ROOT_PATH + "/user?uuid=" + URLEncoder.encode(uuid.toString(), Charset.defaultCharset()) + "&return=" + 
            URLEncoder.encode(pageURL(calculateCurrentPage()), Charset.defaultCharset());
    }

    /**
     * The URL to the user create page.
     * 
     * @return The URL.
     */
    public String createPageURL()
    {
        return ROOT_PATH + "/user/create?return=" + URLEncoder.encode(pageURL(calculateCurrentPage()), Charset.defaultCharset());
    }

    /**
     * Calculates the absolute index of the user at row n in the table. The index is
     * one based.
     * 
     * @param n Row number in the table, one based.
     * @return Absolute index of the user.
     */
    public int calculateTrueIndex (int n)
    {
        return n + start;
    }
    
    /**
     * Create a new WAUMList from data.
     * 
     * @param lu List of users.
     * @param n Number of items in total, not just this page
     * @param six Start index of first user in list, zero based.
     * @param m Max number of users per page
     */
    private UserManagementList(List<User> lu, int n, int six, int m) {
        users = lu;
        start = six;
        max = m;
        total = n;
        ROOT_PATH = ConfigProvider.getConfig().
            getValue("janus.http.root-path", String.class);
    }

    /**
     * Factory function for a UserManagementList based on data in the database.
     * 
     * @param sf The mutiny session factory.
     * @param start Start index of the ist
     * @param max Max number of items to retrieve
     * @return A user management list
     */
    public static Uni<UserManagementList> createUserManagementList(SessionFactory sf, int start, int max)
    {
        return Uni.combine().all().unis(
            sf.withSession(s -> User.getListOfUsers(s, start, max)),
            sf.withSession(s -> User.getNumberOfUsers(s))).asTuple()
        .map(lu -> new UserManagementList(
                lu.getItem1(),
                lu.getItem2().intValue(),
                start,
                max));
    }    
}
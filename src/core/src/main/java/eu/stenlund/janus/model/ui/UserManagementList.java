package eu.stenlund.janus.model.ui;

import java.util.List;

import org.eclipse.microprofile.config.ConfigProvider;

import eu.stenlund.janus.model.User;

/**
 * The Workarea for UserManagement list route.
 *
 * @author Tomas Stenlund
 * @since 2022-07-21
 * 
 */
public class UserManagementList {
    
    /**
     * The root path
     */
    private String ROOT_PATH;
    
    /**
     * The list of users
     */
    public List<User> users;

    /**
     * The number of users in the list
     */
    public int count;

    /**
     * Start index of the list
     */
    public int listStart;

    /**
     * End index of the list
     */
    public int listEnd;

    /**
     * The start number of the first user
     */
    public int start;

    /**
     * Current page
     */
    public int page;

    /**
     * Max number of users per page
     */ 
    public int max;


    /**
     * Number of items
     */
    public int total;

    /**
     * Number of pages
     */
    public int pages;

    public String pageURL(int n)
    {
        return ROOT_PATH + "/user/list?start=" + String.valueOf((n-1) * max) + "&max="+String.valueOf(max);
    }

    public String nextPageURL()
    {
        return pageURL(page+1);
    }

    public String previousPageURL()
    {
        return pageURL(page-1);
    }

    public int trueIndex (int n)
    {
        return n + start;
    }
    
    /**
     * Create a new WAUMList from data.
     * 
     * @param lu List of users.
     * @param n Number of items in total
     * @param s Start index of first user in list
     * @param m Max number of users per page
     */
    public UserManagementList(List<User> lu, int n, int s, int m) {
        users = lu;
        start = s;
        max = m;
        page = start/max + 1;
        total = n;
        pages = total/max + 1;
        count = lu.size();
        listStart = start + 1;
        listEnd = start + count;
        ROOT_PATH = ConfigProvider.getConfig().getValue("janus.http.root-path", String.class);
    }

}

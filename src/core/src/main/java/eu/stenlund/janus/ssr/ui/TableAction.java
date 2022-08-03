package eu.stenlund.janus.ssr.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.base.JanusTemplateHelper;
import io.quarkus.qute.Qute;
import io.quarkus.qute.RawString;

/**
 * A pagination, that can be used for tables and other stuff.
 *
 * @author Tomas Stenlund
 * @since 2022-08-02
 * 
 */
public class TableAction {

    private static final Logger log = Logger.getLogger(TableAction.class);

    /**
     * Start index of the tables first row.
     */
    public int six;

    /**
     * End index of the tables last row.
     */
    public int eix;

    /**
     * Current page.
     */
    public int page;

    /**
     * Total number of rows.
     */
    public int total;

    /**
     * The number of pages the table consists of.
     */
    public int pages;

    /**
     * Max number of rows for the table on the page.
     */
    public int max;

    /**
     * The base URL for sleecting a new page.
     */
    public String tableURL;
    public String nextPageURL;
    public String previousPageURL;

    public List<String> columns;
    public List<List<String>> data;
    public List<Button> actionButtons;
    public Button createButton;

    /**
     * Calculates the absolute index of the user at row n in the table. The index is
     * one based.
     * 
     * @param n Row number in the table, one based.
     * @return Absolute index of the user.
     */
    public int trueIndex (int n)
    {
        return n + six;
    }

    /**
     * Create the URL for a specific page for the list user interface given the page number.
     * 
     * @param n The page.
     * @return An URL to the page.
     */
    public String pageURL(int page)
    {
        return tableURL + "?six=" + String.valueOf((page-1)*max) + "&max="+String.valueOf(max);
    }

    /**
     * Create the URL for a specific page for the list user interface given the first row index.
     * 
     * @param six The index to the forst row in the table.
     * @return An URL to the page.
     */
    private String pageURLBasedOnIndex(int six)
    {
        return tableURL + "?six=" + String.valueOf(six) + "&max="+String.valueOf(max);
    }

    /**
     * @param columns The labels for each column in the table.
     * @param data The actual data in the table, must contain the same number of coolumns as the labels.
     * @param tableURL The tables URL, used by pagination to <path>?six=<six>&max=<max>.
     * @param createLabel The label of the create button.
     * @param createURL The create URL (GET).
     * @param actionLabel The label of the action buttons for each row.
     * @param actionURLs The URL:s for the action buttons for each row (GET).
     * @param six The Start index of the table.
     * @param max Max number of items in the table.
     * @param total Total number of items in all pages.
     */
    public TableAction (List<String> columns, List<List<String>> data, String tableURL, String createLabel, String createURL, 
        String actionLabel, List<String> actionURLs, int six, int max, int total)
    {
        this.six = six;
        this.max = max;
        this.total = total;
        this.tableURL = tableURL;

        // Calculated values
        this.eix = six + data.size();
        this.pages = (total-1) / max + 1;
        this.page = six/max + 1;
        this.nextPageURL = pageURLBasedOnIndex(six+max);
        this.previousPageURL = pageURLBasedOnIndex(six-max);

        // Check incoming data
        if (data.size() <= 0)
            throw new IllegalArgumentException("Data matrix do not contain any data");

        int n = data.get(0).size();
        data.forEach(row -> {if (row.size() != n) throw new IllegalArgumentException("Data matrix size do not match number of columns");});

        if (columns.size() != n || data.size() > max || data.size() != actionURLs.size()) {
            throw new IllegalArgumentException("Data matrix size do not match columns and rows with header and actions");
        }

        // Set up the widget
        this.columns = columns;
        this.data = data;

        // Create the buttons
        this.actionButtons = new ArrayList<Button>(data.size());
        actionURLs.forEach(url -> this.actionButtons.add (new Button(actionLabel, url, null)));
        createButton = new Button(createLabel, createURL, null);
    }
}

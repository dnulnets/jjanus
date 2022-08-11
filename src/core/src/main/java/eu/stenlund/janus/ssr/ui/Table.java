package eu.stenlund.janus.ssr.ui;

import java.util.List;

import org.jboss.logging.Logger;

/**
 * A pagination, that can be used for tables and other stuff.
 *
 * @author Tomas Stenlund
 * @since 2022-08-02
 * 
 */
public class Table extends Base {

    private static final Logger log = Logger.getLogger(Table.class);

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
    private String tableURL;
    public String nextPageURL;
    public String previousPageURL;

    public List<String> columns;
    public List<List<Base>> data;
    public Button createButton;

    @Override
    public String type()
    {
        return "table";
    }

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
     * @param headers The labels for each column in the table.
     * @param data The actual gui components data model for each cell in the table, must contain the same number of columns as the header.
     * @param tableURL The tables URL, used by pagination to <path>?six=<six>&max=<max>.
     * @param createLabel The label of the create button.
     * @param createURL The create URL (GET).
     * @param six The Start index of the table.
     * @param max Max number of items in the table.
     * @param total Total number of items in all pages.
     */
    public Table (List<String> headers, List<List<Base>> data, String tableURL, String createLabel, String createURL, int six, int max, int total)
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
        if (data.size() > 0) {
            int n = data.get(0).size();
            data.forEach(row -> {if (row.size() != n) throw new IllegalArgumentException("Data matrix do not have the same number of columns for every row");});

            if (headers.size() != n) {
                throw new IllegalArgumentException("Data matrix number columns do not match number of columns in the headers");
            }

            if (data.size() > max) {
                throw new IllegalArgumentException("Data matrix number of rows exceeds max number");
            }
        }

        // Set up the widget
        this.columns = headers;
        this.data = data;

        // Create the buttons
        createButton = new Button(createLabel, createURL, null);
    }
}

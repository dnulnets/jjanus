package eu.stenlund.janus.ssr.ui;

/**
 * A HTML URL link.
 *
 * @author Tomas Stenlund
 * @since 2022-08-30
 * 
 */
public class Link extends Base {

    public String text;
    public String url;
    public String extra;
    
    @Override
    public String type()
    {
        return "link";
    }

    /**
     * Creates the Text component, it can also contains raw text, i.e it will be rednder unquoted
     * and is useful for programatically emitting HTML.
     * 
     * @param text The text string.
     * @param pure If the text string is pure, i.e. no quoting.
     */
    public Link (String text, String extra, String url)
    {
        this.text = text;
        this.url = url;
        this.extra = extra;
    }
}

package eu.stenlund.janus.ssr.ui;

/**
 * A Text in the user interface, it contains both datamodel and view.
 *
 * @author Tomas Stenlund
 * @since 2022-08-01
 * 
 */
public class Text extends Base {

    public String text;
    public boolean pure;

    @Override
    public String type()
    {
        return "text";
    }

    /**
     * Creates the Text component, it can also contains raw text, i.e it will be rednder unquoted
     * and is useful for programatically emitting HTML.
     * 
     * @param text The text string.
     * @param pure If the text string is pure, i.e. no quoting.
     */
    public Text (String text, boolean pure)
    {
        this.text = text;
        this.pure = pure;
    }

    /**
     * Creates the Text component.
     * 
     * @param text The text string.
     */
    public Text (String text)
    {
        this.text = text;
        this.pure = false;
    }

}

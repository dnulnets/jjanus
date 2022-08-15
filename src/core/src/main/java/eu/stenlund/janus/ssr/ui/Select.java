package eu.stenlund.janus.ssr.ui;

import java.util.List;

import org.jboss.logging.Logger;

/**
 * A button in the user interface, it contains both datamodel and view.
 *
 * @author Tomas Stenlund
 * @since 2022-08-11
 * 
 */
public class Select extends Base {

    private static final Logger log = Logger.getLogger(Checkbox.class);

    public static class Item {
        public boolean checked;
        public String label;
        public String value;

        public Item(String label, boolean checked, String value)
        {
            this.label = label;
            this.checked = checked;
            this.value = value;
        }
    }

    public String label;
    public String name;
    public String id;
    public String extra;
    public List<Item> items;

    @Override
    public String type()
    {
        return "checkbox";
    }

    /**
     * Creates a button for submit in formas with label and action.
     * 
     * @param label The label of the button
     * @param action The action when the button is pressed, if null the forms default action is used.
     * @param extra Any extra attributes to add for the input tag.
     */
    public Select(String label, String name, String id, List<Item> values, String extra) {
        this.label = label;
        this.name = name;
        this.id = id;
        this.items = values;
        this.extra = extra==null?"":extra;
    }

}

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
public class MultiSelect extends Base {

    private static final Logger log = Logger.getLogger(Checkbox.class);

    public class Item {
        public boolean checked;
        public Text label;
        public String value;
    }


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
    public MultiSelect(String name, String id, List<Item> values, String extra) {
        this.name = name;
        this.id = id;
        this.items = values;
        this.extra = extra==null?"":extra;

    }

}

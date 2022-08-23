package eu.stenlund.janus.ssr.ui;

import java.util.List;

import org.jboss.logging.Logger;

/**
 * A select interface, it contains the datamodel.
 *
 * @author Tomas Stenlund
 * @since 2022-08-11
 * 
 */
public class Select extends Base {

    private static final Logger log = Logger.getLogger(Checkbox.class);

    /**
     * An item that can be selected.
     */
    public static class Item {

        /**
         * The item is selected/checked.
         */
        public boolean checked;

        /**
         * The label for the item.
         */
        public String label;

        /**
         * The value of the option
         */
        public String value;

        /**
         * The default constructor for the item.
         * 
         * @param label The label of the item.
         * @param checked If the item is selected/checked or not.
         * @param value The value of the item.
         */
        public Item(String label, boolean checked, String value)
        {
            this.label = label;
            this.checked = checked;
            this.value = value;
        }
    }

    /**
     * The label of the selector.
     */
    public String label;

    /**
     * The name of the selector. Used when posting the selections.
     */
    public String name;

    /**
     * The identity of the selector.
     */
    public String id;

    /**
     * If the select is required to have a value.
     */
    public boolean required;
    
    /**
     * Any extra attributes for the selector.
     */
    public String extra;

    /**
     * The list of all items in the selector.
     */
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
    public Select(String label, String name, String id, List<Item> values, boolean required, String extra) {
        this.label = label;
        this.name = name;
        this.id = id;
        this.items = values;
        this.extra = extra==null?"":extra;
        this.required = required;
    }

}

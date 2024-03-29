package eu.stenlund.janus.ssr.ui;

import org.jboss.logging.Logger;

/**
 * A button in the user interface, it contains both datamodel and view.
 *
 * @author Tomas Stenlund
 * @since 2022-08-01
 * 
 */
public class Checkbox extends Base {

    private static final Logger log = Logger.getLogger(Checkbox.class);

    public String label;
    public String name;
    public String id;
    public boolean checked;
    public String value;
    public String extra;

    @Override
    public String type()
    {
        return "checkbox";
    }

    /**
     * Creates a button for submit in formas with label and action.
     * 
     * @param label The label of the button.
     * @param name The name of the checkbox.
     * @param id The identity of the checkbox.
     * @param value The value of the checkbox.
     * @param checked If the checkbox is ticked or not.
     * @param action The action when the button is pressed, if null the forms default action is used.
     * @param extra Any extra attributes to add for the input tag.
     */
    public Checkbox(String label, String name, String id, String value, boolean checked, String extra) {
        this.label = label;
        this.name = name;
        this.id = id;
        this.value = value;
        this.checked = checked;
        this.extra = extra==null?"":extra;
    }

}

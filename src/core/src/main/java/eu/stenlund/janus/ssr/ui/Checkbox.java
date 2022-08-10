package eu.stenlund.janus.ssr.ui;

import java.util.Map;

import org.jboss.logging.Logger;

import eu.stenlund.janus.base.JanusHelper;
import io.quarkus.qute.Qute;
import io.quarkus.qute.RawString;

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
    public RawString name;
    public RawString id;
    public boolean checked;
    public RawString value;
    public RawString extra;

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
    public Checkbox(String label, String name, String id, String value, boolean checked, String extra) {
        this.label = label;
        this.name = new RawString(name);
        this.id = new RawString(id);
        this.value = new RawString(value==null?"":value);
        this.checked = checked;
        this.extra = new RawString(extra==null?"":value);
    }

}

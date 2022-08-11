package eu.stenlund.janus.ssr.ui;

import org.jboss.logging.Logger;

/**
 * A button in the user interface, it contains both datamodel and view.
 *
 * @author Tomas Stenlund
 * @since 2022-08-01
 * 
 */
public class Button extends Base {

    private static final Logger log = Logger.getLogger(Button.class);

    public String label;
    public String action;
    public String extra;

    @Override
    public String type()
    {
        return "button";
    }

    /**
     * Creates a button for submit in formas with label and action.
     * 
     * @param label The label of the button
     * @param action The action when the button is pressed, if null the forms default action is used.
     * @param extra Any extra attributes to add for the input tag, can be null.
     */
    public Button(String label, String action, String extra) {
        this.label = label;
        this.action = action;
        this.extra = extra==null?"":extra;
    }

    /**
     * Creates a button for submit in a form with a label.
     * 
     * @param label The label for the button.
     * @param action The action URL for the button.
     * @param extra Any extra attributes to add for the input tag, can be null.
     */
    public Button(String label, String extra) {
        this.label = label;
        this.action = null;
        this.extra = extra==null?"":extra;
    }

}

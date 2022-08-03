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
public class Button {

    private static final Logger log = Logger.getLogger(Button.class);

    public String label;
    public String action;
    public String extra;

    /**
     * Creates a button for submit in formas with label and action.
     * 
     * @param label The label of the button
     * @param action The action when the button is pressed, if null the forms default action is used.
     * @param extra Any extra attributes to add for the input tag.
     */
    public Button(String label, String action, String extra) {
        this.label = label;
        this.action = action;
        this.extra = extra;
    }

    /**
     * Creates a button for submit in a form with a label.
     * 
     * @param label The label for the button.
     * @param action The action URL for the button.
     * @param extra Any extra attributes to add for the input tag.
     */
    public Button(String label, String extra) {
        this.label = label;
        this.action = null;
        this.extra = extra;
    }

}

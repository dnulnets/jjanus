package eu.stenlund.janus.model.ui;

/**
 * A button in the user interface, it contains both datamodel and view.
 *
 * @author Tomas Stenlund
 * @since 2022-08-01
 * 
 */
public class TextInput {

    public String label;
    public String name;
    public String id;
    public String value;
    public String feedback;
    public String extra;

    public TextInput (String label, String name, String id, String value, String feedback, String extra)
    {
        this.label = label;
        this.name = name;
        this.id = id;
        this.value = value;
        this.feedback = feedback;
        this.extra = extra;
    }

}

package eu.stenlund.janus.ssr.ui;

import io.quarkus.qute.RawString;

/**
 * A button in the user interface, it contains both datamodel and view.
 *
 * @author Tomas Stenlund
 * @since 2022-08-01
 * 
 */
public class TextInput extends Base {

    public String label;
    public RawString name;
    public RawString id;
    public RawString value;
    public String feedback;
    public RawString extra;

    @Override
    public String type()
    {
        return "textinput";
    }

    public TextInput (String label, String name, String id, String value, String feedback, String extra)
    {
        this.label = label;
        this.name = new RawString(name);
        this.id = new RawString(id);
        this.value = new RawString(value==null?"":value);
        this.feedback = feedback;
        this.extra = new RawString(extra==null?"":extra);
    }

}

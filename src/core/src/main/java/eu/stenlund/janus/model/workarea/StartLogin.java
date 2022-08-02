package eu.stenlund.janus.model.workarea;

import eu.stenlund.janus.base.JanusTemplateHelper;
import eu.stenlund.janus.model.ui.Button;
import eu.stenlund.janus.model.ui.TextInput;
import eu.stenlund.janus.msg.Messages;

/**
 * The Workarea for the login page.
 *
 * @author Tomas Stenlund
 * @since 2022-08-01
 * 
 */
public class StartLogin {
    
    /**
     * The login button model
     */
    public Button login;

    /**
     * The username text field model for the login screen.
     */
    public TextInput username;

    /**
     * The password text field model for the login screen
     */
    public TextInput password;

    /**
     * Constructor for the login page.
     * 
     * @param locale The locale of the page.
     */
    public StartLogin (String locale)
    {
        Messages msg = JanusTemplateHelper.getMessageBundle(Messages.class, locale);
        login = new Button(msg.login_login(),null);
        username = new TextInput(msg.login_username(), "j_username", "id-username", null, msg.login_username_required(), "required");
        password = new TextInput(msg.login_password(), "j_password", "id-password", null, msg.login_password_required(), "required");
    }
}

package eu.stenlund.janus.ssr.model;

import org.eclipse.microprofile.config.ConfigProvider;

import eu.stenlund.janus.msg.Messages;
import eu.stenlund.janus.ssr.JanusSSRHelper;
import eu.stenlund.janus.ssr.JanusTemplateHelper;
import eu.stenlund.janus.ssr.ui.Button;
import eu.stenlund.janus.ssr.ui.Form;
import eu.stenlund.janus.ssr.ui.TextInput;

/**
 * The Workarea for the login page.
 *
 * @author Tomas Stenlund
 * @since 2022-08-01
 * 
 */
public class StartLogin {
    
    /**
     * The form.
     */
    public Form form;

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
        String ROOT_PATH = ConfigProvider.getConfig().getValue("janus.http.root-path", String.class);
        Messages msg = JanusTemplateHelper.getMessageBundle(Messages.class, locale);
        login = new Button(msg.login_login(),null);
        username = new TextInput(msg.login_username(), "j_username", "id-username", null, msg.login_username_required(), JanusSSRHelper.required());
        password = new TextInput(msg.login_password(), "j_password", "id-password", null, msg.login_password_required(), JanusSSRHelper.required());
        form = new Form(Form.POST, ROOT_PATH + "/j_security_check", true);
    }
}

package eu.stenlund.janus.msg;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;

/**
 * The default Message bundle, uses English.
 *
 * @author Tomas Stenlund
 * @since 2022-07-11
 * 
 */
@MessageBundle("usermsg")
public interface UserManagement {

    @Message("User administration")
    String list_administration();

    @Message("Name")
    String list_name();

    @Message("Username")
    String list_username();

    @Message("Email")
    String list_email();

    @Message("Roles")
    String list_roles();

    @Message("Add user")
    String list_add();

    @Message("Edit")
    String list_edit();

    
    @Message("User management")
    String user_administration();
    
    @Message("The user must have a name!")
    String user_must_have_name();
    
    @Message("Name")
    String user_name();
    
    @Message("Username")
    String user_username();
    
    @Message("The user must have a username!")
    String user_must_have_username();
    
    @Message("Email")
    String user_email();
    
    @Message("The user must have an email address!")
    String user_must_have_email();
    
    @Message("Password")
    String user_password();
    
    @Message("A new user must have a password!")
    String user_must_have_password();
    
    @Message("Roles")
    String user_roles();
    
    @Message("Save")
    String user_save();
    
    @Message("Delete")
    String user_delete();
    
    @Message("Cancel")
    String user_cancel();

}
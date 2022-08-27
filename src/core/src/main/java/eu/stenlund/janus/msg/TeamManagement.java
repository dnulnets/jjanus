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
@MessageBundle("teammsg")
public interface TeamManagement {

    @Message ("Team administration")
    String list_administration();

    @Message("Teamname")
    String list_name();

    @Message("Number of users")
    String list_number_of_users();

    @Message("Users")
    String list_users();

    @Message("Edit")
    String list_edit();

    @Message("Action")
    String list_action();

    @Message("Add team")
    String list_add();

    @Message("Save")
    String team_save();
    
    @Message("Delete")
    String team_delete();
    
    @Message("Cancel")
    String team_cancel();

    @Message("A team must have a name")
    String team_must_have_name();

    @Message("Name")
    String team_name();

    @Message ("Team administration")
    String team_administration();

    @Message("Products")
    String team_products();

    @Message("Members")
    String team_members();

    @Message("Products")
    String list_products();
}
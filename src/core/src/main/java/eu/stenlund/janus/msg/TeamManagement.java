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

    @Message("Add team")
    String list_add();

}
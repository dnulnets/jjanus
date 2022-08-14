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
@MessageBundle("productmsg")
public interface ProductManagement {

    @Message ("Product administration")
    String list_administration();

    @Message("Name")
    String list_name();

    @Message("Version")
    String list_current_version();

    @Message("Description")
    String list_description();

    @Message("Action")
    String list_action();

    @Message("Edit")
    String list_edit();

    @Message("Create")
    String list_add();

}
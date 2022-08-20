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
@MessageBundle("productversionmsg")
public interface ProductVersionManagement {

    @Message ("Product version administration")
    String list_administration();

    @Message("Product")
    String list_product();

    @Message("Version")
    String list_version();

    @Message("State")
    String list_state();

    @Message("Action")
    String list_action();

    @Message("Edit")
    String list_edit();

    @Message("Create")
    String list_add();

    @Message("Delete")
    String productversion_delete();

    @Message("Save")
    String productversion_save();

    @Message("Cancel")
    String productversion_cancel();

    @Message("Version")
    String productversion_version();

    @Message("A product version must have a version")
    String productversion_must_have_version();

    @Message("Closed version")
    String productversion_closed();

    @Message("Product")
    String productversion_product();

    @Message("State")
    String productversion_state();

}
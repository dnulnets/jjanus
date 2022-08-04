package eu.stenlund.janus.msg;

import io.quarkus.qute.i18n.Localized;
import io.quarkus.qute.i18n.Message;

/**
 * The default Message bundle, uses English.
 *
 * @author Tomas Stenlund
 * @since 2022-07-11
 * 
 */
@Localized("sv")
public interface TeamManagement_sv extends TeamManagement {

    @Message ("Teamadministrering")
    String list_administration();

    @Message("Team")
    String list_name();

    @Message("Antal deltagare")
    String list_number_of_users();

    @Message("Deltagare")
    String list_users();

    @Message("Ändra")
    String list_edit();

    @Message("Skapa team")
    String list_add();

}
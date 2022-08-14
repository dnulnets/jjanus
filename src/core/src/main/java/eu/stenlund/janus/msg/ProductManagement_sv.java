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
public interface ProductManagement_sv extends ProductManagement {

    @Message ("Produktdministrering")
    String list_administration();

    @Message("Namn")
    String list_name();

    @Message("Version")
    String list_current_version();

    @Message("Beskrivning")
    String list_description();

    @Message("Aktivitet")
    String list_action();

    @Message("Ändra")
    String list_edit();

    @Message("Skapa")
    String list_add();
}

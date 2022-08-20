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
public interface ProductVersionManagement_sv extends ProductVersionManagement {

    @Message ("Produktversionsdministrering")
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

    @Message("Ta bort")
    String productversion_delete();

    @Message("Spara")
    String productversion_save();

    @Message("Avbryt")
    String productversion_cancel();

    @Message("Version")
    String productversion_version();

    @Message("En produktversion måste ha en version")
    String productversion_must_have_version();

    @Message("Stängd version")
    String productversion_closed();

    @Message("Produkt")
    String productversion_product();

    @Message("Tillstånd")
    String productversion_state();

}

package eu.stenlund.janus.msg;

import io.quarkus.qute.i18n.Localized;
import io.quarkus.qute.i18n.Message;

/**
 * Message bundle for Svenska.
 *
 * @author Tomas Stenlund
 * @since 2022-07-11
 * 
 */
@Localized("sv-SE")
public interface Messages_sv extends Messages {

    @Message("sv")
    String language();

    @Message("Ange ditt användarnamn")
    String login_enter_username();

    @Message("Ange ditt lösenord")
    String login_enter_password();

    @Message("Användarnamn")
    String login_username();

    @Message("Lösenord")
    String login_password();

    @Message("Kunde ej verifiera din behörighet")
    String login_fail_header();

    @Message("Du har inte angett ett giltigt användarnamn eller lösenord.")
    String login_fail_message();

    @Message("Try again")
    String login_fail_back();

    @Message("Land")
    String country();

    @Message("Sverige")
    String country_sweden();

    @Message("USA")
    String country_usa();

    @Message("Storbritannien")
    String country_united_kingdom();

    @Message("Administration")
    String menu_administration();

    @Message("Användare")
    String menu_administration_users();  

    @Message("Produkter")
    String menu_administration_products();

    @Message("Produktversioner")
    String menu_administration_productversions();

    @Message("Produktägare")
    String menu_product();

    @Message("Produkter")
    String menu_product_products();

    @Message("Produktversioner")
    String menu_product_productversions();

    @Message("Användarbehov")
    String menu_product_needs();

    @Message("Produktkrav")
    String menu_product_requirements();

    @Message("Välj produkt")
    String menu_product_select();

    @Message("Logga in")
    String login_login();    

    @Message("Logga ut")
    String menu_logout();

    @Message("Användarnamn krävs")
    String login_username_required();

    @Message("Lösenord krävs")
    String login_password_required();

    @Message("Team")
    String menu_administration_teams();

    @Message("Ett fel har inträffat")
    String error_title();

    @Message("Objektet saknas")
    String error_nosuchitem_title();

    @Message("Det finns inget objekt av typen {type} med uuid {uuid}.")
    String error_nosuchitem(String type, String uuid);

    @Message("Teknisk information:")
    String error_technical_info();

    @Message("Tillbaka")
    String error_back();

    @Message("Sökresultat")
    String search_result();

    @Message("Produkter")
    String search_products();

    @Message("Team")
    String search_teams();

    @Message("Användarbehov")
    String search_user_needs();

    @Message("Systemkrav")
    String search_requirements();

    @Message("Ärenden")
    String search_issues();

    @Message("Ingen hittad")
    String search_none_found();

    @Message("Inte implementerad ännu")
    String search_not_implemented();

}
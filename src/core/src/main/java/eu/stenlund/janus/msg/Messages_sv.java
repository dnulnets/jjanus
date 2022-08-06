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

    @Message("Logga in")
    String login_login();    


    @Message("Användarnamn krävs")
    String login_username_required();

    @Message("Lösenord krävs")
    String login_password_required();

    @Message("Team")
    String menu_administration_teams();

    @Message("Systemfel")
    String error_title();

    @Message("Tillbaka")
    String error_back();
}
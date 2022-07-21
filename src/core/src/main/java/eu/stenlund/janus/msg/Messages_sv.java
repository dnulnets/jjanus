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
@Localized("sv")
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

    // User administration
    @Message("Användaradministration")
    String user_list_administration();

    @Message("Visar {pages} användare per sida")
    String user_list_showing_max(int pages);

    @Message("Namn")
    String user_list_name();

    @Message("Användarnamn")
    String user_list_username();

    @Message("Epost")
    String user_list_email();

    @Message("Roller")
    String user_list_roles();

    @Message("Visar användare {start} till {end} av {max}")
    String user_list_showing_users(int start, int end, int max);

    @Message("Visar sida {current} av {max}")
    String user_list_showing_pages(int current, int max);    

    @Message("Föregående")
    String user_list_previous();

    @Message("Nästa")
    String user_list_next();    
}
package eu.stenlund.janus.msg;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.Localized;

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
}
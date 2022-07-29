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
public interface UserManagement_sv extends UserManagement {

    @Message("Användaradministration")
    String list_administration();

    @Message("Visar {pages} användare per sida")
    String list_showing_max(int pages);

    @Message("Namn")
    String list_name();

    @Message("Användarnamn")
    String list_username();

    @Message("Epost")
    String list_email();

    @Message("Roller")
    String list_roles();

    @Message("Visar användare {start} till {end} av {max}")
    String list_showing_users(int start, int end, int max);

    @Message("Visar sida {current} av {max}")
    String list_showing_pages(int current, int max);    

    @Message("Föregående")
    String list_previous();

    @Message("Nästa")
    String list_next();

    @Message("Ny användare")
    String list_add();

    @Message("Editera")
    String list_edit();

    @Message("Användaradministration")
    String user_administration();
    
    @Message("Användaren måste ha ett namn!")
    String user_must_have_name();
    
    @Message("Namn")
    String user_name();
    
    @Message("Användarnamn")
    String user_username();
    
    @Message("Användaren måste ha ett användarnamn!")
    String user_must_have_username();
    
    @Message("Epost")
    String user_email();
    
    @Message("Användaren måste ha en mailadress!")
    String user_must_have_email();
    
    @Message("Lösenord")
    String user_password();
    
    @Message("En ny användaren måste ha ett lösenord!")
    String user_must_have_password();
    
    @Message("Roller")
    String user_roles();
    
    @Message("Spara")
    String user_save();
    
    @Message("Ta bort")
    String user_delete();
    
    @Message("Avbryt")
    String user_cancel();

}
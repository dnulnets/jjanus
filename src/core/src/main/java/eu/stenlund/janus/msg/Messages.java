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
@MessageBundle
public interface Messages {

    @Message("en")
    String language();

    @Message("Enter your user name")
    String login_enter_username();

    @Message("Enter your password")
    String login_enter_password();

    @Message("Username")
    String login_username();

    @Message("Password")
    String login_password();

    @Message("Unable to authenticate")
    String login_fail_header();

    @Message("You have failed to supply a valid username or password.")
    String login_fail_message();

    @Message("Try again")
    String login_fail_back();

    @Message("Country")
    String country();

    @Message("Sweden")
    String country_sweden();

    @Message("USA")
    String country_usa();

    @Message("United Kingdom")
    String country_united_kingdom();

    @Message("Administration")
    String menu_administration();

    @Message("Users")
    String menu_administration_users();

    @Message("Login")
    String login_login();
    
    @Message("Username is required")
    String login_username_required();

    @Message("Password is required")
    String login_password_required();

}
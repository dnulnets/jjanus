package eu.stenlund.janus.msg;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;

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
}
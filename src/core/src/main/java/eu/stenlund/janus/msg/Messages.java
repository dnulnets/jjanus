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

    @Message("Products")
    String menu_administration_products();

    @Message("Product versions")
    String menu_administration_productversions();

    @Message("Product owner")
    String menu_product();

    @Message("Products")
    String menu_product_products();

    @Message("Product versions")
    String menu_product_productversions();

    @Message("User needs")
    String menu_product_needs();

    @Message("Product requirements")
    String menu_product_requirements();

    @Message("Select product")
    String menu_product_select();

    @Message("Logout")
    String menu_logout();

    @Message("Login")
    String login_login();
    
    @Message("Username is required")
    String login_username_required();

    @Message("Password is required")
    String login_password_required();

    @Message("Teams")
    String menu_administration_teams();

    @Message("An error has occured")
    String error_title();

    @Message("No such item")
    String error_nosuchitem_title();

    @Message("No item of type {type} with uuid {uuid} exists.")
    String error_nosuchitem(String type, String uuid);

    @Message("Technical information:")
    String error_technical_info();
    
    @Message("Return")
    String error_back();

    @Message("Search result")
    String search_result();

    @Message("Products")
    String search_products();

    @Message("Teams")
    String search_teams();

    @Message("User needs")
    String search_user_needs();

    @Message("System requirements")
    String search_requirements();

    @Message("Issues")
    String search_issues();

    @Message("None found")
    String search_none_found();

    @Message("Not implemented yet")
    String search_not_implemented();


}
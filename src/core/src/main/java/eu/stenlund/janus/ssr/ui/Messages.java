package eu.stenlund.janus.ssr.ui;

import io.quarkus.qute.i18n.Message;
import io.quarkus.qute.i18n.MessageBundle;

/**
 * The SSR Message bundle, uses English.
 *
 * @author Tomas Stenlund
 * @since 2022-07-11
 * 
 */
@MessageBundle("ssr")
public interface Messages {

    @Message("Action")
    String table_action_action();

    @Message("Showing {pages} items per page")
    String table_action_showing_max(int pages);

    @Message("Showing items {start} to {end} of {max}")
    String table_action_showing_items(int start, int end, int max);

    @Message("Showing page {current} of {max}")
    String table_action_showing_pages(int current, int max);

    @Message("Previous")
    String table_action_previous();

    @Message("Next")
    String table_action_next();

}
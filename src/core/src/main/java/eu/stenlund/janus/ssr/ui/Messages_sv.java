package eu.stenlund.janus.ssr.ui;

import io.quarkus.qute.i18n.Localized;
import io.quarkus.qute.i18n.Message;

/**
 * The SSR Message bundle, uses English.
 *
 * @author Tomas Stenlund
 * @since 2022-07-11
 * 
 */
@Localized("sv-SE")
public interface Messages_sv extends Messages {

    @Message("Aktivitet")
    String table_action_action();

    @Message("Visar {pages} objekt per sida")
    String table_action_showing_max(int pages);

    @Message("Visar objekt {start} to {end} of {max}")
    String table_action_showing_items(int start, int end, int max);

    @Message("Visar sida {current} of {max}")
    String table_action_showing_pages(int current, int max);

    @Message("Föregående")
    String table_action_previous();

    @Message("Nästa")
    String table_action_next();
}

package eu.stenlund.janus.ssr.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.jboss.logging.Logger;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.model.Product;
import eu.stenlund.janus.model.Team;
import eu.stenlund.janus.msg.Messages;
import eu.stenlund.janus.ssr.JanusTemplateHelper;
import eu.stenlund.janus.ssr.ui.Link;
import io.smallrye.mutiny.Uni;

/**
 * The Workarea for TeamManagement list route /user/list
 *
 * @author Tomas Stenlund
 * @since 2022-08-04
 * 
 */
public class SearchList {

    private static final Logger log = Logger.getLogger(SearchList.class);

    /**
     * The list of products
     */
    public List<Link> products;

    /*
     * The list of found teams.
     */
    public List<Link> teams;

    /**
     * Create a new workarea from the teams in the database.
     * 
     * @param users The teams for this page.
     * @param total Total number of teams in the database.
     * @param six The index for the first row in the table.
     * @param max The max number of users in the table.
     * @param locale The locale to render.
     */
    private SearchList(List<Product> products, List<Team> teams, String what, String locale) {

        // Get hold of the message bundle and root path
        String ROOT_PATH = JanusHelper.getConfig(String.class, "janus.http.root-path", "/");
        Messages msg = JanusTemplateHelper.getMessageBundle(Messages.class, locale);

        // Return URL
        String backURL = UriBuilder.fromPath(ROOT_PATH)
        .segment("search")
        .queryParam("what", what)
        .build().toString();

        // Create product list
        final String url = backURL;

        this.products = products.stream().map(p -> {

            String actionURL  = UriBuilder.fromPath(ROOT_PATH)
                .segment("product")
                .queryParam("uuid", p.id)
                .queryParam("return", url)
                .build().toString();

            return new Link(p.name + (p.current!=null?(" ("+p.current.version+")"):" ()"), "", actionURL);
        }).toList();

        this.teams = teams.stream().map(p -> {

            String actionURL  = UriBuilder.fromPath(ROOT_PATH)
                .segment("team")
                .queryParam("uuid", p.id)
                .queryParam("return", url)
                .build().toString();

            return new Link(p.name, "", actionURL);
        }).toList();

    }

    /**
     * Factory function for a UserManagementList based on data in the database.
     * 
     * @param sf The mutiny session factory.
     * @param start Start index of the ist
     * @param max Max number of items to retrieve
     * @return A user management list
     */
    public static Uni<SearchList> createModel(SessionFactory sf, String what, String locale)
    {
        return Uni.combine().all().unis(
            sf.withSession(s -> Product.search(s, what)),
            sf.withSession(s -> Team.search(s, what))).
        combinedWith ((products, teams) -> {
            return new SearchList(products, teams, what, locale);
        });

    }  

}

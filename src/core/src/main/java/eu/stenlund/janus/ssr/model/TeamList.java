package eu.stenlund.janus.ssr.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.model.Team;
import eu.stenlund.janus.msg.TeamManagement;
import eu.stenlund.janus.ssr.JanusSSRHelper;
import eu.stenlund.janus.ssr.JanusTemplateHelper;
import eu.stenlund.janus.ssr.ui.Base;
import eu.stenlund.janus.ssr.ui.Button;
import eu.stenlund.janus.ssr.ui.Table;
import eu.stenlund.janus.ssr.ui.Text;
import io.smallrye.mutiny.Uni;

/**
 * The Workarea for TeamManagement list route /user/list
 *
 * @author Tomas Stenlund
 * @since 2022-08-04
 * 
 */
public class TeamList {

    /**
     * The team table
     */
    public Table table;
 
    /**
     * Create a new workarea from the teams in the database.
     * 
     * @param users The teams for this page.
     * @param total Total number of teams in the database.
     * @param six The index for the first row in the table.
     * @param max The max number of users in the table.
     * @param locale The locale to render.
     */
    private TeamList(List<Team> teams, int total, int six, int max, String locale) {

        // Get hold of the message bundle and root path
        String ROOT_PATH = JanusHelper.getConfig(String.class, "janus.http.root-path", "/");
        TeamManagement msg = JanusTemplateHelper.getMessageBundle(TeamManagement.class, locale);

        // Create action URL:s
        String returnURL = UriBuilder.fromPath(ROOT_PATH)
        .segment("team")
        .segment("list")
        .queryParam("six", six)
        .queryParam("max", max)
        .build().toString();

        String tableURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("team")
            .segment("list")
            .build().toString();

        String createURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("team")
            .segment("create")
            .queryParam("return", returnURL)
            .build().toString();

        
        // Create the table header
        List<String> columns = new ArrayList<String>(4);
        columns.add(msg.list_name());
        columns.add(msg.list_products());
        columns.add(msg.list_number_of_users());
        columns.add(msg.list_users());
        columns.add(msg.list_action());

        // Create the table data matrix
        List<List<Base>> data = new ArrayList<List<Base>>(teams.size());
        teams.forEach(team -> {
            List<Base> row = new ArrayList<Base>(columns.size());
            row.add(new Text(team.name));
            String s = team.products.stream().map(u -> u.name).collect(Collectors.joining("<br/>"));
            row.add(new Text(s,true));
            row.add(new Text(String.valueOf(team.members.size())));
            s = team.members.stream().map(u -> u.name + " (" + u.username + ")").collect(Collectors.joining("<br/>"));
            row.add(new Text(s,true));
            String actionURL  = UriBuilder.fromPath(ROOT_PATH)
                .segment("team")
                .queryParam("uuid", team.id)
                .queryParam("return", returnURL)
                .build().toString();
            row.add(new Button(msg.list_edit(), actionURL, JanusSSRHelper.unpolyFollow()));
            data.add(row);
        });

        table = new Table(columns, data, tableURL, msg.list_add(), createURL, six, max, total);
    }

    /**
     * Factory function for a UserManagementList based on data in the database.
     * 
     * @param sf The mutiny session factory.
     * @param start Start index of the ist
     * @param max Max number of items to retrieve
     * @return A user management list
     */
    public static Uni<TeamList> createModel(SessionFactory sf, int start, int max, String locale)
    {
        return Uni.combine().all().unis(
            sf.withSession(s -> Team.getList(s, start, max)),
            sf.withSession(s -> Team.getCount(s))).
        combinedWith((list, count)-> new TeamList(
            list,
            count.intValue(),
            start,
            max,
            locale));
    }  

}

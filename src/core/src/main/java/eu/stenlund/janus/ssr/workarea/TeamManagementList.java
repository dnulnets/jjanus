package eu.stenlund.janus.ssr.workarea;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.ConfigProvider;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import eu.stenlund.janus.base.JanusTemplateHelper;
import eu.stenlund.janus.model.Team;
import eu.stenlund.janus.model.User;
import eu.stenlund.janus.msg.TeamManagement;
import eu.stenlund.janus.msg.UserManagement;
import eu.stenlund.janus.ssr.ui.TableAction;
import io.smallrye.mutiny.Uni;

/**
 * The Workarea for TeamManagement list route /user/list
 *
 * @author Tomas Stenlund
 * @since 2022-08-04
 * 
 */
public class TeamManagementList {

    /**
     * The user table
     */
    public TableAction table;
 
    /**
     * Create a new workarea from the teams in the database.
     * 
     * @param users The teams for this page.
     * @param total Total number of teams in the database.
     * @param six The index for the first row in the table.
     * @param max The max number of users in the table.
     * @param locale The locale to render.
     */
    private TeamManagementList(List<Team> teams, int total, int six, int max, String locale) {

        // Get hold of the message bundle and root path
        String ROOT_PATH = ConfigProvider.getConfig().getValue("janus.http.root-path", String.class);
        TeamManagement msg = JanusTemplateHelper.getMessageBundle(TeamManagement.class, locale);
        String returnURL = URLEncoder.encode(ROOT_PATH + "/team/list?six=" + String.valueOf(six) + "&max="+String.valueOf(max),
            Charset.defaultCharset());
        String tableURL = ROOT_PATH + "/team/list";
        String createURL = ROOT_PATH + "/team/create?return=" + returnURL;

        // Create the table header
        List<String> columns = new ArrayList<String>(4);
        columns.add(msg.list_name());
        columns.add(msg.list_number_of_users());
        columns.add(msg.list_users());

        // Create the table data matrix
        List<List<String>> data = new ArrayList<List<String>>(teams.size());
        List<String> actionURLs = new ArrayList<String>(teams.size());
        teams.forEach(team -> {
            List<String> row = new ArrayList<String>(4);
            row.add(team.name);
            row.add("0");
            String s = team.members.stream().map(u -> u.name).collect(Collectors.joining(", "));
            row.add(s);
            data.add(row);
            actionURLs.add(ROOT_PATH + "/team/?uuid=" + URLEncoder.encode(team.id.toString(), Charset.defaultCharset()) 
                + "&return=" + returnURL);
        });

        table = new TableAction(columns, data, tableURL, msg.list_add(), createURL, msg.list_edit(), actionURLs, six, max, total);
    }

    /**
     * Factory function for a UserManagementList based on data in the database.
     * 
     * @param sf The mutiny session factory.
     * @param start Start index of the ist
     * @param max Max number of items to retrieve
     * @return A user management list
     */
    public static Uni<TeamManagementList> createModel(SessionFactory sf, int start, int max, String locale)
    {
        return Uni.combine().all().unis(
            sf.withSession(s -> Team.getListOfTeams(s, start, max)),
            sf.withSession(s -> Team.getNumberOfTeams(s))).asTuple()
        .map(lu -> new TeamManagementList(
                lu.getItem1(),
                lu.getItem2().intValue(),
                start,
                max,
                locale));
    }  

}

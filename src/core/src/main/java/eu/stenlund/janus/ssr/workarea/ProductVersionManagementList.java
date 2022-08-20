package eu.stenlund.janus.ssr.workarea;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.model.ProductVersion;
import eu.stenlund.janus.msg.ProductVersionManagement;
import eu.stenlund.janus.ssr.JanusSSRHelper;
import eu.stenlund.janus.ssr.JanusTemplateHelper;
import eu.stenlund.janus.ssr.ui.Base;
import eu.stenlund.janus.ssr.ui.Button;
import eu.stenlund.janus.ssr.ui.Table;
import eu.stenlund.janus.ssr.ui.Text;
import io.smallrye.mutiny.Uni;

/**
 * The Workarea for ProductManagement list route /user/list
 *
 * @author Tomas Stenlund
 * @since 2022-08-04
 * 
 */
public class ProductVersionManagementList {

    /**
     * The team table
     */
    public Table table;
 
    private ProductVersionManagementList(List<ProductVersion> versions, int total, int six, int max, String locale) {

        // Get hold of the message bundle and root path
        String ROOT_PATH = JanusHelper.getConfig(String.class, "janus.http.root-path", "/");
        ProductVersionManagement msg = JanusTemplateHelper.getMessageBundle(ProductVersionManagement.class, locale);

        // Create action URL:s
        String returnURL = UriBuilder.fromPath(ROOT_PATH)
        .segment("productversion")
        .segment("list")
        .queryParam("six", six)
        .queryParam("max", max)
        .build().toString();

        String tableURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("productversion")
            .segment("list")
            .build().toString();

        String createURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("productversion")
            .segment("create")
            .queryParam("return", returnURL)
            .build().toString();

        
        // Create the table header
        List<String> columns = new ArrayList<String>(4);
        columns.add(msg.list_product());
        columns.add(msg.list_version());
        columns.add(msg.list_state());
        columns.add(msg.list_action());

        // Create the table data matrix
        List<List<Base>> data = new ArrayList<List<Base>>(versions.size());
        versions.forEach(pv -> {
            List<Base> row = new ArrayList<Base>(columns.size());
            row.add(new Text(pv.product!=null?pv.product.name:""));
            row.add(new Text(pv.version));
            row.add(new Text(pv.state!=null?pv.state.display:""));
            String actionURL  = UriBuilder.fromPath(ROOT_PATH)
                .segment("productversion")
                .queryParam("uuid", pv.id)
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
    public static Uni<ProductVersionManagementList> createModel(SessionFactory sf, int start, int max, String locale)
    {
        return Uni.combine().all().unis(
            sf.withSession(s -> ProductVersion.getList(s, start, max)),
            sf.withSession(s -> ProductVersion.getCount(s))).
        combinedWith((list, count)-> new ProductVersionManagementList(
            list,
            count.intValue(),
            start,
            max,
            locale));
    }  

}

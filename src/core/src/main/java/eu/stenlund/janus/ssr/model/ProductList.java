package eu.stenlund.janus.ssr.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import eu.stenlund.janus.base.JanusHelper;
import eu.stenlund.janus.model.Product;
import eu.stenlund.janus.msg.ProductManagement;
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
public class ProductList {

    /**
     * The team table
     */
    public Table table;
 
    /**
     * Create a new workarea from the products in the database.
     * 
     * @param users The teams for this page.
     * @param total Total number of teams in the database.
     * @param six The index for the first row in the table.
     * @param max The max number of users in the table.
     * @param locale The locale to render.
     */
    private ProductList(List<Product> products, int total, int six, int max, String locale) {

        // Get hold of the message bundle and root path
        String ROOT_PATH = JanusHelper.getConfig(String.class, "janus.http.root-path", "/");
        ProductManagement msg = JanusTemplateHelper.getMessageBundle(ProductManagement.class, locale);

        // Create action URL:s
        String returnURL = UriBuilder.fromPath(ROOT_PATH)
        .segment("product")
        .segment("list")
        .queryParam("six", six)
        .queryParam("max", max)
        .build().toString();

        String tableURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("product")
            .segment("list")
            .build().toString();

        String createURL = UriBuilder.fromPath(ROOT_PATH)
            .segment("product")
            .segment("create")
            .queryParam("return", returnURL)
            .build().toString();

        
        // Create the table header
        List<String> columns = new ArrayList<String>(4);
        columns.add(msg.list_name());
        columns.add(msg.list_current_version());
        columns.add(msg.list_description());
        columns.add(msg.list_action());

        // Create the table data matrix
        List<List<Base>> data = new ArrayList<List<Base>>(products.size());
        products.forEach(product -> {
            List<Base> row = new ArrayList<Base>(columns.size());
            row.add(new Text(product.name));
            String s = "No state";
            if (product.current != null) {
                s = product.current.state!=null?product.current.state.display:"No state";
            }
            row.add(new Text(product.current!=null?product.current.version + " (" + s + ")":"No version"));
            row.add(new Text(product.description));
            String actionURL  = UriBuilder.fromPath(ROOT_PATH)
                .segment("product")
                .queryParam("uuid", product.id)
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
    public static Uni<ProductList> createModel(SessionFactory sf, int start, int max, String locale)
    {
        return Uni.combine().all().unis(
            sf.withSession(s -> Product.getList(s, start, max)),
            sf.withSession(s -> Product.getCount(s))).
        combinedWith((list, count)-> new ProductList(list, count.intValue(), start, max, locale));
    }

}

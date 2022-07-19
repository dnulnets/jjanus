package eu.stenlund.janus.model.base;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;

import io.quarkus.runtime.StartupEvent;

/**
 * The flyway migration of the database. It is started "manually" by the
 * application (via a StartupEvent) because it cannot use reactive connections
 * as of now.
 *
 * @author Tomas Stenlund
 * @since 2022-07-11
 * 
 */
@ApplicationScoped
public class JanusFlyway {

    @ConfigProperty(name = "janus.flyway.migrate")
    boolean runMigration;
    @ConfigProperty(name = "janus.flyway.schema")
    String schema;
    @ConfigProperty(name = "janus.flyway.baseline-version")
    String version;

    @ConfigProperty(name = "quarkus.datasource.reactive.url")
    String datasourceUrl;
    @ConfigProperty(name = "quarkus.datasource.username")
    String datasourceUsername;
    @ConfigProperty(name = "quarkus.datasource.password")
    String datasourcePassword;

    /**
     * Runs the flyway migration if it is activated by the janus.flyway.migrate
     * property.
     * 
     * @param event The genric Quarkus startup event that initiates the migration.
     */
    public void runFlywayMigration(@Observes StartupEvent event) {
        if (runMigration) {
            Flyway flyway = Flyway.configure()
                    .dataSource("jdbc:" + datasourceUrl, datasourceUsername, datasourcePassword)
                    .schemas(schema)
                    .baselineVersion(version).load();
            flyway.baseline();
            flyway.migrate();
        }
    }
}

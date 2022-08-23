package eu.stenlund.janus.model.base;

import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.reactive.mutiny.Mutiny.Session;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;

/**
 * The JanusEntity that acts as base class for all entities, it supports with
 * unique keys for the identity of the entity using UUID.
 *
 * @author Tomas Stenlund
 * @since 2022-07-11
 * 
 */
@MappedSuperclass
public class JanusEntity {

    private static final Logger log = Logger.getLogger(JanusEntity.class);

    /**
     * The generic identity of the entity as a UUID.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
            @Parameter(name = org.hibernate.id.UUIDGenerator.UUID_GEN_STRATEGY_CLASS, value = "org.hibernate.id.uuid.CustomVersionOneStrategy")
    })
    public UUID id;

    public static <T> Uni<T> get(Class<T> clazz, Session s, UUID uuid) {
        log.info ("JanusEntity.get:" + uuid);
        return uuid!=null?s.find(clazz, uuid):Uni.createFrom().nullItem();
    }

    public static <T> Uni<Void> delete(Class<T> clazz, Session s, UUID uuid) {
        return uuid!=null?s.find(clazz, uuid).chain(u -> s.remove(u)):Uni.createFrom().voidItem();
    }

    public static <T> Uni<T> create(Session s, T object) {
        return s.persist(object).replaceWith(object);   
    }

}

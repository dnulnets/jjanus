package eu.stenlund.janus.model.base;

import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.MappedSuperclass;

/**
 * The JanusEntity that is used by all hibernate entities, it supports with
 * unique keys for
 * the identity of the entity using UUID.
 *
 * @author Tomas Stenlund
 * @since 2022-07-11
 * 
 */
@MappedSuperclass
public class JanusEntity {

    /**
     * The generic identity of the entity as a UUID.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
            @Parameter(name = org.hibernate.id.UUIDGenerator.UUID_GEN_STRATEGY_CLASS, value = "org.hibernate.id.uuid.CustomVersionOneStrategy")
    })
    private UUID id;

}

package eu.stenlund.janus.model;

import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class JanusEntity extends PanacheEntityBase{

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator",
        parameters = {
            @Parameter(
                name = org.hibernate.id.UUIDGenerator.UUID_GEN_STRATEGY_CLASS,
                value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
            )
        }
    )
    private UUID id;

}

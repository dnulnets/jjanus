package eu.stenlund.janus.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import eu.stenlund.janus.model.base.JanusEntity;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import java.util.UUID;

@Entity
@Table(name = "\"User\"")
public class User extends JanusEntity {

    @Column(length = 64, nullable = false, updatable = true)
    public String name;

    @Column(length = 64, nullable = false, updatable = true)
    public String email;

    @Column(length = 128, nullable = false, updatable = true)
    public String credential;
}

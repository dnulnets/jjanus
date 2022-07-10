package eu.stenlund.janus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import eu.stenlund.janus.model.base.JanusEntity;

@Entity
@Table(name = "role")
public class Role extends JanusEntity {
    
    @Column(unique = true, length = 64, nullable = false, updatable = false)
    public String name;

    @Column(unique = false, length = 256, nullable = true, updatable = true)
    public String description;

}

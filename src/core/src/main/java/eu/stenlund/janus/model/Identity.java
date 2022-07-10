package eu.stenlund.janus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "\"Identity\"")
public class Identity extends JanusEntity {
    
    @Column(unique = true, length = 64, nullable = false, updatable = false)
    public String identity;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public User user;

}

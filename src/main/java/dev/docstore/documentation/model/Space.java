package dev.docstore.documentation.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="wiki_t_space")
@Data
public class Space {

    @Id
    @GeneratedValue(generator = "SpaceGenerator")
    @SequenceGenerator(name = "SpaceGenerator", sequenceName = "wiki_s_space", allocationSize = 1)
    private Long id;

    @Column
    private String name;
    
}
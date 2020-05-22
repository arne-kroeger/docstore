package dev.docstore.documentation.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="wiki_t_tag")
@Data
public class Tag {

    @Id
    @GeneratedValue(generator = "TagGenerator")
    @SequenceGenerator(name = "TagGenerator", sequenceName = "wiki_s_tag", allocationSize = 1)
    private Long id;

    @Column
    private String name;
    
}
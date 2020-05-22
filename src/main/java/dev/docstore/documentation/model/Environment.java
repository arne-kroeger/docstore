package dev.docstore.documentation.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="wiki_t_environment")
@Data
public class Environment {

    @Id
    @GeneratedValue(generator = "EnvironmentGenerator")
    @SequenceGenerator(name = "EnvironmentGenerator", sequenceName = "wiki_s_environment", allocationSize = 1)
    private Long id;

    @Column
    private String name;
    
}
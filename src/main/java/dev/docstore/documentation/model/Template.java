package dev.docstore.documentation.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="wiki_t_template")
@Data
public class Template {

    @Id
    @GeneratedValue(generator = "TemplateGenerator")
    @SequenceGenerator(name = "TemplateGenerator", sequenceName = "wiki_s_template", allocationSize = 1)
    private Long id;

    @ManyToMany
    @JoinTable(name = "wiki_x_template_tag", joinColumns = { @JoinColumn(name = "id_template") }, inverseJoinColumns = {
            @JoinColumn(name = "id_tag") })
    private List<Tag> tags = new ArrayList<>();

    @Column
    private String uuid;
    private String name;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;


    
}
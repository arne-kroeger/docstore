package dev.docstore.documentation.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "wiki_t_document")
@Data
public class Document {

    @Id
    @GeneratedValue(generator = "DocumentGenerator")
    @SequenceGenerator(name = "DocumentGenerator", sequenceName = "wiki_s_document", allocationSize = 1)
    private Long id;

    @OneToOne
    private Space space;

    @OneToOne
    private Environment environment;

    @ManyToMany
    @JoinTable(name = "wiki_x_document_tag", joinColumns = { @JoinColumn(name = "id_document") }, inverseJoinColumns = {
            @JoinColumn(name = "id_tag") })
    private List<Tag> tags = new ArrayList<>();

    @Column
    private String uuid;
    private String title;
    private DocumentStatus status;

    private LocalDateTime createDate;
    private LocalDateTime latestUpdateReceived;

    private Boolean outdated = false;

    @Column(columnDefinition = "TEXT")
    private String body;

}
package dev.docstore.documentation;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import dev.docstore.documentation.api.DocumentApi;
import dev.docstore.documentation.api.model.Document;
import dev.docstore.documentation.api.model.Space;
import dev.docstore.documentation.api.model.TemplateData;
import dev.docstore.documentation.api.model.TemplateVariable;
import dev.docstore.documentation.model.DocumentStatus;
import dev.docstore.documentation.model.Environment;
import dev.docstore.documentation.model.Tag;
import dev.docstore.documentation.model.Template;
import dev.docstore.documentation.model.dao.DocumentDAO;
import dev.docstore.documentation.model.dao.EnvironmentDAO;
import dev.docstore.documentation.model.dao.SpaceDAO;
import dev.docstore.documentation.model.dao.TagDAO;
import dev.docstore.documentation.model.dao.TemplateDAO;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

@RestController
public class DocumentController implements DocumentApi {

    @Autowired
    private DocumentDAO documentDAO;

    @Autowired
    private SpaceDAO spaceDAO;

    @Autowired
    private TagDAO tagDAO;

    @Autowired
    private TemplateDAO templateDAO;

    @Autowired
    private EnvironmentDAO environmentDAO;

    @Override
    @Transactional
    public ResponseEntity<List<Document>> getLatestDocuments() {
        List<Document> documents = new ArrayList<>();
        for (dev.docstore.documentation.model.Document document : documentDAO.getLatestDocuments(12)) {
            documents.add(convertDocument(document));
        }
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Document> getDocument(String uuid, String environmentName) {
        Environment environment = environmentDAO.getByName(environmentName);

        return new ResponseEntity<>(convertDocument(documentDAO.getLatestVersion(uuid, environment)), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<List<Document>> searchDocuments(@Valid String body) {
        List<Document> documents = new ArrayList<>();

        for (dev.docstore.documentation.model.Document document : documentDAO.search(body, 100)) {
            documents.add(convertDocument(document));
        }

        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Document> addNewDocument(final @Valid Document givenDocument) {
        dev.docstore.documentation.model.Document storeDocument = new dev.docstore.documentation.model.Document();

        dev.docstore.documentation.model.Document latestVersion = documentDAO.getLatestVersion(givenDocument.getUuid(), createOrUpdateEnvironment(givenDocument.getEnvironment()));

        storeDocument.setUuid(givenDocument.getUuid());
        storeDocument.setTitle(givenDocument.getTitle());
        storeDocument.setBody(givenDocument.getContent());
        storeDocument.setEnvironment(createOrUpdateEnvironment(givenDocument.getEnvironment()));
        storeDocument.setSpace(createOrUpdateSpace(givenDocument.getSpace()));
        storeDocument.getTags().addAll(createOrUpdateTags(givenDocument.getTags()));
        storeDocument.setCreateDate(LocalDateTime.now());
        storeDocument.setLatestUpdateReceived(LocalDateTime.now());
        storeDocument.setStatus(DocumentStatus.ACTIVE);

        if (latestVersion == null || !latestVersion.getTitle().equals(storeDocument.getTitle())
                || !latestVersion.getBody().equals(storeDocument.getBody())
                || !compareTagsEqual(latestVersion.getTags(), storeDocument.getTags())
                || !latestVersion.getSpace().getId().equals(storeDocument.getSpace().getId())) {
            documentDAO.save(storeDocument);
            documentDAO.outdateOldEntries(storeDocument);
        } else {
            latestVersion.setLatestUpdateReceived(LocalDateTime.now());
            documentDAO.save(latestVersion);
            documentDAO.outdateOldEntries(latestVersion);
        }

        return new ResponseEntity<>(convertDocument(storeDocument), HttpStatus.OK);
    }

    private Environment createOrUpdateEnvironment(final dev.docstore.documentation.api.model.@Valid Environment givenEnvironment) {
        Environment environment = environmentDAO.getByName(givenEnvironment.getName());
        
        if (environment == null) {
            environment = new Environment();
            environment.setName(givenEnvironment.getName());

            environmentDAO.save(environment);
        }

        return environment;
    }

    @Override
    @Transactional
    public ResponseEntity<Document> addNewDocumentForTemplate(String templateUuid, @Valid TemplateData templateData) {
        Template template = templateDAO.getByUuid(templateUuid);

        if (template == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String content = "";
        String title = "";
        try {
            content = parseTemplate(templateUuid, templateData, template.getContent());
            title = parseTemplate(templateUuid, templateData, template.getTitle());
        } catch (IOException | TemplateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }

        Document document = new Document();
        document.setContent(content);
        document.setSpace(templateData.getSpace());
        document.setEnvironment(templateData.getEnvironment());
        document.setTitle(title);
        document.setUuid(templateData.getUuid());
        document.getTags().addAll(convertTags(template.getTags()));
        document.getTags().addAll(templateData.getAdditionalTags());

        return this.addNewDocument(document);
    }

    private String parseTemplate(String templateUuid, TemplateData templateData, String templateContent) throws IOException, TemplateException {
        freemarker.template.Template freemarkerTemplate = new freemarker.template.Template(templateUuid,
                new StringReader(templateContent), new Configuration());
        Map<String, Object> variables = new LinkedHashMap<>();

        for (TemplateVariable variable : templateData.getVariables()) {
            variables.put(variable.getName(), variable.getValue());
        }

        Writer out = new StringWriter();
        freemarkerTemplate.process(variables, out);

        return out.toString();
    }

    private boolean compareTagsEqual(List<Tag> tags, List<Tag> tags2) {
        if (tags.size() != tags2.size()) {
            return false;
        }

        Set<Tag> intersect = new HashSet<>(tags);
        intersect.retainAll(tags2);

        return intersect.size() == tags.size();
    }

    private Collection<? extends Tag> createOrUpdateTags(List<String> tags) {
        List<Tag> storedTags = new ArrayList<>();
        for (String tag : tags) {
            Tag storedTag = tagDAO.getByName(tag);

            if (storedTag == null) {
                storedTag = new Tag();
                storedTag.setName(tag);

                tagDAO.save(storedTag);
            }

            storedTags.add(storedTag);
        }

        return storedTags;
    }

    private dev.docstore.documentation.model.Space createOrUpdateSpace(@Valid Space givenSpace) {
        dev.docstore.documentation.model.Space space = spaceDAO.getByName(givenSpace.getName());

        if (space == null) {
            space = new dev.docstore.documentation.model.Space();
            space.setName(givenSpace.getName());

            spaceDAO.save(space);
        }

        return space;
    }

    private Document convertDocument(final dev.docstore.documentation.model.Document storedDocument) {
        Document document = new Document();
        document.setId(storedDocument.getId());
        document.setContent(storedDocument.getBody());
        document.setTitle(storedDocument.getTitle());
        document.setSpace(convertSpace(storedDocument.getSpace()));
        document.setEnvironment(convertEnvironment(storedDocument.getEnvironment()));
        
        for (Environment env : documentDAO.getAllEnvironmentsByUuid(storedDocument.getUuid(), Arrays.asList(storedDocument.getEnvironment()))) {
            document.getOtherEnvironments().add(convertEnvironment(env));
        }
        
        document.setTags(convertTags(storedDocument.getTags()));
        document.setUuid(storedDocument.getUuid());
        document.setLatestChange(storedDocument.getCreateDate().atOffset(OffsetDateTime.now().getOffset()));
        
        if (storedDocument.getLatestUpdateReceived() != null) {
            document.setLatestUpdate(storedDocument.getLatestUpdateReceived().atOffset(OffsetDateTime.now().getOffset()));
        }
        

        return document;
    }

    private dev.docstore.documentation.api.model.Environment convertEnvironment(Environment storedEnvironment) {
        dev.docstore.documentation.api.model.Environment environment = new dev.docstore.documentation.api.model.Environment();

        environment.setName(storedEnvironment.getName());

        return environment;
    }

    private List<String> convertTags(List<Tag> tags) {
        List<String> tagList = new ArrayList<>();

        for (Tag tag : tags) {
            tagList.add(tag.getName());
        }

        return tagList;
    }

    private Space convertSpace(dev.docstore.documentation.model.Space storedSpace) {
        Space space = new Space();
        space.setName(storedSpace.getName());

        return space;
    }

}

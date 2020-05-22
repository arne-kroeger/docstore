package dev.docstore.documentation.model.dao;

import java.util.List;

import dev.docstore.documentation.model.Document;
import dev.docstore.documentation.model.Environment;

public interface DocumentDAO extends BaseDAO {
    
    public Document getLatestVersion(String uuid, Environment environment);
    public void outdateOldEntries(Document document);
    public List<Document> getLatestDocuments(Integer limit);
    public List<Document> search(String saerchString, int limit);
    public List<Environment> getAllEnvironmentsByUuid(String uuid, List<Environment> blacklist);

}
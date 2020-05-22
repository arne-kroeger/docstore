package dev.docstore.documentation.model.dao;

import dev.docstore.documentation.model.Template;

public interface TemplateDAO extends BaseDAO {
    
    public Template getByUuid(String uuid);

}
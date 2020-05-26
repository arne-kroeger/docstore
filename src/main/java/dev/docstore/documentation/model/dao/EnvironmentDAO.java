package dev.docstore.documentation.model.dao;

import dev.docstore.documentation.model.Environment;

public interface EnvironmentDAO extends BaseDAO {
    
    public Environment getByName(String name);

}
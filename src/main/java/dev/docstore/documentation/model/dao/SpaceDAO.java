package dev.docstore.documentation.model.dao;

import dev.docstore.documentation.model.Space;

public interface SpaceDAO extends BaseDAO {
    
    public Space getByName(String name);

}
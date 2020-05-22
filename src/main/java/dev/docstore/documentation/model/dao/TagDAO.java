package dev.docstore.documentation.model.dao;

import dev.docstore.documentation.model.Tag;

public interface TagDAO extends BaseDAO {
    
    public Tag getByName(String name);

}
package dev.docstore.documentation.model.dao;

public interface BaseDAO {
    
    public <T> T get(Class<T> type, Long id);
    public <T> void save(T entity);

}
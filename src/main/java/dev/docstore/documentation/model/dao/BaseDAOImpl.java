package dev.docstore.documentation.model.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class BaseDAOImpl implements BaseDAO {
    
    protected EntityManager em;

    @PersistenceContext(unitName="entityManagerFactory")
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

    @Override
    public <T> T get(Class<T> type, Long id) {
        return this.em.find(type, id);
    }

    @Override
    public <T> void save(T entity) {
        this.em.persist(entity);
    }

}
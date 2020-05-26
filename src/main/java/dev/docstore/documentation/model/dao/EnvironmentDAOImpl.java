package dev.docstore.documentation.model.dao;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import dev.docstore.documentation.model.Environment;
import dev.docstore.documentation.model.Environment_;

import org.springframework.stereotype.Component;

@Component
public class EnvironmentDAOImpl extends BaseDAOImpl implements EnvironmentDAO {

    @Override
    public Environment getByName(String name) {
        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaQuery<Environment> query = builder.createQuery(Environment.class);
        Root<Environment> root = query.from(Environment.class);

        query.where(builder.equal(builder.lower(root.get(Environment_.name)), name.toLowerCase()));

        try {
            return this.em.createQuery(query).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    

}
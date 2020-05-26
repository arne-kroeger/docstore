package dev.docstore.documentation.model.dao;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import dev.docstore.documentation.model.Tag;
import dev.docstore.documentation.model.Tag_;

import org.springframework.stereotype.Component;

@Component
public class TagDAOImpl extends BaseDAOImpl implements TagDAO {

    @Override
    public Tag getByName(String name) {
        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaQuery<Tag> query = builder.createQuery(Tag.class);
        Root<Tag> root = query.from(Tag.class);

        query.where(builder.equal(builder.lower(root.get(Tag_.name)), name.toLowerCase()));

        try {
            return this.em.createQuery(query).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    

}
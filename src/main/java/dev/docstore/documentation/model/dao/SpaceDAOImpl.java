package dev.docstore.documentation.model.dao;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import dev.docstore.documentation.model.Space;
import dev.docstore.documentation.model.Space_;

import org.springframework.stereotype.Component;

@Component
public class SpaceDAOImpl extends BaseDAOImpl implements SpaceDAO {

    @Override
    public Space getByName(String name) {
        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaQuery<Space> query = builder.createQuery(Space.class);
        Root<Space> root = query.from(Space.class);

        query.where(builder.equal(builder.lower(root.get(Space_.name)), name.toLowerCase()));

        try {
            return this.em.createQuery(query).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    

}
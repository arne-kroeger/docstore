package dev.docstore.documentation.model.dao;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import dev.docstore.documentation.model.Template;
import dev.docstore.documentation.model.Template_;

import org.springframework.stereotype.Component;

@Component
public class TemplateDAOImpl extends BaseDAOImpl implements TemplateDAO {

    @Override
    public Template getByUuid(String uuid) {
        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaQuery<Template> query = builder.createQuery(Template.class);
        Root<Template> root = query.from(Template.class);

        query.where(builder.equal(root.get(Template_.uuid), uuid));

        try {
            return this.em.createQuery(query).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    

}
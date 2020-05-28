package dev.docstore.documentation.model.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;

import dev.docstore.documentation.model.Document;
import dev.docstore.documentation.model.DocumentStatus;
import dev.docstore.documentation.model.Document_;
import dev.docstore.documentation.model.Environment;
import dev.docstore.documentation.model.Environment_;
import dev.docstore.documentation.model.Space;
import dev.docstore.documentation.model.Space_;
import dev.docstore.documentation.model.Tag;
import dev.docstore.documentation.model.Tag_;

@Component
public class DocumentDAOImpl extends BaseDAOImpl implements DocumentDAO {

    @Override
    public Document getLatestVersion(String uuid, Environment environment) {
        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaQuery<Document> query = builder.createQuery(Document.class);
        Root<Document> root = query.from(Document.class);

        query.where(builder.equal(root.get(Document_.uuid), uuid),
                builder.equal(root.get(Document_.environment), environment),
                builder.equal(root.get(Document_.outdated), false));

        query.orderBy(builder.desc(root.get(Document_.createDate)));

        try {
            return this.em.createQuery(query).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void outdateOldEntries(Document document) {
        this.em.createQuery(
                "update Document set outdated = true, status = :status where uuid = :uuid and id != :id and environment = :env")
                .setParameter("status", DocumentStatus.HISTORY)
                .setParameter("uuid", document.getUuid())
                .setParameter("id", document.getId())
                .setParameter("env", document.getEnvironment()).executeUpdate();
    }

    @Override
    public List<Document> getLatestDocuments(Integer limit) {
        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaQuery<Document> query = builder.createQuery(Document.class);
        Root<Document> root = query.from(Document.class);

        query.where(builder.equal(root.get(Document_.outdated), false));

        query.orderBy(builder.desc(root.get(Document_.createDate)));

        return this.em.createQuery(query).setMaxResults(limit.intValue()).getResultList();
    }

    @Override
    public List<Document> search(String saerchString, int limit) {
        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaQuery<Document> query = builder.createQuery(Document.class);
        Root<Document> root = query.from(Document.class);
        ListJoin<Document, Tag> tagJoin = root.join(Document_.tags, JoinType.LEFT);
        Join<Document, Environment> environmentJoin = root.join(Document_.environment, JoinType.LEFT);
        Join<Document, Space> spaceJoin = root.join(Document_.space, JoinType.LEFT);

        query.distinct(true);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get(Document_.outdated), false));

        for (String searchTerm : saerchString.toLowerCase().split(" ")) {
            if (searchTerm.startsWith("#")) {
                predicates.add(builder
                        .or(builder.like(builder.lower(tagJoin.get(Tag_.name)), "%" + searchTerm.substring(1) + "%")));
            } else if (searchTerm.startsWith("+")) {
                predicates.add(builder.or(builder.like(builder.lower(environmentJoin.get(Environment_.name)),
                        "%" + searchTerm.substring(1) + "%")));
            } else if (searchTerm.startsWith("*")) {
                predicates.add(builder.or(
                        builder.like(builder.lower(spaceJoin.get(Space_.name)), "%" + searchTerm.substring(1) + "%")));
            } else {
                predicates
                        .add(builder.or(builder.like(builder.lower(root.get(Document_.title)), "%" + searchTerm + "%"),
                                builder.like(builder.lower(root.get(Document_.body)), "%" + searchTerm + "%"),
                                builder.like(builder.lower(tagJoin.get(Tag_.name)), "%" + searchTerm + "%"),
                                builder.like(builder.lower(environmentJoin.get(Environment_.name)),
                                        "%" + searchTerm + "%"),
                                builder.like(builder.lower(spaceJoin.get(Space_.name)), "%" + searchTerm + "%")));
            }

        }
        query.where(predicates.toArray(new Predicate[predicates.size()]));

        query.orderBy(builder.desc(root.get(Document_.createDate)));

        return this.em.createQuery(query).setMaxResults(limit).getResultList();
    }

    @Override
    public List<Environment> getAllEnvironmentsByUuid(String uuid, List<Environment> blacklist) {
        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaQuery<Environment> query = builder.createQuery(Environment.class);
        Root<Document> root = query.from(Document.class);
        Join<Document, Environment> environmentJoin = root.join(Document_.environment);

        query.select(environmentJoin);
        query.distinct(true);

        List<Predicate> predicates = new ArrayList<>();
        predicates.addAll(Arrays.asList(builder.equal(root.get(Document_.uuid), uuid),
                builder.equal(root.get(Document_.outdated), false)));

        if (blacklist != null && !blacklist.isEmpty()) {
            predicates.add(builder.not(environmentJoin.in(blacklist)));
        }

        query.where(predicates.toArray(new Predicate[predicates.size()]));

        query.orderBy(builder.asc(environmentJoin.get(Environment_.name)));


        return this.em.createQuery(query).getResultList();
    }

    @Override
    public List<Document> getAllWithStatusAndLatestUpdateOlderThan(LocalDateTime date, DocumentStatus... status) {
        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaQuery<Document> query = builder.createQuery(Document.class);
        Root<Document> root = query.from(Document.class);
        
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.lessThan(root.get(Document_.latestUpdateReceived), date));

        if (status != null && status.length > 0) {
            predicates.add(root.get(Document_.status).in(status));
        }

        query.where(predicates.toArray(new Predicate[predicates.size()]));

        return this.em.createQuery(query).getResultList();
    }

    

}
package com.iorga.ivif.test.service;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.RolesAllowed;
import com.iorga.ivif.ja.SecurityService;
import com.iorga.ivif.ja.Sorting;
import com.iorga.ivif.ja.SortingType;
import com.iorga.ivif.test.entity.Profile;
import com.iorga.ivif.test.entity.QProfile;
import com.iorga.ivif.test.ws.EditableProfileGridBaseWS.EditableProfileGridSearchFilter;
import com.iorga.ivif.test.ws.EditableProfileGridBaseWS.EditableProfileGridSearchParam;
import com.iorga.ivif.test.ws.EditableProfileGridBaseWS.EditableProfileGridSearchResult;
import com.iorga.ivif.test.ws.ProfileGridBaseWS.OpenProfileGridFromUser;
import com.iorga.ivif.test.ws.ProfileGridBaseWS.ProfileGridSearchFilter;
import com.iorga.ivif.test.ws.ProfileGridBaseWS.ProfileGridSearchParam;
import com.iorga.ivif.test.ws.ProfileGridBaseWS.ProfileGridSearchResult;
import com.mysema.query.SearchResults;
import com.mysema.query.jpa.JPQLTemplates;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.ConstructorExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import java.lang.Integer;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Generated
@Stateless
public class ProfileBaseService {
    @PersistenceContext
    protected EntityManager entityManager;

    @Inject
    protected SecurityService securityService;



    public Profile find(Integer id) {
        return entityManager.find(Profile.class, id);
    }

    public boolean isNew(Profile entity) {
        return entity.getId() == null;
    }

    @TransactionAttribute
    public Profile save(Profile entityToSave) {
        return save(entityToSave, true);
    }

    @TransactionAttribute
    protected Profile save(Profile entityToSave, boolean flush) {
        if (isNew(entityToSave)) {
            entityManager.persist(entityToSave);
        } else {
            entityManager.merge(entityToSave);
        }
        if (flush) {
            entityManager.flush();
        }
        return entityToSave;
    }

    @TransactionAttribute
    public List<Profile> save(List<Profile> entitiesToSave) {
        for (Profile entityToSave : entitiesToSave) {
            save(entityToSave, false);
        }
        entityManager.flush();
        return entitiesToSave;
    }

    public void detach(Profile entity) {
        entityManager.detach(entity);
    }

    @RolesAllowed("manager")
    public SearchResults<ProfileGridSearchResult> search(ProfileGridSearchParam searchParam) {
        JPAQuery jpaQuery = new JPAQuery(entityManager, JPQLTemplates.DEFAULT);
        QProfile $record = new QProfile("profile");
        jpaQuery.from($record);
        // Applying filter
        ProfileGridSearchFilter filter = searchParam.filter;
        if (filter.name != null) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
        }
        // Applying action filters
        if (filter.openProfileGridFromUser != null) {
            // Check additionnal rights for that action
            securityService.check("admin");
            OpenProfileGridFromUser parameters = filter.openProfileGridFromUser;
            jpaQuery.where($record.id.eq(parameters.profileId));
        }
        // Applying sorting
        Sorting sorting = searchParam.sorting;
        ComparableExpressionBase sortingExpression = null;
        if ("name".equals(sorting.ref)) {
            sortingExpression = $record.name;
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(SortingType.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        }
        // Applying limit & offset
        jpaQuery.limit(searchParam.limit);
        jpaQuery.offset(searchParam.offset);
        // Returning projection
        return jpaQuery.listResults(ConstructorExpression.create(ProfileGridSearchResult.class, $record.name));
    }

    public SearchResults<EditableProfileGridSearchResult> search(EditableProfileGridSearchParam searchParam) {
        JPAQuery jpaQuery = new JPAQuery(entityManager, JPQLTemplates.DEFAULT);
        QProfile $record = new QProfile("profile");
        jpaQuery.from($record);
        // Applying filter
        EditableProfileGridSearchFilter filter = searchParam.filter;
        if (filter.name != null) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
        }
        // Applying action filters
        // Applying sorting
        Sorting sorting = searchParam.sorting;
        ComparableExpressionBase sortingExpression = null;
        if ("name".equals(sorting.ref)) {
            sortingExpression = $record.name;
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(SortingType.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        }
        // Applying limit & offset
        jpaQuery.limit(searchParam.limit);
        jpaQuery.offset(searchParam.offset);
        // Returning projection
        return jpaQuery.listResults(ConstructorExpression.create(EditableProfileGridSearchResult.class, $record.name, $record.id));
    }


}
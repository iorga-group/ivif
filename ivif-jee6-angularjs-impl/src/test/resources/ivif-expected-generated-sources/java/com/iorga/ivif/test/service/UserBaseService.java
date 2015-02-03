package com.iorga.ivif.test.service;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.RolesAllowed;
import com.iorga.ivif.ja.Sorting;
import com.iorga.ivif.ja.SortingType;
import com.iorga.ivif.test.entity.QUser;
import com.iorga.ivif.test.entity.User;
import com.iorga.ivif.test.ws.EditableUserGridBaseWS.EditableUserGridSearchFilter;
import com.iorga.ivif.test.ws.EditableUserGridBaseWS.EditableUserGridSearchParam;
import com.iorga.ivif.test.ws.EditableUserGridBaseWS.EditableUserGridSearchResult;
import com.iorga.ivif.test.ws.ToolbarUserGridBaseWS.ToolbarUserGridSearchFilter;
import com.iorga.ivif.test.ws.ToolbarUserGridBaseWS.ToolbarUserGridSearchParam;
import com.iorga.ivif.test.ws.ToolbarUserGridBaseWS.ToolbarUserGridSearchResult;
import com.iorga.ivif.test.ws.UserGridBaseWS.OpenUserGridFromComputer;
import com.iorga.ivif.test.ws.UserGridBaseWS.UserGridSearchFilter;
import com.iorga.ivif.test.ws.UserGridBaseWS.UserGridSearchParam;
import com.iorga.ivif.test.ws.UserGridBaseWS.UserGridSearchResult;
import com.mysema.query.SearchResults;
import com.mysema.query.jpa.JPQLTemplates;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.ConstructorExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import java.lang.Integer;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Generated
@Stateless
public class UserBaseService {
    @PersistenceContext
    protected EntityManager entityManager;



    public User find(Integer id) {
        return entityManager.find(User.class, id);
    }

    public boolean isNew(User entity) {
        return entity.getId() == null;
    }

    @TransactionAttribute
    public User save(User entityToSave) {
        return save(entityToSave, true);
    }

    @TransactionAttribute
    protected User save(User entityToSave, boolean flush) {
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
    public List<User> save(List<User> entitiesToSave) {
        for (User entityToSave : entitiesToSave) {
            save(entityToSave, false);
        }
        entityManager.flush();
        return entitiesToSave;
    }

    public void detach(User entity) {
        entityManager.detach(entity);
    }

    public SearchResults<UserGridSearchResult> search(UserGridSearchParam searchParam) {
        JPAQuery jpaQuery = new JPAQuery(entityManager, JPQLTemplates.DEFAULT);
        QUser $record = new QUser("user");
        jpaQuery.from($record);
        // Applying filter
        UserGridSearchFilter filter = searchParam.filter;
        if (filter.name != null) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
        }
        if (filter.profile_id != null) {
            jpaQuery.where($record.profile.id.eq(filter.profile_id));
        }
        // Applying action filters
        if (filter.openUserGridFromComputer != null) {
            OpenUserGridFromComputer parameters = filter.openUserGridFromComputer;
            jpaQuery.where($record.id.eq(parameters.userId));
        }
        // Applying sorting
        Sorting sorting = searchParam.sorting;
        ComparableExpressionBase sortingExpression = null;
        if ("name".equals(sorting.ref)) {
            sortingExpression = $record.name;
        } else if ("profile_id".equals(sorting.ref)) {
            sortingExpression = $record.profile.id;
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(SortingType.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        }
        // Applying limit & offset
        jpaQuery.limit(searchParam.limit);
        jpaQuery.offset(searchParam.offset);
        // Returning projection
        return jpaQuery.listResults(ConstructorExpression.create(UserGridSearchResult.class, $record.name, $record.profile.id));
    }

    @RolesAllowed({"admin", "manager"})
    public SearchResults<EditableUserGridSearchResult> search(EditableUserGridSearchParam searchParam) {
        JPAQuery jpaQuery = new JPAQuery(entityManager, JPQLTemplates.DEFAULT);
        QUser $record = new QUser("user");
        jpaQuery.from($record);
        // Applying filter
        EditableUserGridSearchFilter filter = searchParam.filter;
        if (filter.firstName != null) {
            jpaQuery.where($record.firstName.containsIgnoreCase(filter.firstName));
        }
        if (filter.name != null) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
        }
        // Applying sorting
        Sorting sorting = searchParam.sorting;
        ComparableExpressionBase sortingExpression = null;
        if ("firstName".equals(sorting.ref)) {
            sortingExpression = $record.firstName;
        } else if ("name".equals(sorting.ref)) {
            sortingExpression = $record.name;
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(SortingType.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        }
        // Applying limit & offset
        jpaQuery.limit(searchParam.limit);
        jpaQuery.offset(searchParam.offset);
        // Returning projection
        return jpaQuery.listResults(ConstructorExpression.create(EditableUserGridSearchResult.class, $record.firstName, $record.name, $record.profile.id, $record.id, $record.version));
    }

    public SearchResults<ToolbarUserGridSearchResult> search(ToolbarUserGridSearchParam searchParam) {
        JPAQuery jpaQuery = new JPAQuery(entityManager, JPQLTemplates.DEFAULT);
        QUser $record = new QUser("user");
        jpaQuery.from($record);
        // Applying filter
        ToolbarUserGridSearchFilter filter = searchParam.filter;
        if (filter.name != null) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
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
        return jpaQuery.listResults(ConstructorExpression.create(ToolbarUserGridSearchResult.class, $record.name, $record.profile.id, $record.id));
    }


}
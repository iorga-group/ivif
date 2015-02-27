package com.iorga.ivif.test.service;

import com.iorga.ivif.ja.EntityBaseService;
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
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.ConstructorExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import java.lang.Integer;
import java.lang.Override;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Generated
@Stateless
public class ProfileBaseService extends EntityBaseService<Profile, Integer> {

    @PersistenceContext
    protected EntityManager entityManager;

    @Inject
    protected SecurityService securityService;


    @Override
    protected Integer getId(Profile entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Profile entity, Integer id) {
        entity.setId(id);
    }

    @RolesAllowed("manager")
    public SearchResults<ProfileGridSearchResult> search(ProfileGridSearchParam searchParam) {
        JPAQuery jpaQuery = createJPAQuery();
        QProfile $record = new QProfile("profile");
        jpaQuery.from($record);
        applyQueryAndFiltersAndSorting($record, searchParam, jpaQuery);
        applyLimitAndOffset(searchParam, jpaQuery);
        return listSearchResults($record, searchParam, jpaQuery);
    }

    protected void applyQueryAndFiltersAndSorting(QProfile $record, ProfileGridSearchParam searchParam, JPAQuery jpaQuery) {
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
    }

    protected void applyLimitAndOffset(ProfileGridSearchParam searchParam, JPAQuery jpaQuery) {
        jpaQuery.limit(searchParam.limit);
        jpaQuery.offset(searchParam.offset);
    }

    protected SearchResults<ProfileGridSearchResult> listSearchResults(QProfile $record, ProfileGridSearchParam searchParam, JPAQuery jpaQuery) {
        return jpaQuery.listResults(ConstructorExpression.create(ProfileGridSearchResult.class, $record.name));
    }

    public SearchResults<EditableProfileGridSearchResult> search(EditableProfileGridSearchParam searchParam) {
        JPAQuery jpaQuery = createJPAQuery();
        QProfile $record = new QProfile("profile");
        jpaQuery.from($record);
        applyQueryAndFiltersAndSorting($record, searchParam, jpaQuery);
        applyLimitAndOffset(searchParam, jpaQuery);
        return listSearchResults($record, searchParam, jpaQuery);
    }

    protected void applyQueryAndFiltersAndSorting(QProfile $record, EditableProfileGridSearchParam searchParam, JPAQuery jpaQuery) {
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
    }

    protected void applyLimitAndOffset(EditableProfileGridSearchParam searchParam, JPAQuery jpaQuery) {
        jpaQuery.limit(searchParam.limit);
        jpaQuery.offset(searchParam.offset);
    }

    protected SearchResults<EditableProfileGridSearchResult> listSearchResults(QProfile $record, EditableProfileGridSearchParam searchParam, JPAQuery jpaQuery) {
        return jpaQuery.listResults(ConstructorExpression.create(EditableProfileGridSearchResult.class, $record.name, $record.id));
    }


}
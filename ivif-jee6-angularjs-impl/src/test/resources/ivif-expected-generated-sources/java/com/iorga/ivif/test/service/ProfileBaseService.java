package com.iorga.ivif.test.service;

import com.iorga.ivif.ja.EntityBaseService;
import com.iorga.ivif.ja.EntityBaseService.SearchState;
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
import java.util.List;
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



    protected class ProfileGridSearchState extends SearchState<QProfile, ProfileGridSearchParam> {
        protected ProfileGridSearchState(ProfileGridSearchParam searchParam) {
            super(new QProfile("profile"), searchParam);
        }
    }

    @RolesAllowed("manager")
    public SearchResults<ProfileGridSearchResult> search(ProfileGridSearchParam searchParam) {
        ProfileGridSearchState searchState = new ProfileGridSearchState(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return listSearchResults(searchState);
    }

    protected void applyQueryAndFiltersAndSorting(ProfileGridSearchState searchState) {
        QProfile $record = searchState.$record;
        ProfileGridSearchParam searchParam = searchState.searchParam;
        JPAQuery jpaQuery = searchState.jpaQuery;
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

    protected SearchResults<ProfileGridSearchResult> listSearchResults(ProfileGridSearchState searchState) {
        return searchState.jpaQuery.listResults(listExpression(searchState));
    }

    protected ConstructorExpression<ProfileGridSearchResult> listExpression(ProfileGridSearchState searchState) {
        QProfile $record = searchState.$record;
        return ConstructorExpression.create(ProfileGridSearchResult.class, $record.name);
    }

    @RolesAllowed("manager")
    public List<ProfileGridSearchResult> find(ProfileGridSearchParam searchParam) {
        ProfileGridSearchState searchState = new ProfileGridSearchState(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return list(searchState);
    }

    protected List<ProfileGridSearchResult> list(ProfileGridSearchState searchState) {
        return searchState.jpaQuery.list(listExpression(searchState));
    }


    protected class EditableProfileGridSearchState extends SearchState<QProfile, EditableProfileGridSearchParam> {
        protected EditableProfileGridSearchState(EditableProfileGridSearchParam searchParam) {
            super(new QProfile("profile"), searchParam);
        }
    }

    public SearchResults<EditableProfileGridSearchResult> search(EditableProfileGridSearchParam searchParam) {
        EditableProfileGridSearchState searchState = new EditableProfileGridSearchState(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return listSearchResults(searchState);
    }

    protected void applyQueryAndFiltersAndSorting(EditableProfileGridSearchState searchState) {
        QProfile $record = searchState.$record;
        EditableProfileGridSearchParam searchParam = searchState.searchParam;
        JPAQuery jpaQuery = searchState.jpaQuery;
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

    protected SearchResults<EditableProfileGridSearchResult> listSearchResults(EditableProfileGridSearchState searchState) {
        return searchState.jpaQuery.listResults(listExpression(searchState));
    }

    protected ConstructorExpression<EditableProfileGridSearchResult> listExpression(EditableProfileGridSearchState searchState) {
        QProfile $record = searchState.$record;
        return ConstructorExpression.create(EditableProfileGridSearchResult.class, $record.name, $record.id);
    }

    public List<EditableProfileGridSearchResult> find(EditableProfileGridSearchParam searchParam) {
        EditableProfileGridSearchState searchState = new EditableProfileGridSearchState(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return list(searchState);
    }

    protected List<EditableProfileGridSearchResult> list(EditableProfileGridSearchState searchState) {
        return searchState.jpaQuery.list(listExpression(searchState));
    }


}
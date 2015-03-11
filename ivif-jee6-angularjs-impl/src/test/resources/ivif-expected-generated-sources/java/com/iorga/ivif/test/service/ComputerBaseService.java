package com.iorga.ivif.test.service;

import com.iorga.ivif.ja.EntityBaseService;
import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.Sorting;
import com.iorga.ivif.ja.SortingType;
import com.iorga.ivif.ja.test.ConnectedUser;
import com.iorga.ivif.test.entity.Computer;
import com.iorga.ivif.test.entity.QComputer;
import com.iorga.ivif.test.ws.ComputerForConnectedUserGridBaseWS.ComputerForConnectedUserGridSearchFilter;
import com.iorga.ivif.test.ws.ComputerForConnectedUserGridBaseWS.ComputerForConnectedUserGridSearchParam;
import com.iorga.ivif.test.ws.ComputerForConnectedUserGridBaseWS.ComputerForConnectedUserGridSearchResult;
import com.iorga.ivif.test.ws.ComputerGridBaseWS.ComputerGridSearchFilter;
import com.iorga.ivif.test.ws.ComputerGridBaseWS.ComputerGridSearchParam;
import com.iorga.ivif.test.ws.ComputerGridBaseWS.ComputerGridSearchResult;
import com.iorga.ivif.test.ws.ComputerGridBaseWS.OpenComputerGridFromUser;
import com.iorga.ivif.test.ws.ComputerToCurrentUserDesktopSessionGridBaseWS.ComputerToCurrentUserDesktopSessionGridSearchFilter;
import com.iorga.ivif.test.ws.ComputerToCurrentUserDesktopSessionGridBaseWS.ComputerToCurrentUserDesktopSessionGridSearchParam;
import com.iorga.ivif.test.ws.ComputerToCurrentUserDesktopSessionGridBaseWS.ComputerToCurrentUserDesktopSessionGridSearchResult;
import com.iorga.ivif.test.ws.ComputerToDesktopSessionGridBaseWS.ComputerToDesktopSessionGridSearchFilter;
import com.iorga.ivif.test.ws.ComputerToDesktopSessionGridBaseWS.ComputerToDesktopSessionGridSearchParam;
import com.iorga.ivif.test.ws.ComputerToDesktopSessionGridBaseWS.ComputerToDesktopSessionGridSearchResult;
import com.mysema.query.SearchResults;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.ConstructorExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import java.lang.Integer;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Generated
@Stateless
public class ComputerBaseService extends EntityBaseService<Computer, Integer> {

    @PersistenceContext
    protected EntityManager entityManager;

    @Inject
    protected ConnectedUser connectedUser;


    public SearchResults<ComputerGridSearchResult> search(ComputerGridSearchParam searchParam) {
        JPAQuery jpaQuery = createJPAQuery();
        QComputer $record = new QComputer("computer");
        jpaQuery.from($record);
        applyQueryAndFiltersAndSorting($record, searchParam, jpaQuery);
        applyLimitAndOffset(searchParam, jpaQuery);
        return listSearchResults($record, searchParam, jpaQuery);
    }

    protected void applyQueryAndFiltersAndSorting(QComputer $record, ComputerGridSearchParam searchParam, JPAQuery jpaQuery) {
        // Applying static query
        jpaQuery.where($record.defaultProfile.isNull());
        // Applying filter
        ComputerGridSearchFilter filter = searchParam.filter;
        if (filter.name != null) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
        }
        if (filter.user_name != null) {
            jpaQuery.where($record.user.name.containsIgnoreCase(filter.user_name));
        }
        // Applying action filters
        if (filter.openComputerGridFromUser != null) {
            OpenComputerGridFromUser parameters = filter.openComputerGridFromUser;
            jpaQuery.where($record.user.id.eq(parameters.userid));
        }
        // Applying sorting
        Sorting sorting = searchParam.sorting;
        ComparableExpressionBase sortingExpression = null;
        if ("name".equals(sorting.ref)) {
            sortingExpression = $record.name;
        } else if ("user_name".equals(sorting.ref)) {
            sortingExpression = $record.user.name;
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(SortingType.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        }
    }

    protected void applyLimitAndOffset(ComputerGridSearchParam searchParam, JPAQuery jpaQuery) {
        jpaQuery.limit(searchParam.limit);
        jpaQuery.offset(searchParam.offset);
    }

    protected SearchResults<ComputerGridSearchResult> listSearchResults(QComputer $record, ComputerGridSearchParam searchParam, JPAQuery jpaQuery) {
        return jpaQuery.listResults(ConstructorExpression.create(ComputerGridSearchResult.class, $record.name, $record.user.name, $record.user.id));
    }

    public SearchResults<ComputerToDesktopSessionGridSearchResult> search(ComputerToDesktopSessionGridSearchParam searchParam) {
        JPAQuery jpaQuery = createJPAQuery();
        QComputer $record = new QComputer("computer");
        jpaQuery.from($record);
        applyQueryAndFiltersAndSorting($record, searchParam, jpaQuery);
        applyLimitAndOffset(searchParam, jpaQuery);
        return listSearchResults($record, searchParam, jpaQuery);
    }

    protected void applyQueryAndFiltersAndSorting(QComputer $record, ComputerToDesktopSessionGridSearchParam searchParam, JPAQuery jpaQuery) {
        // Applying filter
        ComputerToDesktopSessionGridSearchFilter filter = searchParam.filter;
        if (filter.id != null) {
            jpaQuery.where($record.id.like("%" + filter.id + "%"));
        }
        if (filter.name != null) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
        }
        if (filter.user_id != null) {
            jpaQuery.where($record.user.id.like("%" + filter.user_id + "%"));
        }
        // Applying action filters
        // Applying sorting
        Sorting sorting = searchParam.sorting;
        ComparableExpressionBase sortingExpression = null;
        if ("id".equals(sorting.ref)) {
            sortingExpression = $record.id;
        } else if ("name".equals(sorting.ref)) {
            sortingExpression = $record.name;
        } else if ("user_id".equals(sorting.ref)) {
            sortingExpression = $record.user.id;
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(SortingType.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        }
    }

    protected void applyLimitAndOffset(ComputerToDesktopSessionGridSearchParam searchParam, JPAQuery jpaQuery) {
        jpaQuery.limit(searchParam.limit);
        jpaQuery.offset(searchParam.offset);
    }

    protected SearchResults<ComputerToDesktopSessionGridSearchResult> listSearchResults(QComputer $record, ComputerToDesktopSessionGridSearchParam searchParam, JPAQuery jpaQuery) {
        return jpaQuery.listResults(ConstructorExpression.create(ComputerToDesktopSessionGridSearchResult.class, $record.id, $record.name, $record.user.id));
    }

    public SearchResults<ComputerForConnectedUserGridSearchResult> search(ComputerForConnectedUserGridSearchParam searchParam) {
        JPAQuery jpaQuery = createJPAQuery();
        QComputer $record = new QComputer("computer");
        jpaQuery.from($record);
        applyQueryAndFiltersAndSorting($record, searchParam, jpaQuery);
        applyLimitAndOffset(searchParam, jpaQuery);
        return listSearchResults($record, searchParam, jpaQuery);
    }

    protected void applyQueryAndFiltersAndSorting(QComputer $record, ComputerForConnectedUserGridSearchParam searchParam, JPAQuery jpaQuery) {
        // Applying static query
        jpaQuery.where($record.user.id.eq(connectedUser.getUserId()));
        // Applying filter
        ComputerForConnectedUserGridSearchFilter filter = searchParam.filter;
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

    protected void applyLimitAndOffset(ComputerForConnectedUserGridSearchParam searchParam, JPAQuery jpaQuery) {
        jpaQuery.limit(searchParam.limit);
        jpaQuery.offset(searchParam.offset);
    }

    protected SearchResults<ComputerForConnectedUserGridSearchResult> listSearchResults(QComputer $record, ComputerForConnectedUserGridSearchParam searchParam, JPAQuery jpaQuery) {
        return jpaQuery.listResults(ConstructorExpression.create(ComputerForConnectedUserGridSearchResult.class, $record.name));
    }

    public SearchResults<ComputerToCurrentUserDesktopSessionGridSearchResult> search(ComputerToCurrentUserDesktopSessionGridSearchParam searchParam) {
        JPAQuery jpaQuery = createJPAQuery();
        QComputer $record = new QComputer("computer");
        jpaQuery.from($record);
        applyQueryAndFiltersAndSorting($record, searchParam, jpaQuery);
        applyLimitAndOffset(searchParam, jpaQuery);
        return listSearchResults($record, searchParam, jpaQuery);
    }

    protected void applyQueryAndFiltersAndSorting(QComputer $record, ComputerToCurrentUserDesktopSessionGridSearchParam searchParam, JPAQuery jpaQuery) {
        // Applying filter
        ComputerToCurrentUserDesktopSessionGridSearchFilter filter = searchParam.filter;
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

    protected void applyLimitAndOffset(ComputerToCurrentUserDesktopSessionGridSearchParam searchParam, JPAQuery jpaQuery) {
        jpaQuery.limit(searchParam.limit);
        jpaQuery.offset(searchParam.offset);
    }

    protected SearchResults<ComputerToCurrentUserDesktopSessionGridSearchResult> listSearchResults(QComputer $record, ComputerToCurrentUserDesktopSessionGridSearchParam searchParam, JPAQuery jpaQuery) {
        return jpaQuery.listResults(ConstructorExpression.create(ComputerToCurrentUserDesktopSessionGridSearchResult.class, $record.name, $record.id));
    }


}
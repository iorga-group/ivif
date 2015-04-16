package com.iorga.ivif.test.service;

import com.iorga.ivif.ja.EntityBaseService;
import com.iorga.ivif.ja.EntityBaseService.SearchState;
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
import java.util.List;
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



    protected class ComputerGridSearchState extends SearchState<QComputer, ComputerGridSearchParam> {
        protected ComputerGridSearchState(ComputerGridSearchParam searchParam) {
            super(new QComputer("computer"), searchParam);
        }
    }

    public SearchResults<ComputerGridSearchResult> search(ComputerGridSearchParam searchParam) {
        ComputerGridSearchState searchState = new ComputerGridSearchState(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return listSearchResults(searchState);
    }

    protected void applyQueryAndFiltersAndSorting(ComputerGridSearchState searchState) {
        QComputer $record = searchState.$record;
        ComputerGridSearchParam searchParam = searchState.searchParam;
        JPAQuery jpaQuery = searchState.jpaQuery;
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
        } else {
            // default sorting
            jpaQuery.orderBy($record.name.asc());
        }
    }

    protected SearchResults<ComputerGridSearchResult> listSearchResults(ComputerGridSearchState searchState) {
        return searchState.jpaQuery.listResults(listExpression(searchState));
    }

    protected ConstructorExpression<ComputerGridSearchResult> listExpression(ComputerGridSearchState searchState) {
        QComputer $record = searchState.$record;
        return ConstructorExpression.create(ComputerGridSearchResult.class, $record.name, $record.user.name, $record.user.id);
    }

    public List<ComputerGridSearchResult> find(ComputerGridSearchParam searchParam) {
        ComputerGridSearchState searchState = new ComputerGridSearchState(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return list(searchState);
    }

    protected List<ComputerGridSearchResult> list(ComputerGridSearchState searchState) {
        return searchState.jpaQuery.list(listExpression(searchState));
    }


    protected class ComputerToDesktopSessionGridSearchState extends SearchState<QComputer, ComputerToDesktopSessionGridSearchParam> {
        protected ComputerToDesktopSessionGridSearchState(ComputerToDesktopSessionGridSearchParam searchParam) {
            super(new QComputer("computer"), searchParam);
        }
    }

    public SearchResults<ComputerToDesktopSessionGridSearchResult> search(ComputerToDesktopSessionGridSearchParam searchParam) {
        ComputerToDesktopSessionGridSearchState searchState = new ComputerToDesktopSessionGridSearchState(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return listSearchResults(searchState);
    }

    protected void applyQueryAndFiltersAndSorting(ComputerToDesktopSessionGridSearchState searchState) {
        QComputer $record = searchState.$record;
        ComputerToDesktopSessionGridSearchParam searchParam = searchState.searchParam;
        JPAQuery jpaQuery = searchState.jpaQuery;
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

    protected SearchResults<ComputerToDesktopSessionGridSearchResult> listSearchResults(ComputerToDesktopSessionGridSearchState searchState) {
        return searchState.jpaQuery.listResults(listExpression(searchState));
    }

    protected ConstructorExpression<ComputerToDesktopSessionGridSearchResult> listExpression(ComputerToDesktopSessionGridSearchState searchState) {
        QComputer $record = searchState.$record;
        return ConstructorExpression.create(ComputerToDesktopSessionGridSearchResult.class, $record.id, $record.name, $record.user.id);
    }

    public List<ComputerToDesktopSessionGridSearchResult> find(ComputerToDesktopSessionGridSearchParam searchParam) {
        ComputerToDesktopSessionGridSearchState searchState = new ComputerToDesktopSessionGridSearchState(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return list(searchState);
    }

    protected List<ComputerToDesktopSessionGridSearchResult> list(ComputerToDesktopSessionGridSearchState searchState) {
        return searchState.jpaQuery.list(listExpression(searchState));
    }


    protected class ComputerForConnectedUserGridSearchState extends SearchState<QComputer, ComputerForConnectedUserGridSearchParam> {
        protected ComputerForConnectedUserGridSearchState(ComputerForConnectedUserGridSearchParam searchParam) {
            super(new QComputer("computer"), searchParam);
        }
    }

    public SearchResults<ComputerForConnectedUserGridSearchResult> search(ComputerForConnectedUserGridSearchParam searchParam) {
        ComputerForConnectedUserGridSearchState searchState = new ComputerForConnectedUserGridSearchState(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return listSearchResults(searchState);
    }

    protected void applyQueryAndFiltersAndSorting(ComputerForConnectedUserGridSearchState searchState) {
        QComputer $record = searchState.$record;
        ComputerForConnectedUserGridSearchParam searchParam = searchState.searchParam;
        JPAQuery jpaQuery = searchState.jpaQuery;
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
        } else {
            // default sorting
            jpaQuery.orderBy($record.name.desc(), $record.user.name.asc());
        }
    }

    protected SearchResults<ComputerForConnectedUserGridSearchResult> listSearchResults(ComputerForConnectedUserGridSearchState searchState) {
        return searchState.jpaQuery.listResults(listExpression(searchState));
    }

    protected ConstructorExpression<ComputerForConnectedUserGridSearchResult> listExpression(ComputerForConnectedUserGridSearchState searchState) {
        QComputer $record = searchState.$record;
        return ConstructorExpression.create(ComputerForConnectedUserGridSearchResult.class, $record.name);
    }

    public List<ComputerForConnectedUserGridSearchResult> find(ComputerForConnectedUserGridSearchParam searchParam) {
        ComputerForConnectedUserGridSearchState searchState = new ComputerForConnectedUserGridSearchState(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return list(searchState);
    }

    protected List<ComputerForConnectedUserGridSearchResult> list(ComputerForConnectedUserGridSearchState searchState) {
        return searchState.jpaQuery.list(listExpression(searchState));
    }


    protected class ComputerToCurrentUserDesktopSessionGridSearchState extends SearchState<QComputer, ComputerToCurrentUserDesktopSessionGridSearchParam> {
        protected ComputerToCurrentUserDesktopSessionGridSearchState(ComputerToCurrentUserDesktopSessionGridSearchParam searchParam) {
            super(new QComputer("computer"), searchParam);
        }
    }

    public SearchResults<ComputerToCurrentUserDesktopSessionGridSearchResult> search(ComputerToCurrentUserDesktopSessionGridSearchParam searchParam) {
        ComputerToCurrentUserDesktopSessionGridSearchState searchState = new ComputerToCurrentUserDesktopSessionGridSearchState(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return listSearchResults(searchState);
    }

    protected void applyQueryAndFiltersAndSorting(ComputerToCurrentUserDesktopSessionGridSearchState searchState) {
        QComputer $record = searchState.$record;
        ComputerToCurrentUserDesktopSessionGridSearchParam searchParam = searchState.searchParam;
        JPAQuery jpaQuery = searchState.jpaQuery;
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

    protected SearchResults<ComputerToCurrentUserDesktopSessionGridSearchResult> listSearchResults(ComputerToCurrentUserDesktopSessionGridSearchState searchState) {
        return searchState.jpaQuery.listResults(listExpression(searchState));
    }

    protected ConstructorExpression<ComputerToCurrentUserDesktopSessionGridSearchResult> listExpression(ComputerToCurrentUserDesktopSessionGridSearchState searchState) {
        QComputer $record = searchState.$record;
        return ConstructorExpression.create(ComputerToCurrentUserDesktopSessionGridSearchResult.class, $record.name, $record.id);
    }

    public List<ComputerToCurrentUserDesktopSessionGridSearchResult> find(ComputerToCurrentUserDesktopSessionGridSearchParam searchParam) {
        ComputerToCurrentUserDesktopSessionGridSearchState searchState = new ComputerToCurrentUserDesktopSessionGridSearchState(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return list(searchState);
    }

    protected List<ComputerToCurrentUserDesktopSessionGridSearchResult> list(ComputerToCurrentUserDesktopSessionGridSearchState searchState) {
        return searchState.jpaQuery.list(listExpression(searchState));
    }


}
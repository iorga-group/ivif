package com.iorga.ivif.test.service;

import com.iorga.ivif.ja.EntityBaseService;
import com.iorga.ivif.ja.EntityBaseService.SearchState;
import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.RolesAllowed;
import com.iorga.ivif.ja.Sorting;
import com.iorga.ivif.ja.SortingType;
import com.iorga.ivif.test.entity.QProfile;
import com.iorga.ivif.test.entity.QUser;
import com.iorga.ivif.test.entity.User;
import com.iorga.ivif.test.ws.EditableUserGridBaseWS.EditableUserGridSearchFilter;
import com.iorga.ivif.test.ws.EditableUserGridBaseWS.EditableUserGridSearchParam;
import com.iorga.ivif.test.ws.EditableUserGridBaseWS.EditableUserGridSearchResult;
import com.iorga.ivif.test.ws.LeftJoinUserGridBaseWS.LeftJoinUserGridSearchFilter;
import com.iorga.ivif.test.ws.LeftJoinUserGridBaseWS.LeftJoinUserGridSearchParam;
import com.iorga.ivif.test.ws.LeftJoinUserGridBaseWS.LeftJoinUserGridSearchResult;
import com.iorga.ivif.test.ws.SelectEditableAndButtonUserGridBaseWS.SelectEditableAndButtonUserGridSearchFilter;
import com.iorga.ivif.test.ws.SelectEditableAndButtonUserGridBaseWS.SelectEditableAndButtonUserGridSearchParam;
import com.iorga.ivif.test.ws.SelectEditableAndButtonUserGridBaseWS.SelectEditableAndButtonUserGridSearchResult;
import com.iorga.ivif.test.ws.SpecificSearchUserGridBaseWS.SpecificSearchUserGridSearchFilter;
import com.iorga.ivif.test.ws.SpecificSearchUserGridBaseWS.SpecificSearchUserGridSearchParam;
import com.iorga.ivif.test.ws.SpecificSearchUserGridBaseWS.SpecificSearchUserGridSearchResult;
import com.iorga.ivif.test.ws.ToolbarUserGridBaseWS.ToolbarUserGridSearchFilter;
import com.iorga.ivif.test.ws.ToolbarUserGridBaseWS.ToolbarUserGridSearchParam;
import com.iorga.ivif.test.ws.ToolbarUserGridBaseWS.ToolbarUserGridSearchResult;
import com.iorga.ivif.test.ws.UserGridBaseWS.OpenUserGridFromComputer;
import com.iorga.ivif.test.ws.UserGridBaseWS.UserGridSearchFilter;
import com.iorga.ivif.test.ws.UserGridBaseWS.UserGridSearchParam;
import com.iorga.ivif.test.ws.UserGridBaseWS.UserGridSearchResult;
import com.mysema.query.SearchResults;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.ConstructorExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import java.lang.Integer;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;

@Generated
@Stateless
public class UserBaseService extends EntityBaseService<User, Integer> {

    @PersistenceContext
    protected EntityManager entityManager;


    /**
     * Apply pagination sort : sort on Id columns in order to paginate later
     **/
    protected void applyPaginationSort(SearchState<QUser, ?> searchParam) {
        searchParam.jpaQuery.orderBy(searchParam.$record.id.asc());
    }

    protected class UserGridSearchState extends SearchState<QUser, UserGridSearchParam> {
        protected UserGridSearchState(UserGridSearchParam searchParam) {
            super(new QUser("user"), searchParam);
        }
    }

    public SearchResults<UserGridSearchResult> search(UserGridSearchParam searchParam) {
        UserGridSearchState searchState = new UserGridSearchState(searchParam);
        applyFrom(searchState);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return listSearchResults(searchState);
    }

    protected void applyFrom(UserGridSearchState searchState) {
        searchState.jpaQuery.from(searchState.$record);
    }

    protected void applyQueryAndFiltersAndSorting(UserGridSearchState searchState) {
        QUser $record = searchState.$record;
        UserGridSearchParam searchParam = searchState.searchParam;
        JPAQuery jpaQuery = searchState.jpaQuery;
        // Applying filter
        UserGridSearchFilter filter = searchParam.filter;
        if (StringUtils.isNotEmpty(filter.name)) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
        }
        if (filter.profile_id != null) {
            jpaQuery.where($record.profile.id.like("%" + filter.profile_id + "%"));
        }
        // Applying action filters
        if (filter.openUserGridFromComputer != null) {
            OpenUserGridFromComputer parameters = filter.openUserGridFromComputer;
            jpaQuery.where($record.id.eq(parameters.userId));
        }
        // Applying sorting
        Sorting sorting = searchParam.sorting;
        ComparableExpressionBase sortingExpression = null;
        if (sorting != null) {
            if ("name".equals(sorting.ref)) {
                sortingExpression = $record.name;
            } else if ("profile_id".equals(sorting.ref)) {
                sortingExpression = $record.profile.id;
            }
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(SortingType.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        } else {
            // default sorting
            jpaQuery.orderBy($record.lastModification.asc());
        }
        applyPaginationSort(searchState);
    }

    protected SearchResults<UserGridSearchResult> listSearchResults(UserGridSearchState searchState) {
        return searchState.jpaQuery.listResults(listExpression(searchState));
    }

    protected ConstructorExpression<UserGridSearchResult> listExpression(UserGridSearchState searchState) {
        QUser $record = searchState.$record;
        return ConstructorExpression.create(UserGridSearchResult.class, $record.name, $record.profile.id);
    }

    public List<UserGridSearchResult> find(UserGridSearchParam searchParam) {
        UserGridSearchState searchState = new UserGridSearchState(searchParam);
        applyFrom(searchState);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return list(searchState);
    }

    protected List<UserGridSearchResult> list(UserGridSearchState searchState) {
        return searchState.jpaQuery.list(listExpression(searchState));
    }


    protected class LeftJoinUserGridSearchState extends SearchState<QUser, LeftJoinUserGridSearchParam> {
        protected QProfile profile = new QProfile("profile");

        protected LeftJoinUserGridSearchState(LeftJoinUserGridSearchParam searchParam) {
            super(new QUser("user"), searchParam);
        }
    }

    public SearchResults<LeftJoinUserGridSearchResult> search(LeftJoinUserGridSearchParam searchParam) {
        LeftJoinUserGridSearchState searchState = new LeftJoinUserGridSearchState(searchParam);
        applyFrom(searchState);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return listSearchResults(searchState);
    }

    protected void applyFrom(LeftJoinUserGridSearchState searchState) {
        searchState.jpaQuery.from(searchState.$record);
        searchState.jpaQuery.leftJoin(searchState.$record.profile, searchState.profile);
    }

    protected void applyQueryAndFiltersAndSorting(LeftJoinUserGridSearchState searchState) {
        QUser $record = searchState.$record;
        QProfile profile = searchState.profile;
        LeftJoinUserGridSearchParam searchParam = searchState.searchParam;
        JPAQuery jpaQuery = searchState.jpaQuery;
        // Applying filter
        LeftJoinUserGridSearchFilter filter = searchParam.filter;
        if (StringUtils.isNotEmpty(filter.name)) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
        }
        if (StringUtils.isNotEmpty(filter.__profile_name)) {
            jpaQuery.where(profile.name.containsIgnoreCase(filter.__profile_name));
        }
        // Applying action filters
        // Applying sorting
        Sorting sorting = searchParam.sorting;
        ComparableExpressionBase sortingExpression = null;
        if (sorting != null) {
            if ("name".equals(sorting.ref)) {
                sortingExpression = $record.name;
            } else if ("__profile_name".equals(sorting.ref)) {
                sortingExpression = profile.name;
            }
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(SortingType.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        }
        applyPaginationSort(searchState);
    }

    protected SearchResults<LeftJoinUserGridSearchResult> listSearchResults(LeftJoinUserGridSearchState searchState) {
        return searchState.jpaQuery.listResults(listExpression(searchState));
    }

    protected ConstructorExpression<LeftJoinUserGridSearchResult> listExpression(LeftJoinUserGridSearchState searchState) {
        QUser $record = searchState.$record;
        QProfile profile = searchState.profile;
        return ConstructorExpression.create(LeftJoinUserGridSearchResult.class, $record.name, profile.name, profile.description, profile.id);
    }

    public List<LeftJoinUserGridSearchResult> find(LeftJoinUserGridSearchParam searchParam) {
        LeftJoinUserGridSearchState searchState = new LeftJoinUserGridSearchState(searchParam);
        applyFrom(searchState);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return list(searchState);
    }

    protected List<LeftJoinUserGridSearchResult> list(LeftJoinUserGridSearchState searchState) {
        return searchState.jpaQuery.list(listExpression(searchState));
    }


    protected class EditableUserGridSearchState extends SearchState<QUser, EditableUserGridSearchParam> {
        protected EditableUserGridSearchState(EditableUserGridSearchParam searchParam) {
            super(new QUser("user"), searchParam);
        }
    }

    @RolesAllowed({"admin", "manager"})
    public SearchResults<EditableUserGridSearchResult> search(EditableUserGridSearchParam searchParam) {
        EditableUserGridSearchState searchState = new EditableUserGridSearchState(searchParam);
        applyFrom(searchState);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return listSearchResults(searchState);
    }

    protected void applyFrom(EditableUserGridSearchState searchState) {
        searchState.jpaQuery.from(searchState.$record);
    }

    protected void applyQueryAndFiltersAndSorting(EditableUserGridSearchState searchState) {
        QUser $record = searchState.$record;
        EditableUserGridSearchParam searchParam = searchState.searchParam;
        JPAQuery jpaQuery = searchState.jpaQuery;
        // Applying filter
        EditableUserGridSearchFilter filter = searchParam.filter;
        if (StringUtils.isNotEmpty(filter.firstName)) {
            jpaQuery.where($record.firstName.containsIgnoreCase(filter.firstName));
        }
        if (StringUtils.isNotEmpty(filter.name)) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
        }
        if (filter.status != null) {
            jpaQuery.where($record.status.eq(filter.status));
        }
        if (filter.enabled != null) {
            jpaQuery.where($record.enabled.eq(filter.enabled));
        }
        if (StringUtils.isNotEmpty(filter.bigComment)) {
            jpaQuery.where($record.bigComment.containsIgnoreCase(filter.bigComment));
        }
        if (filter.pass != null) {
            jpaQuery.where($record.pass.eq(filter.pass));
        }
        if (filter.lastModification != null) {
            jpaQuery.where($record.lastModification.eq(filter.lastModification));
        }
        // Applying action filters
        // Applying sorting
        Sorting sorting = searchParam.sorting;
        ComparableExpressionBase sortingExpression = null;
        if (sorting != null) {
            if ("firstName".equals(sorting.ref)) {
                sortingExpression = $record.firstName;
            } else if ("name".equals(sorting.ref)) {
                sortingExpression = $record.name;
            } else if ("status".equals(sorting.ref)) {
                sortingExpression = $record.status;
            } else if ("enabled".equals(sorting.ref)) {
                sortingExpression = $record.enabled;
            } else if ("bigComment".equals(sorting.ref)) {
                sortingExpression = $record.bigComment;
            } else if ("pass".equals(sorting.ref)) {
                sortingExpression = $record.pass;
            } else if ("lastModification".equals(sorting.ref)) {
                sortingExpression = $record.lastModification;
            }
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(SortingType.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        }
        applyPaginationSort(searchState);
    }

    protected SearchResults<EditableUserGridSearchResult> listSearchResults(EditableUserGridSearchState searchState) {
        return searchState.jpaQuery.listResults(listExpression(searchState));
    }

    protected ConstructorExpression<EditableUserGridSearchResult> listExpression(EditableUserGridSearchState searchState) {
        QUser $record = searchState.$record;
        return ConstructorExpression.create(EditableUserGridSearchResult.class, $record.firstName, $record.name, $record.status, $record.profile.description, $record.enabled, $record.bigComment, $record.pass, $record.lastModification, $record.profile.id, $record.profile.name, $record.id, $record.version);
    }

    @RolesAllowed({"admin", "manager"})
    public List<EditableUserGridSearchResult> find(EditableUserGridSearchParam searchParam) {
        EditableUserGridSearchState searchState = new EditableUserGridSearchState(searchParam);
        applyFrom(searchState);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return list(searchState);
    }

    protected List<EditableUserGridSearchResult> list(EditableUserGridSearchState searchState) {
        return searchState.jpaQuery.list(listExpression(searchState));
    }


    protected class ToolbarUserGridSearchState extends SearchState<QUser, ToolbarUserGridSearchParam> {
        protected ToolbarUserGridSearchState(ToolbarUserGridSearchParam searchParam) {
            super(new QUser("user"), searchParam);
        }
    }

    public SearchResults<ToolbarUserGridSearchResult> search(ToolbarUserGridSearchParam searchParam) {
        ToolbarUserGridSearchState searchState = new ToolbarUserGridSearchState(searchParam);
        applyFrom(searchState);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return listSearchResults(searchState);
    }

    protected void applyFrom(ToolbarUserGridSearchState searchState) {
        searchState.jpaQuery.from(searchState.$record);
    }

    protected void applyQueryAndFiltersAndSorting(ToolbarUserGridSearchState searchState) {
        QUser $record = searchState.$record;
        ToolbarUserGridSearchParam searchParam = searchState.searchParam;
        JPAQuery jpaQuery = searchState.jpaQuery;
        // Applying filter
        ToolbarUserGridSearchFilter filter = searchParam.filter;
        if (StringUtils.isNotEmpty(filter.name)) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
        }
        if (StringUtils.isNotEmpty(filter.firstName)) {
            jpaQuery.where($record.firstName.containsIgnoreCase(filter.firstName));
        }
        // Applying action filters
        // Applying sorting
        Sorting sorting = searchParam.sorting;
        ComparableExpressionBase sortingExpression = null;
        if (sorting != null) {
            if ("name".equals(sorting.ref)) {
                sortingExpression = $record.name;
            } else if ("comment".equals(sorting.ref)) {
                sortingExpression = $record.comment;
            }
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(SortingType.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        }
        applyPaginationSort(searchState);
    }

    protected SearchResults<ToolbarUserGridSearchResult> listSearchResults(ToolbarUserGridSearchState searchState) {
        return searchState.jpaQuery.listResults(listExpression(searchState));
    }

    protected ConstructorExpression<ToolbarUserGridSearchResult> listExpression(ToolbarUserGridSearchState searchState) {
        QUser $record = searchState.$record;
        return ConstructorExpression.create(ToolbarUserGridSearchResult.class, $record.name, $record.profile.name, $record.profile.id, $record.id);
    }

    public List<ToolbarUserGridSearchResult> find(ToolbarUserGridSearchParam searchParam) {
        ToolbarUserGridSearchState searchState = new ToolbarUserGridSearchState(searchParam);
        applyFrom(searchState);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return list(searchState);
    }

    protected List<ToolbarUserGridSearchResult> list(ToolbarUserGridSearchState searchState) {
        return searchState.jpaQuery.list(listExpression(searchState));
    }


    protected class SpecificSearchUserGridSearchState extends SearchState<QUser, SpecificSearchUserGridSearchParam> {
        protected SpecificSearchUserGridSearchState(SpecificSearchUserGridSearchParam searchParam) {
            super(new QUser("user"), searchParam);
        }
    }

    public SearchResults<SpecificSearchUserGridSearchResult> search(SpecificSearchUserGridSearchParam searchParam) {
        SpecificSearchUserGridSearchState searchState = new SpecificSearchUserGridSearchState(searchParam);
        applyFrom(searchState);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return listSearchResults(searchState);
    }

    protected void applyFrom(SpecificSearchUserGridSearchState searchState) {
        searchState.jpaQuery.from(searchState.$record);
    }

    protected void applyQueryAndFiltersAndSorting(SpecificSearchUserGridSearchState searchState) {
        QUser $record = searchState.$record;
        SpecificSearchUserGridSearchParam searchParam = searchState.searchParam;
        JPAQuery jpaQuery = searchState.jpaQuery;
        // Applying filter
        SpecificSearchUserGridSearchFilter filter = searchParam.filter;
        if (StringUtils.isNotEmpty(filter.name)) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
        }
        if (StringUtils.isNotEmpty(filter.firstName)) {
            jpaQuery.where($record.firstName.containsIgnoreCase(filter.firstName));
        }
        // Applying action filters
        // Applying sorting
        Sorting sorting = searchParam.sorting;
        ComparableExpressionBase sortingExpression = null;
        if (sorting != null) {
            if ("name".equals(sorting.ref)) {
                sortingExpression = $record.name;
            }
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(SortingType.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        }
        applyPaginationSort(searchState);
    }

    protected SearchResults<SpecificSearchUserGridSearchResult> listSearchResults(SpecificSearchUserGridSearchState searchState) {
        return searchState.jpaQuery.listResults(listExpression(searchState));
    }

    protected ConstructorExpression<SpecificSearchUserGridSearchResult> listExpression(SpecificSearchUserGridSearchState searchState) {
        QUser $record = searchState.$record;
        return ConstructorExpression.create(SpecificSearchUserGridSearchResult.class, $record.name, $record.profile.name, $record.id);
    }

    public List<SpecificSearchUserGridSearchResult> find(SpecificSearchUserGridSearchParam searchParam) {
        SpecificSearchUserGridSearchState searchState = new SpecificSearchUserGridSearchState(searchParam);
        applyFrom(searchState);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return list(searchState);
    }

    protected List<SpecificSearchUserGridSearchResult> list(SpecificSearchUserGridSearchState searchState) {
        return searchState.jpaQuery.list(listExpression(searchState));
    }


    protected class SelectEditableAndButtonUserGridSearchState extends SearchState<QUser, SelectEditableAndButtonUserGridSearchParam> {
        protected SelectEditableAndButtonUserGridSearchState(SelectEditableAndButtonUserGridSearchParam searchParam) {
            super(new QUser("user"), searchParam);
        }
    }

    public SearchResults<SelectEditableAndButtonUserGridSearchResult> search(SelectEditableAndButtonUserGridSearchParam searchParam) {
        SelectEditableAndButtonUserGridSearchState searchState = new SelectEditableAndButtonUserGridSearchState(searchParam);
        applyFrom(searchState);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return listSearchResults(searchState);
    }

    protected void applyFrom(SelectEditableAndButtonUserGridSearchState searchState) {
        searchState.jpaQuery.from(searchState.$record);
    }

    protected void applyQueryAndFiltersAndSorting(SelectEditableAndButtonUserGridSearchState searchState) {
        QUser $record = searchState.$record;
        SelectEditableAndButtonUserGridSearchParam searchParam = searchState.searchParam;
        JPAQuery jpaQuery = searchState.jpaQuery;
        // Applying filter
        SelectEditableAndButtonUserGridSearchFilter filter = searchParam.filter;
        if (StringUtils.isNotEmpty(filter.name)) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
        }
        if (filter.id != null) {
            jpaQuery.where($record.id.like("%" + filter.id + "%"));
        }
        // Applying action filters
        // Applying sorting
        Sorting sorting = searchParam.sorting;
        ComparableExpressionBase sortingExpression = null;
        if (sorting != null) {
            if ("name".equals(sorting.ref)) {
                sortingExpression = $record.name;
            } else if ("id".equals(sorting.ref)) {
                sortingExpression = $record.id;
            }
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(SortingType.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        }
        applyPaginationSort(searchState);
    }

    protected SearchResults<SelectEditableAndButtonUserGridSearchResult> listSearchResults(SelectEditableAndButtonUserGridSearchState searchState) {
        return searchState.jpaQuery.listResults(listExpression(searchState));
    }

    protected ConstructorExpression<SelectEditableAndButtonUserGridSearchResult> listExpression(SelectEditableAndButtonUserGridSearchState searchState) {
        QUser $record = searchState.$record;
        return ConstructorExpression.create(SelectEditableAndButtonUserGridSearchResult.class, $record.name, $record.id, $record.firstName, $record.profile.name, $record.version);
    }

    public List<SelectEditableAndButtonUserGridSearchResult> find(SelectEditableAndButtonUserGridSearchParam searchParam) {
        SelectEditableAndButtonUserGridSearchState searchState = new SelectEditableAndButtonUserGridSearchState(searchParam);
        applyFrom(searchState);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return list(searchState);
    }

    protected List<SelectEditableAndButtonUserGridSearchResult> list(SelectEditableAndButtonUserGridSearchState searchState) {
        return searchState.jpaQuery.list(listExpression(searchState));
    }


}
package com.iorga.ivif.test.service;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.Sorting;
import com.iorga.ivif.ja.SortingType;
import com.iorga.ivif.ja.test.ConnectedUser;
import com.iorga.ivif.test.entity.DesktopSession;
import com.iorga.ivif.test.entity.DesktopSession.DesktopSessionId;
import com.iorga.ivif.test.entity.QDesktopSession;
import com.iorga.ivif.test.ws.DesktopSessionGridBaseWS.DesktopSessionGridSearchFilter;
import com.iorga.ivif.test.ws.DesktopSessionGridBaseWS.DesktopSessionGridSearchParam;
import com.iorga.ivif.test.ws.DesktopSessionGridBaseWS.DesktopSessionGridSearchResult;
import com.iorga.ivif.test.ws.DesktopSessionGridBaseWS.OpenCurrentUserDesktopSessionGridFromComputer;
import com.iorga.ivif.test.ws.DesktopSessionGridBaseWS.OpenDesktopSessionGridFromComputer;
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
public class DesktopSessionBaseService {
    @PersistenceContext
    protected EntityManager entityManager;

    @Inject
    protected ConnectedUser connectedUser;



    public DesktopSession find(Integer userId, Integer computerId) {
        return find(new DesktopSessionId(userId, computerId));
    }

    public DesktopSession find(DesktopSessionId id) {
        return entityManager.find(DesktopSession.class, id);
    }

    public boolean isNew(DesktopSession entity) {
        return entity.getUserId() == null || entity.getComputerId() == null;
    }

    @TransactionAttribute
    public DesktopSession save(DesktopSession entityToSave) {
        return save(entityToSave, true);
    }

    @TransactionAttribute
    protected DesktopSession save(DesktopSession entityToSave, boolean flush) {
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
    public List<DesktopSession> save(List<DesktopSession> entitiesToSave) {
        for (DesktopSession entityToSave : entitiesToSave) {
            save(entityToSave, false);
        }
        entityManager.flush();
        return entitiesToSave;
    }

    public void detach(DesktopSession entity) {
        entityManager.detach(entity);
    }

    public SearchResults<DesktopSessionGridSearchResult> search(DesktopSessionGridSearchParam searchParam) {
        JPAQuery jpaQuery = new JPAQuery(entityManager, JPQLTemplates.DEFAULT);
        QDesktopSession $record = new QDesktopSession("desktopSession");
        jpaQuery.from($record);
        // Applying filter
        DesktopSessionGridSearchFilter filter = searchParam.filter;
        if (filter.computerId != null) {
            jpaQuery.where($record.computerId.eq(filter.computerId));
        }
        if (filter.name != null) {
            jpaQuery.where($record.name.containsIgnoreCase(filter.name));
        }
        // Applying action filters
        if (filter.openDesktopSessionGridFromComputer != null) {
            OpenDesktopSessionGridFromComputer parameters = filter.openDesktopSessionGridFromComputer;
            jpaQuery.where($record.userId.eq(parameters.userId).and($record.computerId.eq(parameters.computerId)));
        }
        // Applying action filters
        if (filter.openCurrentUserDesktopSessionGridFromComputer != null) {
            OpenCurrentUserDesktopSessionGridFromComputer parameters = filter.openCurrentUserDesktopSessionGridFromComputer;
            jpaQuery.where($record.userId.eq(connectedUser.getUserId()).and($record.computerId.eq(parameters.computerId)));
        }
        // Applying sorting
        Sorting sorting = searchParam.sorting;
        ComparableExpressionBase sortingExpression = null;
        if ("computerId".equals(sorting.ref)) {
            sortingExpression = $record.computerId;
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
        return jpaQuery.listResults(ConstructorExpression.create(DesktopSessionGridSearchResult.class, $record.computerId, $record.name));
    }


}
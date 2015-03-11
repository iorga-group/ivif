package com.iorga.ivif.ja;

import com.google.common.reflect.TypeToken;
import com.mysema.query.jpa.JPQLTemplates;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.Ops;
import com.mysema.query.types.expr.BooleanExpression;

import javax.ejb.TransactionAttribute;
import javax.persistence.NonUniqueResultException;
import java.util.List;

public abstract class EntityBaseService<E extends IEntity<I>, I> extends PersistenceService {
    public static final BooleanExpression FALSE_PREDICATE = Expressions.predicate(Ops.EQ, Expressions.constant(0), Expressions.constant(1));
    public static final BooleanExpression TRUE_PREDICATE = Expressions.predicate(Ops.EQ, Expressions.constant(1), Expressions.constant(1));

    protected Class<E> entityClass;

    {
        final TypeToken<E> entityTypeToken = new TypeToken<E>(getClass()) {};
        entityClass = (Class<E>) entityTypeToken.getRawType();
    }


    public E find(I id) {
        return entityManager.find(entityClass, id);
    }

    public boolean isNew(E entity) {
        return entity.entityId() == null;
    }

    @TransactionAttribute
    public E save(E entityToSave) {
        return save(entityToSave, true);
    }

    @TransactionAttribute
    protected E save(E entityToSave, boolean flush) {
        if (isNew(entityToSave)) {
            entityToSave = create(entityToSave);
        } else {
            entityToSave = update(entityToSave);
        }
        if (flush) {
            entityManager.flush();
        }
        return entityToSave;
    }

    @TransactionAttribute
    protected E create(E entity) {
        entityManager.persist(entity);
        return entity;
    }

    @TransactionAttribute
    protected E update(E entity) {
        return entityManager.merge(entity);
    }

    @TransactionAttribute
    public List<E> save(List<E> entitiesToSave) {
        for (E entityToSave : entitiesToSave) {
            save(entityToSave, false);
        }
        entityManager.flush();
        return entitiesToSave;
    }

    public void detach(E entity) {
        entityManager.detach(entity);
    }

    protected E toSingleResult(List<E> list) {
        if (list != null) {
            if (list.size() > 1) {
              throw new NonUniqueResultException("Query produced " + list.size() + " results (expected 1)");
            } else if (!list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    protected JPAQuery createJPAQuery() {
        return new JPAQuery(entityManager);
    }
}

package com.iorga.ivif.ja;

import com.google.common.reflect.TypeToken;

import javax.ejb.TransactionAttribute;
import java.util.List;

public abstract class EntityBaseService<E, I> extends PersistenceService {

    protected Class<E> entityClass;

    {
        final TypeToken<E> entityTypeToken = new TypeToken<E>(getClass()) {};
        entityClass = (Class<E>) entityTypeToken.getRawType();
    }


    public E find(I id) {
        return entityManager.find(entityClass, id);
    }

    protected abstract I getId(E entity);

    protected abstract void setId(E entity, I id);

    public boolean isNew(E entity) {
        return getId(entity) == null;
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
}
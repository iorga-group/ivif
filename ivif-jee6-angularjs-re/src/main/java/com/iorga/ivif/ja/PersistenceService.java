package com.iorga.ivif.ja;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class PersistenceService {

    @PersistenceContext
    protected EntityManager entityManager;

}

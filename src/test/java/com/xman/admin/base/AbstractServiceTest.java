package com.xman.admin.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
public abstract class AbstractServiceTest {

    @Autowired
    protected EntityManager em;

    protected void initEm() {
        em.flush();
        em.clear();
    }

}

package com.minh.shopee.services.repocustom;


import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;

import com.minh.shopee.repository.GenericRepositoryCustom;

public class GenericRepositoryImpl<T> implements GenericRepositoryCustom<T> {

    @PersistenceContext
    private EntityManager entityManager;

    private final Class<T> domainClass;

    public GenericRepositoryImpl(Class<T> domainClass) {
        this.domainClass = domainClass;
    }

    @Override
    public <R> Page<R> findAll(Specification<T> spec, Pageable pageable, Class<R> projection) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<R> cq = cb.createQuery(projection);
        Root<T> root = cq.from(domainClass);

        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, cq, cb);
            if (predicate != null) {
                cq.where(predicate);
            }
        }

        if (projection.equals(domainClass)) {
            @SuppressWarnings("unchecked")
            Selection<? extends R> selection = (Selection<? extends R>) root;
            cq.select(selection);
        } else {
            try {
                Constructor<?> constructor = projection.getConstructors()[0];
                Parameter[] parameters = constructor.getParameters();

                List<Selection<?>> selections = new ArrayList<>();
                for (Parameter param : parameters) {
                    String paramName = param.getName();
                    selections.add(root.get(paramName));
                }

                cq.select(cb.construct(projection, selections.toArray(new Selection[0])));
            } catch (Exception e) {
                throw new RuntimeException("Failed to build projection constructor query", e);
            }
        }

        if (pageable.getSort().isSorted()) {
            cq.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));
        }

        TypedQuery<R> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<R> content = query.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(domainClass);
        countQuery.select(cb.count(countRoot));
        if (spec != null) {
            Predicate predicate = spec.toPredicate(countRoot, countQuery, cb);
            if (predicate != null) {
                countQuery.where(predicate);
            }
        }
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}

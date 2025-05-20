package com.minh.shopee.repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;

import com.minh.shopee.domain.model.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

public class ProductRepositoryImpl implements GenericRepositoryCustom<Product> {

    @PersistenceContext
    private EntityManager entityManager;

    private final Class<Product> domainClass = Product.class;

    public ProductRepositoryImpl() {
    }

    @Override
    public <R> Page<R> findAll(Specification<Product> spec, Pageable pageable, Class<R> projection) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<R> cq = cb.createQuery(projection);
        Root<Product> root = cq.from(domainClass);

        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, cq, cb);
            if (predicate != null) {
                cq.where(predicate);
            }
        }

        if (projection.equals(domainClass)) {
            // Nếu trả về entity đầy đủ thì select root
            @SuppressWarnings("unchecked")
            Selection<? extends R> selection = (Selection<? extends R>) root;
            cq.select(selection);
        } else {
            try {
                Constructor<?> constructor = projection.getConstructors()[0];
                Parameter[] parameters = constructor.getParameters();

                List<Selection<?>> selections = new ArrayList<>();
                for (Parameter param : parameters) {
                    String paramName = param.getName(); // tên tham số constructor
                    selections.add(root.get(paramName)); // lấy trường tương ứng trong entity
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

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(domainClass);
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

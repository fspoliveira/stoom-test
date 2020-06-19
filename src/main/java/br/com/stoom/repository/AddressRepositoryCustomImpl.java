package br.com.stoom.repository;

import br.com.stoom.entity.Address;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class AddressRepositoryCustomImpl implements AddressRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Address> findBy(String field, Object value) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Address> query = criteriaBuilder.createQuery(Address.class);
        Root<Address> address = query.from(Address.class);
        Path<String> fieldPath = address.get(field);
        Predicate upperFieldLike = criteriaBuilder.like(criteriaBuilder.upper(fieldPath), "%" + String.valueOf(value).toUpperCase() + "%");
        query.select(address)
            .where(criteriaBuilder.and(upperFieldLike));
        return entityManager.createQuery(query).getResultList();
    }
}
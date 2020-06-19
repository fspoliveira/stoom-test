package br.com.stoom.repository;

import br.com.stoom.entity.Address;

import java.util.List;

public interface AddressRepositoryCustom {

    List<Address> findBy(String field, Object value);
}

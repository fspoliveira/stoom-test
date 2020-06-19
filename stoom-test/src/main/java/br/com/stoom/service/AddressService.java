package br.com.stoom.service;

import br.com.stoom.entity.Address;
import br.com.stoom.exception.AddressNotFoundException;
import br.com.stoom.google.service.LatitudeLongitudeService;
import br.com.stoom.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressService.class);
    private LatitudeLongitudeService latLonService;
    private AddressRepository repository;

    @Autowired
    public AddressService(AddressRepository repository, LatitudeLongitudeService latLonService) {
        this.repository = repository;
        this.latLonService = latLonService;
    }

    public Address findById(UUID id) {
        return repository.findById(id).orElseThrow(AddressNotFoundException::new);
    }

    public List<Address> findAll() {
        return repository.findAll();
    }

    public List<Address> findBy(String filterField, Object filterString) {
        return repository.findBy(filterField, filterString);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Address update(Address address) {
        log.debug("Updating {}", address);
        repository.findById(address.getId()).orElseThrow(AddressNotFoundException::new);
        return repository.save(handleLatitudeAndLongitude(address));
    }

    public Address save(Address address) {
        log.info("Creating {}", address);
        return repository.save(handleLatitudeAndLongitude(address));
    }

    private Address handleLatitudeAndLongitude(Address address) {
        log.info("Handling Lat Lon.");
        return isEmpty(address.getLatitude()) || isEmpty(address.getLongitude()) ?
            latLonService.findLatitudeAndLongitude(address) :
            address;
    }
}

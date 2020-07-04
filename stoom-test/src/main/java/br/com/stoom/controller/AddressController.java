package br.com.stoom.controller;

import br.com.stoom.entity.Address;
import br.com.stoom.model.AddressModel;
import br.com.stoom.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static br.com.stoom.entity.Address.fromModel;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/address")
@Slf4j
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public ResponseEntity<List<Address>> getAllAddresses() {
        return ResponseEntity.ok(addressService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressModel> getAddressById(@PathVariable("id") String id) {
        log.info("Finding Address with id {}", id);
        return ResponseEntity.ok(addressService.findById(UUID.fromString(id)).toModel());
    }

    @GetMapping(params = {"field", "value"})
    public ResponseEntity<List<AddressModel>> getAddressByField(@RequestParam(name = "field") String field, @RequestParam(name = "value") String value) {
        log.info("Finding Address using field {} as filter and {} as value", field, value);
        return ResponseEntity.ok(addressService.findBy(field, value).stream()
            .map(Address::toModel)
            .collect(toList()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddressById(@PathVariable("id") String id) {
        log.info("Deleting address with id {}", id);
        addressService.delete(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressModel> updateAddress(@PathVariable("id") String id, @Valid @RequestBody AddressModel addressModel) {
        log.info("Updating address with id {}", id);
        Address address = fromModel(addressModel).toBuilder().id(UUID.fromString(id)).build();
        return ResponseEntity.ok(addressService.update(address).toModel());
    }

    @PostMapping
    public ResponseEntity<AddressModel> updateAddress(@Valid @RequestBody AddressModel addressModel) {
        log.info("Updating address with id {}", addressModel);
        Address persisted = addressService.save(fromModel(addressModel));
        return ResponseEntity.created(URI.create("/api/address/" + persisted.getId().toString())).body(persisted.toModel());
    }
}

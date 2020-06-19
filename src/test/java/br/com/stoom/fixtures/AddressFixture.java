package br.com.stoom.fixtures;

import br.com.stoom.entity.Address;
import br.com.stoom.repository.AddressRepository;

import java.util.UUID;

public class AddressFixture {

    public static Address createSimpleData(AddressRepository addressRepository) {
        return addressRepository.save(aSimpleAddress());
    }

    public static Address aSimpleAddressWithoutLatLon() {
        return aSimpleAddress().toBuilder().latitude(null).longitude(null).build();
    }

    public static Address aSimpleAddressWithId(UUID uuid) {
        return aSimpleAddress().toBuilder().id(uuid).build();
    }

    public static Address aSimpleAddress() {
        return Address.builder()
            .streetName("Some Street name")
            .city("Some City")
            .country("BR")
            .latitude("-22.877083")
            .longitude("-47.048379")
            .neighbourhood("Some neighbourhood")
            .number(123)
            .state("Some State")
            .zipcode("13076-418")
            .complement("Some complement")
            .build();
    }

    public static Address aRealAddress() {
        return Address.builder()
            .streetName("R. Zuneide Aparecida Marin")
            .city("Campinas")
            .country("BR")
            .latitude("-22.877083")
            .longitude("-47.048379")
            .neighbourhood("Jardim Santa Genebra II (Barao Geraldo)")
            .number(43)
            .state("SP")
            .zipcode("13084-780")
            .build();
    }
}

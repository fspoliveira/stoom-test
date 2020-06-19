package br.com.stoom.entity;

import br.com.stoom.model.AddressModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @NonNull
    private String streetName;
    @NonNull
    private Integer number;
    private String complement;
    @NonNull
    private String neighbourhood;
    @NonNull
    private String city;
    @NonNull
    private String state;
    @NonNull
    private String country;
    @NonNull
    private String zipcode;
    private String latitude;
    private String longitude;

    public static Address fromModel(AddressModel addressModel) {
        return Address.builder()
            .longitude(addressModel.getLongitude())
            .latitude(addressModel.getLatitude())
            .city(addressModel.getCity())
            .country(addressModel.getCountry())
            .neighbourhood(addressModel.getNeighbourhood())
            .state(addressModel.getState())
            .zipcode(addressModel.getZipcode())
            .complement(addressModel.getComplement())
            .number(Integer.parseInt(addressModel.getNumber()))
            .streetName(addressModel.getStreetName())
            .build();
    }

    public AddressModel toModel() {
        return AddressModel.builder()
            .zipcode(this.zipcode)
            .streetName(this.streetName)
            .city(this.city)
            .latitude(this.latitude)
            .longitude(this.longitude)
            .country(this.country)
            .neighbourhood(this.neighbourhood)
            .number(this.city)
            .state(this.state)
            .complement(this.complement)
            .build();
    }

    public String hashedObject() throws NoSuchAlgorithmException {
        return new String(MessageDigest.getInstance("MD5").digest(this.toString().getBytes()));
    }
}


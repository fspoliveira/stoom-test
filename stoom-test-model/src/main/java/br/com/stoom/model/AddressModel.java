package br.com.stoom.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder(builderClassName = "AddressModelBuilder", toBuilder = true)
@JsonDeserialize(builder = AddressModel.AddressModelBuilder.class)
public class AddressModel {

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

    @JsonPOJOBuilder(withPrefix = "")
    public static class AddressModelBuilder {

    }
}

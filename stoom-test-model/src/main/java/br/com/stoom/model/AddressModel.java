package br.com.stoom.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder(builderClassName = "AddressModelBuilder", toBuilder = true)
@JsonDeserialize(builder = AddressModel.AddressModelBuilder.class)
public class AddressModel {

    @NotNull private String streetName;
    @Pattern(regexp = "[\\s]*[0-9]*[1-9]+") private String number;
    private String complement;
    @NotNull private String neighbourhood;
    @NotNull private String city;
    @NotNull @Size(min = 2, max = 2) private String state;
    @NotNull private String country;
    @NotNull private String zipcode;
    private String latitude;
    private String longitude;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AddressModelBuilder {

    }
}

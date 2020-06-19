package br.com.stoom.google.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "LocationBuilder", toBuilder = true)
@JsonDeserialize(builder = Location.LocationBuilder.class)
public class Location {

    @JsonProperty("lat")
    private String latitude;
    @JsonProperty("lng")
    private String longitude;

    @JsonPOJOBuilder(withPrefix = "")
    public static class LocationBuilder {

    }
}

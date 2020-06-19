package br.com.stoom.google.service.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(builderClassName = "GeocodingResponseBuilder", toBuilder = true)
@JsonDeserialize(builder = GeocodingResponse.GeocodingResponseBuilder.class)
public class GeocodingResponse {

    private List<Result> results;

    @JsonPOJOBuilder(withPrefix = "")
    public static class GeocodingResponseBuilder {

    }
}

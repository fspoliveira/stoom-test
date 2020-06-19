package br.com.stoom.google.service.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "ResultBuilder", toBuilder = true)
@JsonDeserialize(builder = Result.ResultBuilder.class)
public class Result {

    private Geometry geometry;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ResultBuilder {

    }
}

package br.com.stoom.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "NotFoundResponseModelBuilder", toBuilder = true)
@JsonDeserialize(builder = ErrorModel.NotFoundResponseModelBuilder.class)
public class ErrorModel {

    private String message;

    @JsonPOJOBuilder(withPrefix = "")
    public static class NotFoundResponseModelBuilder {

    }
}

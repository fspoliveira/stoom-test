package br.com.stoom.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "stoom.google.api")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleGeocodingApiProperties {

    private String apiKey;
    private String baseUrl;
}

package br.com.stoom.google.service;

import br.com.stoom.configuration.GoogleGeocodingApiProperties;
import br.com.stoom.entity.Address;
import br.com.stoom.exception.GoogleApiInvalidAddressInformation;
import br.com.stoom.google.service.model.GeocodingResponse;
import br.com.stoom.google.service.model.Geometry;
import br.com.stoom.google.service.model.Result;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

import static java.lang.String.format;

@Service
public class LatitudeLongitudeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GoogleGeocodingApiProperties apiProperties;

    public LatitudeLongitudeService(RestTemplate restTemplate, GoogleGeocodingApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    @Cacheable(value = "findLatitudeAndLongitude", key = "#address.hashedObject()")
    public Address findLatitudeAndLongitude(Address address) {
        String formatedAddress = formatAddress(address);
        GeocodingResponse geocodingResponseEntity = consumeGoogleApi(formatedAddress);
        Pair<String, String> latitudeAndLongitude =
            getLatitudeAndLongitude(Objects.requireNonNull(geocodingResponseEntity));
        return address.toBuilder()
            .latitude(latitudeAndLongitude.getLeft())
            .longitude(latitudeAndLongitude.getRight())
            .build();
    }

    private String formatAddress(Address address) {
        return format(
            "%s, %s. %s - %s, %s. %s",
            address.getStreetName(),
            address.getNumber(),
            address.getCity(),
            address.getState(),
            address.getCountry(),
            address.getZipcode()).replaceAll(" ", "+");
    }

    private GeocodingResponse consumeGoogleApi(String formatedAddress) {
        ResponseEntity<GeocodingResponse> geocodingResponseEntity =
            restTemplate.getForEntity(buildUri(formatedAddress), GeocodingResponse.class);
        if (geocodingResponseEntity.getStatusCode().isError()) {
            throw new GoogleApiInvalidAddressInformation();
        }
        return geocodingResponseEntity.getBody();
    }

    private String buildUri(String formatedAddress) {
        return UriComponentsBuilder.fromHttpUrl(apiProperties.getBaseUrl())
            .queryParam("address", formatedAddress)
            .queryParam("key", apiProperties.getApiKey())
            .toUriString();
    }

    private Pair<String, String> getLatitudeAndLongitude(GeocodingResponse geocodingResponse) {
        return geocodingResponse.getResults()
            .stream()
            .findFirst()
            .map(Result::getGeometry)
            .map(Geometry::getLocation)
            .map(location -> Pair.of(location.getLatitude(), location.getLongitude()))
            .orElseThrow(() -> new IllegalArgumentException("Failed to retrieve data from geocoding API"));
    }
}

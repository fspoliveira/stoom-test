package br.com.stoom.google.service;

import br.com.stoom.configuration.GoogleGeocodingApiProperties;
import br.com.stoom.entity.Address;
import br.com.stoom.exception.GoogleApiInvalidAddressInformation;
import br.com.stoom.google.service.model.GeocodingResponse;
import br.com.stoom.google.service.model.Geometry;
import br.com.stoom.google.service.model.Location;
import br.com.stoom.google.service.model.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static br.com.stoom.fixtures.AddressFixture.aRealAddress;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Lat / Lon Service testing")
class LatitudeLongitudeServiceTest {

    private RestTemplate mockedRestTemplate = mock(RestTemplate.class);
    private GoogleGeocodingApiProperties mockedApiProperties = mock(GoogleGeocodingApiProperties.class);
    private LatitudeLongitudeService service = new LatitudeLongitudeService(mockedRestTemplate, mockedApiProperties);

    @Test
    @DisplayName("Calling google API should succeed")
    public void callGoogleApiWithValidAddressShouldSucceed() {
        // Set Up
        when(mockedApiProperties.getBaseUrl()).thenReturn("http://someUrl.com");
        when(mockedApiProperties.getApiKey()).thenReturn("key");
        when(mockedRestTemplate.getForEntity(
            "http://someUrl.com?address=R.+Zuneide+Aparecida+Marin,+43.+Campinas+-+SP,+BR.+13084-780&key=key",
            GeocodingResponse.class)).thenReturn(aGeocodingResponse());
        Address realAddress = aRealAddress();
        // When I
        Address actual = service.findLatitudeAndLongitude(realAddress);
        // Then
        assertThat(actual).isEqualTo(realAddress.toBuilder().latitude("37.4218147").longitude("-122.084658").build());
    }

    @Test
    @DisplayName("Calling google API should fail")
    public void callGoogleApiWithInvalidAddressShouldFail() {
        // Set Up
        when(mockedApiProperties.getBaseUrl()).thenReturn("http://someUrl.com");
        when(mockedApiProperties.getApiKey()).thenReturn("key");
        when(mockedRestTemplate.getForEntity(
            "http://someUrl.com?address=R.+Zuneide+Aparecida+Marin,+43.+Campinas+-+SP,+BR.+13084-780&key=key",
            GeocodingResponse.class)).thenReturn(ResponseEntity.badRequest().build());
        // When I
        // Then
        assertThatExceptionOfType(GoogleApiInvalidAddressInformation.class).isThrownBy(() -> service.findLatitudeAndLongitude(
            aRealAddress()));
    }

    private ResponseEntity<GeocodingResponse> aGeocodingResponse() {
        return ResponseEntity.ok(GeocodingResponse.builder()
            .results(singletonList(Result.builder()
                .geometry(Geometry.builder()
                    .location(Location.builder().latitude("37.4218147").longitude("-122.084658").build())
                    .build())
                .build()))
            .build());
    }
}
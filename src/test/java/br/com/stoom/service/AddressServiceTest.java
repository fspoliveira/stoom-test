package br.com.stoom.service;

import br.com.stoom.entity.Address;
import br.com.stoom.google.service.LatitudeLongitudeService;
import br.com.stoom.repository.AddressRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.stoom.fixtures.AddressFixture.aSimpleAddress;
import static br.com.stoom.fixtures.AddressFixture.aSimpleAddressWithId;
import static br.com.stoom.fixtures.AddressFixture.aSimpleAddressWithoutLatLon;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@DisplayName("Address Service testing")
class AddressServiceTest {

    private AddressRepository mockedAddressRepository = mock(AddressRepository.class);
    private LatitudeLongitudeService latLonService = mock(LatitudeLongitudeService.class);
    private AddressService addressService = new AddressService(mockedAddressRepository, latLonService);

    @Test
    @DisplayName("Persist address in database")
    public void shouldCallRepositorysSaveMethod() {
        // Set up
        Address simpleAddress = aSimpleAddress();
        when(mockedAddressRepository.save(simpleAddress)).thenReturn(simpleAddress.toBuilder()
            .id(UUID.randomUUID())
            .build());
        // When I
        Address actual = addressService.save(simpleAddress);
        //Then
        verify(mockedAddressRepository, times(1)).save(simpleAddress);
        verifyNoMoreInteractions(mockedAddressRepository);
        verifyNoInteractions(latLonService);
        assertThat(actual).isEqualTo(simpleAddress.toBuilder().id(actual.getId()).build());
    }

    @Test
    @DisplayName("Consult google API and persist address without LatLon in database")
    public void shouldConsultGoogleAPI_AndCallRepositorysSaveMethod() {
        // Set up
        UUID id = UUID.randomUUID();
        Address simpleAddressWithoutLatLon = aSimpleAddressWithoutLatLon().toBuilder().build();
        Address simpleAddress = aSimpleAddress();
        when(latLonService.findLatitudeAndLongitude(simpleAddressWithoutLatLon)).thenReturn(simpleAddress);
        when(mockedAddressRepository.save(simpleAddress)).thenReturn(aSimpleAddressWithId(id));
        // When I
        Address actual = addressService.save(simpleAddressWithoutLatLon);
        //Then
        verify(mockedAddressRepository, times(1)).save(simpleAddress);
        verifyNoMoreInteractions(mockedAddressRepository);
        verify(latLonService, times(1)).findLatitudeAndLongitude(simpleAddressWithoutLatLon);
        verifyNoMoreInteractions(mockedAddressRepository);
        assertThat(actual).isEqualTo(simpleAddress.toBuilder().id(actual.getId()).build());
    }

    @Test
    @DisplayName("Find address by ID")
    public void shouldCallRepositorysFindById() {
        // Set up
        UUID id = UUID.randomUUID();
        Address mockedAddress = aSimpleAddressWithId(id);
        when(mockedAddressRepository.findById(id)).thenReturn(Optional.of(mockedAddress));
        // When I
        Address actual = addressService.findById(id);
        //Then
        verify(mockedAddressRepository, times(1)).findById(id);
        verifyNoMoreInteractions(mockedAddressRepository);
        assertThat(actual).isEqualTo(mockedAddress);
    }

    @Test
    @DisplayName("Find invalid address by ID should throw an error")
    public void shouldThrowAnErrorWhenFindingInvalidId() {
        // Set up
        UUID id = UUID.randomUUID();
        when(mockedAddressRepository.findById(any())).thenThrow(new EmptyResultDataAccessException(1));
        // When I
        assertThatExceptionOfType(EmptyResultDataAccessException.class).isThrownBy(() -> addressService.findById(id));
        //Then
        verify(mockedAddressRepository, times(1)).findById(id);
        verifyNoMoreInteractions(mockedAddressRepository);
    }

    @Test
    @DisplayName("Find all addresses")
    public void shouldCallRepositorysFindAll() {
        // Set up
        Address addressOne = aSimpleAddressWithId(UUID.randomUUID());
        Address addressTwo = aSimpleAddressWithId(UUID.randomUUID());
        when(mockedAddressRepository.findAll()).thenReturn(Arrays.asList(addressOne, addressTwo));
        // When I
        List<Address> actual = addressService.findAll();
        //Then
        verify(mockedAddressRepository, times(1)).findAll();
        verifyNoMoreInteractions(mockedAddressRepository);
        assertThat(actual).isNotEmpty().hasSize(2).containsExactlyInAnyOrder(addressOne, addressTwo);
    }

    @Test
    @DisplayName("Find by parameter")
    public void shouldCallRepositorysFindBy() {
        // Set up
        String filterField = "streetName";
        String filterString = "Some Street name";
        UUID id = UUID.randomUUID();
        Address mockedAddress = aSimpleAddressWithId(id);
        when(mockedAddressRepository.findBy(filterField, filterString)).thenReturn(Collections.singletonList(
            mockedAddress));
        // When I
        List<Address> actual = addressService.findBy(filterField, filterString);
        //Then
        verify(mockedAddressRepository, times(1)).findBy(filterField, filterString);
        verifyNoMoreInteractions(mockedAddressRepository);
        assertThat(actual).isNotEmpty().first().isEqualTo(mockedAddress);
    }

    @Test
    @DisplayName("Update valid address in database")
    public void shouldUpdateAddressInDatabase() {
        // Given
        UUID id = UUID.randomUUID();
        Address updatedAddress = Address.builder()
            .id(id)
            .streetName("New Street name")
            .number(999)
            .complement("New Complement")
            .zipcode("New zipcode")
            .state("new state")
            .neighbourhood("new neighbourhood")
            .longitude("new longitude")
            .latitude("new latitude")
            .country("US")
            .city("New city")
            .build();
        // Set up
        Address mockedAddress = aSimpleAddressWithId(id);
        when(mockedAddressRepository.findById(id)).thenReturn(Optional.of(mockedAddress));
        when(mockedAddressRepository.save(updatedAddress)).thenReturn(updatedAddress);
        // When I
        Address actual = addressService.update(updatedAddress);
        // Then
        verify(mockedAddressRepository, times(1)).findById(id);
        verify(mockedAddressRepository, times(1)).save(updatedAddress);
        verifyNoMoreInteractions(mockedAddressRepository);
        assertThat(actual).isEqualTo(updatedAddress);
    }

    @Test
    @DisplayName("Update invalid address in database should throw an error")
    public void shouldThrowAnErrorWhenUpdatingInvalidAddress() {
        // Given
        UUID id = UUID.randomUUID();
        Address updatedAddress = Address.builder()
            .id(id)
            .streetName("New Street name")
            .number(999)
            .complement("New Complement")
            .zipcode("New zipcode")
            .state("new state")
            .neighbourhood("new neighbourhood")
            .longitude("new longitude")
            .latitude("new latitude")
            .country("US")
            .city("New city")
            .build();
        // Set up
        when(mockedAddressRepository.findById(any())).thenThrow(new EmptyResultDataAccessException(1));
        // When I
        assertThatExceptionOfType(EmptyResultDataAccessException.class).isThrownBy(() -> addressService.update(
            updatedAddress));
        //Then
        verify(mockedAddressRepository, times(1)).findById(id);
        verifyNoMoreInteractions(mockedAddressRepository);
    }

    @Test
    @DisplayName("Delete address in database")
    public void shouldCallRepositorysDeleteMethod() {
        // Set up
        UUID id = UUID.randomUUID();
        Address mockedAddress = aSimpleAddressWithId(id);
        when(mockedAddressRepository.findById(id)).thenReturn(Optional.of(mockedAddress));
        // When I
        addressService.delete(id);
        //Then
        verify(mockedAddressRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(mockedAddressRepository);
    }
}
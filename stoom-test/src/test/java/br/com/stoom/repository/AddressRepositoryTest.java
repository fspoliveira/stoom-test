package br.com.stoom.repository;

import br.com.stoom.configuration.PostgresDatabaseContainer;
import br.com.stoom.entity.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.stoom.fixtures.AddressFixture.aSimpleAddress;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@DisplayName("Address Repository testing")
class AddressRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AddressRepository repository;

    @Container
    private static final PostgresDatabaseContainer POSTGRES_DATABASE_CONTAINER =
        PostgresDatabaseContainer.getInstance();

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Save address in database")
    public void shouldSaveAddressInDatabase() {
        Address expected = aSimpleAddress();
        Address actual = testEntityManager.find(Address.class, repository.save(expected).getId());
        assertThat(actual).isNotNull()
            .hasNoNullFieldsOrProperties()
            .extracting(Address::getStreetName, Address::getCountry, Address::getCity)
            .contains("Some Street name", "BR", "Some City");
    }

    @Test
    @DisplayName("Find address by id")
    public void shouldFindAnAddressByItsId() {
        // Set Up
        Address expected = testEntityManager.persistAndFlush(aSimpleAddress());
        // When I
        Optional<Address> actual = repository.findById(expected.getId());
        // Then
        assertThat(actual).isPresent().get().isEqualTo(expected);
    }

    @Test
    @DisplayName("Find a non-existent address by id")
    public void findAnAddressByNonExistentIdShouldThrowAError() {
        // When I
        // Then
        assertThat(repository.findById(UUID.randomUUID()))
            .isNotPresent();
    }

    @Test
    @DisplayName("Save all addresses in database")
    public void shouldFindAllAddress() {
        // Set Up
        Address simpleAddress = testEntityManager.persistAndFlush(aSimpleAddress());
        Address simpleAddressWithOtherNumber =
            testEntityManager.persistAndFlush(aSimpleAddress().toBuilder().number(999).build());
        // When I
        List<Address> all = repository.findAll();
        // Then
        assertThat(all).isNotNull()
            .isNotEmpty()
            .hasSize(2)
            .containsExactlyInAnyOrder(simpleAddress, simpleAddressWithOtherNumber);
    }

    @Test
    @DisplayName("Delete an address in database")
    public void shouldDeleteAddressInDatabase() {
        // Set Up
        Address expected = testEntityManager.persistAndFlush(aSimpleAddress());
        UUID expectedId = expected.getId();
        // When I
        repository.deleteById(expectedId);
        // Then
        Address actual = testEntityManager.find(Address.class, expectedId);
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("Delete an non-existent address in database")
    public void deleteANonExistentAddressInDatabaseShouldThrowAnError() {
        // When I
        // Then
        assertThatExceptionOfType(EmptyResultDataAccessException.class)
            .isThrownBy(() -> repository.deleteById(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Update address in database")
    public void shouldUpdateAddressInDatabase() {
        // Set Up
        Address simpleAddress = testEntityManager.persistAndFlush(aSimpleAddress());
        // When I
        Address updatedAddress = simpleAddress.toBuilder().number(999).build();
        repository.save(updatedAddress);
        // Then
        Address actual = testEntityManager.find(Address.class, simpleAddress.getId());
        assertThat(actual).extracting(Address::getCity, Address::getNumber).contains("Some City", 999);
    }

    @Test
    @DisplayName("Find an address passing a field as parameter")
    public void shouldFindAnAddressWithAFieldPassedAsParameter() {
        // Set Up
        Address addressOne = testEntityManager.persistAndFlush(aSimpleAddress());
        Address addressTwo = testEntityManager.persistAndFlush(aSimpleAddress().toBuilder()
            .streetName("Some other Street name")
            .build());
        // When I
        List<Address> found = repository.findBy("streetName", "Street");
        // Then
        assertThat(found).isNotNull().isNotEmpty().containsExactlyInAnyOrder(addressOne, addressTwo);
    }

    @Test
    @DisplayName("Find an address passing an invalid field as parameter")
    public void shouldFindAnAddressWithAnInvalidFieldPassedAsParameter() {
        // Set Up
        Address addressOne = testEntityManager.persistAndFlush(aSimpleAddress());
        Address addressTwo = testEntityManager.persistAndFlush(aSimpleAddress().toBuilder()
            .streetName("Some other Street name")
            .build());
        // When I
        // Then
        assertThatExceptionOfType(InvalidDataAccessApiUsageException.class).isThrownBy(() -> repository.findBy("invalidField",
            "Street"));
    }
}
package br.com.stoom.controller;

import br.com.stoom.configuration.PostgresDatabaseContainer;
import br.com.stoom.configuration.RedisContainer;
import br.com.stoom.entity.Address;
import br.com.stoom.fixtures.AddressFixture;
import br.com.stoom.model.AddressModel;
import br.com.stoom.model.ErrorModel;
import br.com.stoom.repository.AddressRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Integration Testing for Address API")
class AddressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddressRepository repository;

    @Container
    private static final PostgresDatabaseContainer POSTGRES_DATABASE_CONTAINER =
        PostgresDatabaseContainer.getInstance();

    @Container
    private static final RedisContainer REDIS_CONTAINER = RedisContainer.getInstance();

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("[Read] Testing the find all addresses flow")
    public void whenIQueryForAllAddresses_thenItShouldReturnAllDataInAddressTable() throws Exception {
        // Set up
        Address address = AddressFixture.createSimpleData(repository);
        // Given a simple GET request
        String responseBody = mockMvc.perform(get("/api/address").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        // Then it should return
        List<AddressModel> addressModel = objectMapper.readValue(responseBody, new TypeReference<List<AddressModel>>() {
        });
        assertThat(addressModel).hasSize(1)
            .extracting(AddressModel::getCity, AddressModel::getLatitude, AddressModel::getLongitude)
            .contains(tuple("Some City", "-22.877083", "-47.048379"));
    }

    @Test
    @DisplayName("[Read] Testing the find all addresses flow returning empty")
    public void whenIQueryForAllAddressesOnAEmptyDataSet_thenItShouldReturnEmptyResponse() throws Exception {
        // Given a simple GET request
        String responseBody = mockMvc.perform(get("/api/address").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        // Then it should return
        List<AddressModel> addressModel = objectMapper.readValue(responseBody, new TypeReference<List<AddressModel>>() {
        });
        assertThat(addressModel).hasSize(0);
    }

    @Test
    @DisplayName("[Read] Testing the find address by id flow")
    public void whenIQueryForAddressesById_thenItShouldReturnSpecificAddress() throws Exception {
        // Set up
        Address address = AddressFixture.createSimpleData(repository);
        // Given a simple GET request
        String responseBody =
            mockMvc.perform(get("/api/address/" + address.getId().toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        // Then it should return
        AddressModel addressModel = objectMapper.readValue(responseBody, AddressModel.class);
        assertThat(addressModel)
            .extracting(AddressModel::getCity, AddressModel::getLatitude, AddressModel::getLongitude)
            .contains("Some City", "-22.877083", "-47.048379");
    }

    @Test
    @DisplayName("[Read] Testing the find address by id flow with invalid ID")
    public void whenIQueryForAddressesByIdWithInvalidId_thenItShouldReturnNotFoundWithError() throws Exception {
        // Given a simple GET request
        String responseBody =
            mockMvc.perform(get("/api/address/" + UUID.randomUUID().toString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        // Then it should return
        ErrorModel addressModel = objectMapper.readValue(responseBody, ErrorModel.class);
        assertThat(addressModel).extracting(ErrorModel::getMessage).isEqualTo("Address not found");
    }

    @Test
    @DisplayName("[Read] Testing the find address by specific field flow")
    public void whenIQueryForAddressesBySpecificField_thenItShouldReturnSpecificAddress() throws Exception {
        // Set up
        AddressFixture.createSimpleData(repository);
        AddressFixture.createSimpleData(repository);
        // Given a simple GET request
        String responseBody = mockMvc.perform(get("/api/address").queryParam("field", "city")
            .queryParam("value", "Some")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        // Then it should return
        List<AddressModel> addressModel = objectMapper.readValue(responseBody, new TypeReference<List<AddressModel>>() {
        });
        assertThat(addressModel).hasSize(2)
            .extracting(AddressModel::getCity, AddressModel::getLatitude, AddressModel::getLongitude)
            .contains(tuple("Some City", "-22.877083", "-47.048379"));
    }

    @Test
    @DisplayName("[Read] Testing the find address by specific field flow with empty datase")
    public void whenIQueryForAddressesBySpecificField_witAnEmptyDataser_thenItShould200() throws Exception {
        // Set up
        // Given a simple GET request
        String responseBody = mockMvc.perform(get("/api/address").queryParam("field", "city")
            .queryParam("value", "Some")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        // Then it should return
        List<AddressModel> addressModel = objectMapper.readValue(responseBody, new TypeReference<List<AddressModel>>() {
        });
        assertThat(addressModel).hasSize(0);
    }

    @Test
    @DisplayName("[Read] Testing the find address by invalid field flow")
    public void whenIQueryForAddressesByIdWithInvalidField_thenItShouldReturnInternalServerErrorWithMessage() throws
                                                                                                              Exception {
        // Given a simple GET request
        String responseBody = mockMvc.perform(get("/api/address").queryParam("field", "invalidField")
            .queryParam("value", "Some")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andReturn()
            .getResponse()
            .getContentAsString();
        // Then it should return
        ErrorModel addressModel = objectMapper.readValue(responseBody, ErrorModel.class);
        assertThat(addressModel).extracting(ErrorModel::getMessage)
            .isEqualTo("Invalid field name passed in query string! The possible values are [zipcode, streetName, city, latitude, longitude, country, neighbourhood, number, state, complement]. Bear in mind that it is case sensitive.");
    }

    @Test
    @DisplayName("[Delete] Testing the delete address by invalid id")
    public void whenIDeleteAddressById_AndItDoesntExist_thenItShouldReturnNotFoundWithMessage() throws Exception {
        // Given a simple GET request
        String responseBody = mockMvc.perform(delete("/api/address/" +
                                                     UUID.randomUUID()
                                                         .toString()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse()
            .getContentAsString();
        // Then it should return
        ErrorModel addressModel = objectMapper.readValue(responseBody, ErrorModel.class);
        assertThat(addressModel).extracting(ErrorModel::getMessage).isEqualTo("Address not found");
    }

    @Test
    @DisplayName("[Delete] Testing the delete address by id")
    public void whenIDeleteAddressById_thenItShouldReturn200() throws Exception {
        Address address = AddressFixture.createSimpleData(repository);
        // Given a simple GET request
        String responseBody =
            mockMvc.perform(delete("/api/address/" + address.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @DisplayName("[Create] Testing the create address")
    public void whenIPostAValidAddress_thenItShouldReturn201() throws Exception {
        AddressModel addressModel = realAddressModel();
        mockMvc.perform(post("/api/address").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressModel)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
        Address address = repository.findAll().stream().findFirst().orElse(null);
        assertThat(address).extracting(Address::getStreetName).isEqualTo(addressModel.getStreetName());
    }

    @Test
    @DisplayName("[Create] Testing the create address with mandatory field empty")
    public void whenIPostAnAddress_withMandatoryFieldEmpty_thenItShouldReturn400() throws Exception {
        AddressModel addressModel = realAddressModel().toBuilder().streetName(null).build();
        mockMvc.perform(post("/api/address").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressModel))).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[Create] Testing the create address without Lat/Lon")
    public void whenIPostAnAddress_withoutLatLon_thenItShouldReturn201_andFetchFromGoogle() throws Exception {
        AddressModel addressModel = realAddressModel();
        mockMvc.perform(post("/api/address").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressModel.toBuilder().latitude(null).longitude(null).build())))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));
        Address address = repository.findAll().stream().findFirst().orElse(null);
        assertThat(address).extracting(Address::getLatitude, Address::getLongitude)
            .contains(addressModel.getLatitude(), addressModel.getLongitude());
    }

    @Test
    @DisplayName("[Create] Testing the create address without Lat/Lon and invalid address data")
    public void whenIPostAnAddress_withoutLatLonAndInvalidAddressData_thenItShouldReturn400() throws Exception {
        AddressModel addressModel = AddressFixture.aSimpleAddress().toModel();
        mockMvc.perform(post("/api/address").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressModel.toBuilder()
                .streetName("invalidStreetName")
                .latitude(null)
                .longitude(null)
                .build()))).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[Update] Testing the update address with invalid id")
    public void whenIUpdateAnAddress_withInvalidId_thenItShouldReturn404() throws Exception {
        AddressModel addressModel = realAddressModel();
        mockMvc.perform(put("/api/address/"+UUID.randomUUID().toString()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressModel)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("[Update] Testing the update address with mandatory field blank")
    public void whenIUpdateAnAddress_withMandatoryFieldEmpty_thenItShouldReturn400() throws Exception {
        Address address = repository.save(AddressFixture.aSimpleAddress());
        AddressModel addressModel = realAddressModel();
        mockMvc.perform(put("/api/address/"+address.getId().toString()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressModel.toBuilder().zipcode(null).build())))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[Update] Testing the update address with valid data and without lat / lon")
    public void whenIUpdateAnAddress_withValidData_withoutLatLon_thenItShouldReturn200() throws Exception {
        Address address = repository.save(AddressFixture.aSimpleAddress());
        AddressModel addressModel = realAddressModel();
        mockMvc.perform(put("/api/address/"+address.getId().toString()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressModel.toBuilder().latitude(null).build())))
            .andExpect(status().isOk());
        String latitude = repository.findAll().stream().findFirst().map(Address::getLatitude).orElse(null);
        assertThat(latitude)
            .isNotNull();
    }

    @Test
    @DisplayName("[Update] Testing the update address with valid data")
    public void whenIUpdateAnAddress_withValidData_withLatLon_thenItShouldReturn200() throws Exception {
        Address address = repository.save(AddressFixture.aSimpleAddress());
        AddressModel addressModel = realAddressModel();
        mockMvc.perform(put("/api/address/"+address.getId().toString()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addressModel)))
            .andExpect(status().isOk());
    }

    private AddressModel realAddressModel() {
        return AddressModel.builder()
            .streetName("R. Zuneide Aparecida Marin")
            .city("Campinas")
            .country("BR")
            .latitude("-22.8354045")
            .longitude("-47.0787762")
            .neighbourhood("Jardim Santa Genebra II (Barao Geraldo)")
            .number("43")
            .state("SP")
            .zipcode("13084-780")
            .build();
    }
}
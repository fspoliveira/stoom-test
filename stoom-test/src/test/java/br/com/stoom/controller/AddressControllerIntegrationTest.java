package br.com.stoom.controller;

import br.com.stoom.configuration.PostgresDatabaseContainer;
import br.com.stoom.configuration.RedisContainer;
import br.com.stoom.entity.Address;
import br.com.stoom.fixtures.AddressFixture;
import br.com.stoom.model.AddressModel;
import br.com.stoom.repository.AddressRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
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

    @Test
    @DisplayName("Testing the whole find all flow")
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
        assertThat(addressModel)
            .hasSize(1)
            .extracting(AddressModel::getCity, AddressModel::getLatitude, AddressModel::getLongitude)
            .contains(tuple("Some City", "-22.877083", "-47.048379"));
    }

}
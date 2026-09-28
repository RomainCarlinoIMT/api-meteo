package com.example.apimeteo;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8089)
public class WeatherIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("ban.api.url", () -> "http://localhost:8089");
        registry.add("metnorway.api.url", () -> "http://localhost:8089");
    }

    @Test
    public void testGetWeatherNominal() throws Exception {
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/search.*"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[4.08,44.12]},\"properties\":{\"label\":\"Alès\"}}]}")));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/weather.*"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                .withBody("{\"properties\":{\"timeseries\":[{\"time\":\"2025-06-10T14:00:00Z\",\"data\":{\"instant\":{\"details\":{\"air_temperature\":24.3}}}}]}}")));

        mockMvc.perform(get("/weather").param("address", "Alès"))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"address\":\"Alès\",\"latitude\":44.12,\"longitude\":4.08,\"hourly\":[{\"time\":\"2025-06-10T14:00:00Z\",\"temperatureCelsius\":24.3}]}", true));
    }

    @Test
    public void testGetWeatherDemoDoesNotCallProviders() throws Exception {
        mockMvc.perform(get("/weather").param("address", "Alès").param("demo", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"address\":\"Alès\",\"latitude\":44.1279,\"longitude\":4.0817,\"hourly\":[{\"time\":\"2025-06-10T14:00:00Z\",\"temperatureCelsius\":24.3}]}", true));

        WireMock.verify(0, WireMock.anyRequestedFor(WireMock.anyUrl()));
    }

    @Test
    public void testGetWeatherMissingAddress() throws Exception {
        mockMvc.perform(get("/weather"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetWeatherAddressNotFound() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/search.*"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"type\":\"FeatureCollection\",\"features\":[]}")));

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/weather").param("address", "CityThatDoesNotExists"));
        });
    }
}
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "weather.provider=open-meteo")
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8090)
class OpenMeteoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("ban.api.url", () -> "http://localhost:8090");
        registry.add("openmeteo.api.url", () -> "http://localhost:8090");
    }

    @Test
    void returnsTheCommonWeatherResponseFormat() throws Exception {
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/search/.*"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[4.08,44.12]},\"properties\":{\"label\":\"Alès\"}}]}")));
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/v1/forecast"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"hourly\":{\"time\":[1749564000],\"temperature_2m\":[24.3]}}")));

        mockMvc.perform(get("/weather").param("address", "Alès"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"address\":\"Alès\",\"latitude\":44.12,\"longitude\":4.08,\"hourly\":[{\"time\":\"2025-06-10T14:00:00Z\",\"temperatureCelsius\":24.3}]}", true));
    }
}
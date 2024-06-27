package org.airly;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

public class AirQualityClient {
    private final String baseUrl;
    private final String stationsEndpoint;
    private final String installationsEndpoint;

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    public AirQualityClient(HttpClient httpClient, ObjectMapper objectMapper, ConfigLoader configLoader) throws IOException {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseUrl = configLoader.getProperty("base.url");
        this.stationsEndpoint = configLoader.getProperty("stations.endpoint");
        this.installationsEndpoint = configLoader.getProperty("installations.endpoint");

    }

    public List<Station> getAllStations() throws Exception {
        return fetchData(baseUrl + stationsEndpoint, Station[].class);
    }

    public List<Installation> getInstallationsForStation(int stationId) throws Exception {
        return fetchData(baseUrl + installationsEndpoint + stationId, Installation[].class);
    }
    private <T> List<T> fetchData(String url, Class<T[]> clazz) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP error code : " + response.statusCode());
        }

        return List.of(objectMapper.readValue(response.body(), clazz));
    }
}


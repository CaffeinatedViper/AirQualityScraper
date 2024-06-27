package org.airly;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {


        AirQualityClient client = new AirQualityClient(HttpClient.newHttpClient(),new ObjectMapper(), new ConfigLoader("config.properties"));

        List<Station> stations = client.getAllStations();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ConcurrentHashMap<Integer, String> results = new ConcurrentHashMap<>();

        for (Station station : stations) {
            executor.submit(() -> {
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Station #").append(station.getId()).append(" (").append(station.getStationName()).append("):\n");
                    List<Installation> installations = client.getInstallationsForStation(station.getId());
                    for (Installation installation : installations) {
                        sb.append("    installation #").append(installation.getId()).append(": '")
                                .append(installation.getParam().getParamCode()).append("'\n");
                    }
                    results.put(station.getId(), sb.toString());
                } catch (Exception e) {
                    results.put(station.getId(), "Error processing station " + station.getId() + ": " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        List<Integer> sortedStationIds = new ArrayList<>(results.keySet());
        Collections.sort(sortedStationIds);
        for (Integer stationId : sortedStationIds) {
            System.out.print(results.get(stationId));
        }
    }
}
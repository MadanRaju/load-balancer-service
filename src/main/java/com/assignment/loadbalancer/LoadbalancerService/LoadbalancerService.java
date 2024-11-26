package com.assignment.loadbalancer.LoadbalancerService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;
import java.net.http.HttpClient;
import java.net.URI;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoadbalancerService {

    private final List<String> downStreamServerUrls = new ArrayList<>();
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    RestTemplate restTemplate = new RestTemplate();

    //Added hosts only for testing purposes, will be auto discovered through eureka on any managed environment
    public LoadbalancerService() {
    }

    public ObjectNode routeRequest() {
        System.out.println("Health Check Request processed");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode dataNode = objectMapper.createObjectNode();
        if (downStreamServerUrls.isEmpty()) {
            dataNode.put("message", "No services available");
            dataNode.put("status", "NOT OK");
            return dataNode;
        }
        //Round robin logic
//        int index = currentIndex.getAndIncrement() % downStreamServerUrls.size();
//        System.out.println(STR."Request sent to\{downStreamServerUrls.get(index)}");

        //Random logic
        Random random = new Random();
        int index = random.nextInt(1000) % downStreamServerUrls.size();
        return restTemplate.getForObject(STR."\{downStreamServerUrls.get(index)}/hello-service/hello", ObjectNode.class);
    }
    //http://hello-service

    public void addService(String serverUrl) {
        downStreamServerUrls.add(serverUrl);
    }

    public void removeService(String serverUrl) {
        downStreamServerUrls.remove(serverUrl);
    }

    @Scheduled(fixedRate = 10000)
    public void checkServerStatus() throws IOException {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest healthCheckRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8761/eureka/apps/hello-service"))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> healthCheckResponse = httpClient.send(healthCheckRequest, HttpResponse.BodyHandlers.ofString());
            if(healthCheckResponse.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode response = mapper.readTree(healthCheckResponse.body());
                for(JsonNode instance: response.get("application").get("instance")) {
                    String serverUrl = STR."http://\{instance.get("ipAddr").asText()}:\{instance.get("port").get("$").asText()}";
                    if(Objects.equals(instance.get("status").asText(), "UP")) {
                        if(!downStreamServerUrls.contains(serverUrl)) {
                            addService(serverUrl);
                        }
                    } else if(Objects.equals(instance.get("status").asText(), "DOWN")) {
                        if(downStreamServerUrls.contains(serverUrl)) {
                            removeService(serverUrl);
                        }
                    }
                }
                System.out.println(downStreamServerUrls);
            }
        } catch (Exception e) {
            System.out.println(STR."Health check exception\{e.getMessage()}");
        }
    }
}

package com.assignment.loadbalancer.LoadbalancerService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoadbalancerService {

    private final List<String> downStreamServerUrls = new ArrayList<>();
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    RestTemplate restTemplate = new RestTemplate();

    //Added hosts only for testing purposes, will be auto discovered through eureka on any managed environment
    public LoadbalancerService() {
        downStreamServerUrls.add("http://localhost:8080");
        downStreamServerUrls.add("http://localhost:8082");
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
        int index = currentIndex.getAndIncrement() % downStreamServerUrls.size();
        System.out.println(STR."Request sent to\{downStreamServerUrls.get(index)}");

        //Random logic
        //Random random = new Random();
        //int index = random.nextInt(1000) % downStreamServerUrls.size();
        return restTemplate.getForObject(STR."\{downStreamServerUrls.get(index)}/hello-service/hello", ObjectNode.class);
    }

    public void addService(String serverUrl) {
        downStreamServerUrls.add(serverUrl);
    }

    public void removeService(String serverUrl) {
        downStreamServerUrls.remove(serverUrl);
    }

    @Scheduled(fixedRate = 10000)
    public void checkServerStatus() throws IOException {

        Iterator<String> iterator = downStreamServerUrls.iterator();
        while (iterator.hasNext()) {
            String server = iterator.next();
            try {
                // Perform a basic health check
                int status = new java.net.URL(STR."\{server}/actuator/health")
                        .openConnection()
                        .getInputStream()
                        .available();
                if (status == 0) {
                    iterator.remove();
                }
            } catch (Exception e) {
                iterator.remove();
            }
        }
    }
}

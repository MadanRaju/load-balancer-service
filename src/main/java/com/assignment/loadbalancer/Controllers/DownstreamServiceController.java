package com.assignment.loadbalancer.Controllers;

import com.assignment.loadbalancer.Feign.FeignClientInterface;
import com.assignment.loadbalancer.LoadbalancerService.LoadbalancerService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping({"/load-balancer-service"})
public class DownstreamServiceController {
    @Autowired
    private FeignClientInterface helloClient;
    @Autowired
    private LoadbalancerService loadBalancerService;

    @RequestMapping ("/hello")
    public ObjectNode sayHello() throws Exception {
        try {
            return loadBalancerService.routeRequest();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}

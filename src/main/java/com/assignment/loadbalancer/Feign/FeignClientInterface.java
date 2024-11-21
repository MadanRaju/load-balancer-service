package com.assignment.loadbalancer.Feign;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "hello-service")
public interface FeignClientInterface {
    @GetMapping("/hello")
    String sayHello();
}


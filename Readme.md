# Simple Load Balancer using Spring Boot

## Overview
This project implements a simple load balancer in Spring Boot using round-robin scheduling (with the option to add more strategies). It dynamically distributes HTTP requests among backend servers.

## Setup Instructions
1. Clone the load-balancer-service & hello-service repositories.
2. Run the hello-service on different ports (e.g., 8080, 8082).
3. Run the load balancer service (`LoadbalancerApplication.java`).
4. Open http://localhost:8761 on the browser to check active status of servers detected.
5. Open http://localhost:8761/eureka/apps/hello-service to check details of the services in particular.

## Architectural diagram
Link - https://drive.google.com/file/d/14UH9-9jlBVaWp-otWHzWK4crGswpvVVT/view?usp=drive_link
![load-balancer.png](https://drive.google.com/file/d/14UH9-9jlBVaWp-otWHzWK4crGswpvVVT/view?usp=drive_link)

## High-Level Design
- **Load Balancer Service**: Routes requests based on round-robin or random selection.
- **Dynamic Server Management**: Add or remove backend servers dynamically using REST endpoints.

## Extending the Project
- Extend Eureka to automatically detect and add/remove servers from the active pool.
- Implement custom load balancer configurations to dynamically change the distribution strategy.
- Implement additional load balancing algorithms (e.g., least connections).
- Add health checks to periodically verify server availability.

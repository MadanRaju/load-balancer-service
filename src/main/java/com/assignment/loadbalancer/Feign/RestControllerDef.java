import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@LoadBalanced
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}

public void main() {
}

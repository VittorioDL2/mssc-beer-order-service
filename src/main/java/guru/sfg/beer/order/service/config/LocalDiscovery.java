package guru.sfg.beer.order.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Profile("local-discovery")
@EnableDiscoveryClient
@Configuration
public class LocalDiscovery {


}
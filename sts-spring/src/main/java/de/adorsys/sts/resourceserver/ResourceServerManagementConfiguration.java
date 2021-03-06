package de.adorsys.sts.resourceserver;

import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses = ResourceServerManagementConfigurationProperties.class)
public class ResourceServerManagementConfiguration {

    @Bean
    public ResourceServerService resourceServerService(
            ResourceServerRepository repository
    ) {
        return new ResourceServerService(repository);
    }
}

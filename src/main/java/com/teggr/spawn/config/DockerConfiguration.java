package com.teggr.spawn.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.teggr.spawn.docker.DockerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(prefix = "docker", name = "enabled", havingValue = "true", matchIfMissing = false)
public class DockerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DockerConfiguration.class);

    @Bean
    public DockerClientConfig dockerClientConfig(DockerConfigurationProperties properties) {
        logger.info("Configuring Docker client with host: {}", properties.getHost());
        
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(properties.getHost())
                .withDockerTlsVerify(properties.isTlsVerify());

        if (properties.getCertPath() != null) {
            builder.withDockerCertPath(properties.getCertPath());
        }

        if (properties.getApiVersion() != null) {
            builder.withApiVersion(properties.getApiVersion());
        }

        if (properties.getRegistryUrl() != null) {
            builder.withRegistryUrl(properties.getRegistryUrl());
        }

        if (properties.getRegistryUsername() != null) {
            builder.withRegistryUsername(properties.getRegistryUsername());
        }

        if (properties.getRegistryPassword() != null) {
            builder.withRegistryPassword(properties.getRegistryPassword());
        }

        if (properties.getRegistryEmail() != null) {
            builder.withRegistryEmail(properties.getRegistryEmail());
        }

        return builder.build();
    }

    @Bean
    public DockerHttpClient dockerHttpClient(DockerClientConfig dockerClientConfig) {
        return new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(dockerClientConfig.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
    }

    @Bean
    public DockerClient dockerClient(DockerClientConfig dockerClientConfig, 
                                    DockerHttpClient dockerHttpClient) {
        return DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient);
    }

    @Bean
    public DockerTemplate dockerTemplate(DockerClient dockerClient) {
        logger.info("Creating DockerTemplate bean");
        return new DockerTemplate(dockerClient);
    }
}

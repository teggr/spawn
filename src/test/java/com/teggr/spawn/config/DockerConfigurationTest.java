package com.teggr.spawn.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.transport.DockerHttpClient;
import com.teggr.spawn.docker.DockerTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class DockerConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DockerConfiguration.class));

    @Test
    void shouldNotCreateBeansWhenDockerDisabled() {
        // When
        contextRunner
                .withBean(DockerConfigurationProperties.class)
                .withPropertyValues("docker.enabled=false")
                .run(context -> {
                    // Then
                    assertThat(context).doesNotHaveBean(DockerClientConfig.class);
                    assertThat(context).doesNotHaveBean(DockerHttpClient.class);
                    assertThat(context).doesNotHaveBean(DockerClient.class);
                    assertThat(context).doesNotHaveBean(DockerTemplate.class);
                });
    }

    @Test
    void shouldNotCreateBeansByDefault() {
        // When
        contextRunner
                .withBean(DockerConfigurationProperties.class)
                .run(context -> {
                    // Then
                    assertThat(context).doesNotHaveBean(DockerClientConfig.class);
                    assertThat(context).doesNotHaveBean(DockerHttpClient.class);
                    assertThat(context).doesNotHaveBean(DockerClient.class);
                    assertThat(context).doesNotHaveBean(DockerTemplate.class);
                });
    }

    @Test
    void shouldCreateBeansWhenDockerEnabled() {
        // When
        contextRunner
                .withBean(DockerConfigurationProperties.class)
                .withPropertyValues(
                        "docker.enabled=true",
                        "docker.host=unix:///var/run/docker.sock"
                )
                .run(context -> {
                    // Then
                    assertThat(context).hasSingleBean(DockerClientConfig.class);
                    assertThat(context).hasSingleBean(DockerHttpClient.class);
                    assertThat(context).hasSingleBean(DockerClient.class);
                    assertThat(context).hasSingleBean(DockerTemplate.class);
                    
                    DockerTemplate dockerTemplate = context.getBean(DockerTemplate.class);
                    assertThat(dockerTemplate).isNotNull();
                    assertThat(dockerTemplate.getDockerClient()).isNotNull();
                });
    }

    @Test
    void shouldConfigureDockerClientWithCustomProperties() {
        // When
        contextRunner
                .withBean(DockerConfigurationProperties.class)
                .withPropertyValues(
                        "docker.enabled=true",
                        "docker.host=tcp://localhost:2375",
                        "docker.tlsVerify=false",
                        "docker.apiVersion=1.41"
                )
                .run(context -> {
                    // Then
                    assertThat(context).hasSingleBean(DockerClientConfig.class);
                    assertThat(context).hasSingleBean(DockerTemplate.class);
                    assertThat(context).hasSingleBean(DockerConfigurationProperties.class);
                    
                    // Verify beans are properly created
                    DockerTemplate template = context.getBean(DockerTemplate.class);
                    assertThat(template.getDockerClient()).isNotNull();
                });
    }
}

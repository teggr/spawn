package com.teggr.spawn.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DockerConfigurationPropertiesTest {

    @Test
    void shouldHaveDefaultValues() {
        // Given
        DockerConfigurationProperties properties = new DockerConfigurationProperties();

        // Then
        assertThat(properties.getHost()).isEqualTo("unix:///var/run/docker.sock");
        assertThat(properties.isTlsVerify()).isFalse();
        assertThat(properties.getCertPath()).isNull();
        assertThat(properties.getApiVersion()).isNull();
        assertThat(properties.getRegistryUrl()).isNull();
        assertThat(properties.getRegistryUsername()).isNull();
        assertThat(properties.getRegistryPassword()).isNull();
        assertThat(properties.getRegistryEmail()).isNull();
    }

    @Test
    void shouldSetAndGetHost() {
        // Given
        DockerConfigurationProperties properties = new DockerConfigurationProperties();
        String host = "tcp://localhost:2375";

        // When
        properties.setHost(host);

        // Then
        assertThat(properties.getHost()).isEqualTo(host);
    }

    @Test
    void shouldSetAndGetTlsVerify() {
        // Given
        DockerConfigurationProperties properties = new DockerConfigurationProperties();

        // When
        properties.setTlsVerify(true);

        // Then
        assertThat(properties.isTlsVerify()).isTrue();
    }

    @Test
    void shouldSetAndGetCertPath() {
        // Given
        DockerConfigurationProperties properties = new DockerConfigurationProperties();
        String certPath = "/path/to/certs";

        // When
        properties.setCertPath(certPath);

        // Then
        assertThat(properties.getCertPath()).isEqualTo(certPath);
    }

    @Test
    void shouldSetAndGetApiVersion() {
        // Given
        DockerConfigurationProperties properties = new DockerConfigurationProperties();
        String apiVersion = "1.41";

        // When
        properties.setApiVersion(apiVersion);

        // Then
        assertThat(properties.getApiVersion()).isEqualTo(apiVersion);
    }

    @Test
    void shouldSetAndGetRegistryUrl() {
        // Given
        DockerConfigurationProperties properties = new DockerConfigurationProperties();
        String registryUrl = "https://index.docker.io/v1/";

        // When
        properties.setRegistryUrl(registryUrl);

        // Then
        assertThat(properties.getRegistryUrl()).isEqualTo(registryUrl);
    }

    @Test
    void shouldSetAndGetRegistryUsername() {
        // Given
        DockerConfigurationProperties properties = new DockerConfigurationProperties();
        String username = "testuser";

        // When
        properties.setRegistryUsername(username);

        // Then
        assertThat(properties.getRegistryUsername()).isEqualTo(username);
    }

    @Test
    void shouldSetAndGetRegistryPassword() {
        // Given
        DockerConfigurationProperties properties = new DockerConfigurationProperties();
        String password = "testpassword";

        // When
        properties.setRegistryPassword(password);

        // Then
        assertThat(properties.getRegistryPassword()).isEqualTo(password);
    }

    @Test
    void shouldSetAndGetRegistryEmail() {
        // Given
        DockerConfigurationProperties properties = new DockerConfigurationProperties();
        String email = "test@example.com";

        // When
        properties.setRegistryEmail(email);

        // Then
        assertThat(properties.getRegistryEmail()).isEqualTo(email);
    }
}

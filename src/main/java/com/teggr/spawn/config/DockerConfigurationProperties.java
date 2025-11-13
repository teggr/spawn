package com.teggr.spawn.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "docker")
public class DockerConfigurationProperties {

    /**
     * Docker host URI (e.g., unix:///var/run/docker.sock, tcp://localhost:2375)
     */
    private String host = "unix:///var/run/docker.sock";

    /**
     * Enable TLS for Docker connection
     */
    private boolean tlsVerify = false;

    /**
     * Path to TLS certificate directory
     */
    private String certPath;

    /**
     * Docker API version
     */
    private String apiVersion;

    /**
     * Docker registry URL
     */
    private String registryUrl;

    /**
     * Docker registry username
     */
    private String registryUsername;

    /**
     * Docker registry password
     */
    private String registryPassword;

    /**
     * Docker registry email
     */
    private String registryEmail;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isTlsVerify() {
        return tlsVerify;
    }

    public void setTlsVerify(boolean tlsVerify) {
        this.tlsVerify = tlsVerify;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }

    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    public String getRegistryUsername() {
        return registryUsername;
    }

    public void setRegistryUsername(String registryUsername) {
        this.registryUsername = registryUsername;
    }

    public String getRegistryPassword() {
        return registryPassword;
    }

    public void setRegistryPassword(String registryPassword) {
        this.registryPassword = registryPassword;
    }

    public String getRegistryEmail() {
        return registryEmail;
    }

    public void setRegistryEmail(String registryEmail) {
        this.registryEmail = registryEmail;
    }
}

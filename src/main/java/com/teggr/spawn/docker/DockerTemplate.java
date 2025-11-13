package com.teggr.spawn.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Generic Docker template for managing Docker containers.
 * Provides operations for listing, running, stopping, and removing containers.
 */
public class DockerTemplate {

    private static final Logger logger = LoggerFactory.getLogger(DockerTemplate.class);

    private final DockerClient dockerClient;

    public DockerTemplate(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    /**
     * Lists all containers (both running and stopped).
     *
     * @param showAll if true, shows all containers including stopped ones; if false, shows only running containers
     * @return List of containers
     */
    public List<Container> listContainers(boolean showAll) {
        logger.debug("Listing containers (showAll: {})", showAll);
        return dockerClient.listContainersCmd()
                .withShowAll(showAll)
                .exec();
    }

    /**
     * Lists only running containers.
     *
     * @return List of running containers
     */
    public List<Container> listContainers() {
        return listContainers(false);
    }

    /**
     * Runs a new container from the specified image.
     *
     * @param imageName the Docker image name
     * @return the ID of the created container
     */
    public String runContainer(String imageName) {
        logger.debug("Running container from image: {}", imageName);
        CreateContainerResponse container = dockerClient.createContainerCmd(imageName).exec();
        dockerClient.startContainerCmd(container.getId()).exec();
        logger.info("Started container: {}", container.getId());
        return container.getId();
    }

    /**
     * Runs a new container from the specified image with a custom name.
     *
     * @param imageName     the Docker image name
     * @param containerName the name for the container
     * @return the ID of the created container
     */
    public String runContainer(String imageName, String containerName) {
        logger.debug("Running container from image: {} with name: {}", imageName, containerName);
        CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                .withName(containerName)
                .exec();
        dockerClient.startContainerCmd(container.getId()).exec();
        logger.info("Started container: {} with name: {}", container.getId(), containerName);
        return container.getId();
    }

    /**
     * Stops a running container.
     *
     * @param containerId the ID or name of the container to stop
     */
    public void stopContainer(String containerId) {
        logger.debug("Stopping container: {}", containerId);
        dockerClient.stopContainerCmd(containerId).exec();
        logger.info("Stopped container: {}", containerId);
    }

    /**
     * Stops a running container with a timeout.
     *
     * @param containerId the ID or name of the container to stop
     * @param timeout     the timeout in seconds to wait before killing the container
     */
    public void stopContainer(String containerId, int timeout) {
        logger.debug("Stopping container: {} with timeout: {}s", containerId, timeout);
        dockerClient.stopContainerCmd(containerId)
                .withTimeout(timeout)
                .exec();
        logger.info("Stopped container: {}", containerId);
    }

    /**
     * Removes a container.
     *
     * @param containerId the ID or name of the container to remove
     */
    public void removeContainer(String containerId) {
        logger.debug("Removing container: {}", containerId);
        dockerClient.removeContainerCmd(containerId).exec();
        logger.info("Removed container: {}", containerId);
    }

    /**
     * Removes a container with options.
     *
     * @param containerId the ID or name of the container to remove
     * @param force       if true, forces removal of a running container
     * @param removeVolumes if true, removes volumes associated with the container
     */
    public void removeContainer(String containerId, boolean force, boolean removeVolumes) {
        logger.debug("Removing container: {} (force: {}, removeVolumes: {})", 
                    containerId, force, removeVolumes);
        dockerClient.removeContainerCmd(containerId)
                .withForce(force)
                .withRemoveVolumes(removeVolumes)
                .exec();
        logger.info("Removed container: {}", containerId);
    }

    /**
     * Gets the Docker client instance.
     *
     * @return the Docker client
     */
    public DockerClient getDockerClient() {
        return dockerClient;
    }
}

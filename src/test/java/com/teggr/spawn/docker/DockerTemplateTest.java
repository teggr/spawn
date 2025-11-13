package com.teggr.spawn.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Container;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DockerTemplateTest {

    @Mock
    private DockerClient dockerClient;

    @Mock
    private ListContainersCmd listContainersCmd;

    @Mock
    private CreateContainerCmd createContainerCmd;

    @Mock
    private StartContainerCmd startContainerCmd;

    @Mock
    private StopContainerCmd stopContainerCmd;

    @Mock
    private RemoveContainerCmd removeContainerCmd;

    @Mock
    private CreateContainerResponse createContainerResponse;

    @Mock
    private Container container;

    private DockerTemplate dockerTemplate;

    @BeforeEach
    void setUp() {
        dockerTemplate = new DockerTemplate(dockerClient);
    }

    @Test
    void shouldListAllContainers() {
        // Given
        List<Container> expectedContainers = Arrays.asList(container, container);
        when(dockerClient.listContainersCmd()).thenReturn(listContainersCmd);
        when(listContainersCmd.withShowAll(true)).thenReturn(listContainersCmd);
        when(listContainersCmd.exec()).thenReturn(expectedContainers);

        // When
        List<Container> containers = dockerTemplate.listContainers(true);

        // Then
        assertThat(containers).hasSize(2);
        verify(dockerClient).listContainersCmd();
        verify(listContainersCmd).withShowAll(true);
        verify(listContainersCmd).exec();
    }

    @Test
    void shouldListRunningContainers() {
        // Given
        List<Container> expectedContainers = Arrays.asList(container);
        when(dockerClient.listContainersCmd()).thenReturn(listContainersCmd);
        when(listContainersCmd.withShowAll(false)).thenReturn(listContainersCmd);
        when(listContainersCmd.exec()).thenReturn(expectedContainers);

        // When
        List<Container> containers = dockerTemplate.listContainers();

        // Then
        assertThat(containers).hasSize(1);
        verify(dockerClient).listContainersCmd();
        verify(listContainersCmd).withShowAll(false);
        verify(listContainersCmd).exec();
    }

    @Test
    void shouldRunContainer() {
        // Given
        String imageName = "nginx:latest";
        String containerId = "container123";
        when(dockerClient.createContainerCmd(imageName)).thenReturn(createContainerCmd);
        when(createContainerCmd.exec()).thenReturn(createContainerResponse);
        when(createContainerResponse.getId()).thenReturn(containerId);
        when(dockerClient.startContainerCmd(containerId)).thenReturn(startContainerCmd);
        doNothing().when(startContainerCmd).exec();

        // When
        String result = dockerTemplate.runContainer(imageName);

        // Then
        assertThat(result).isEqualTo(containerId);
        verify(dockerClient).createContainerCmd(imageName);
        verify(createContainerCmd).exec();
        verify(dockerClient).startContainerCmd(containerId);
        verify(startContainerCmd).exec();
    }

    @Test
    void shouldRunContainerWithName() {
        // Given
        String imageName = "nginx:latest";
        String containerName = "my-nginx";
        String containerId = "container123";
        when(dockerClient.createContainerCmd(imageName)).thenReturn(createContainerCmd);
        when(createContainerCmd.withName(containerName)).thenReturn(createContainerCmd);
        when(createContainerCmd.exec()).thenReturn(createContainerResponse);
        when(createContainerResponse.getId()).thenReturn(containerId);
        when(dockerClient.startContainerCmd(containerId)).thenReturn(startContainerCmd);
        doNothing().when(startContainerCmd).exec();

        // When
        String result = dockerTemplate.runContainer(imageName, containerName);

        // Then
        assertThat(result).isEqualTo(containerId);
        verify(dockerClient).createContainerCmd(imageName);
        verify(createContainerCmd).withName(containerName);
        verify(createContainerCmd).exec();
        verify(dockerClient).startContainerCmd(containerId);
        verify(startContainerCmd).exec();
    }

    @Test
    void shouldStopContainer() {
        // Given
        String containerId = "container123";
        when(dockerClient.stopContainerCmd(containerId)).thenReturn(stopContainerCmd);
        doNothing().when(stopContainerCmd).exec();

        // When
        dockerTemplate.stopContainer(containerId);

        // Then
        verify(dockerClient).stopContainerCmd(containerId);
        verify(stopContainerCmd).exec();
    }

    @Test
    void shouldStopContainerWithTimeout() {
        // Given
        String containerId = "container123";
        int timeout = 10;
        when(dockerClient.stopContainerCmd(containerId)).thenReturn(stopContainerCmd);
        when(stopContainerCmd.withTimeout(timeout)).thenReturn(stopContainerCmd);
        doNothing().when(stopContainerCmd).exec();

        // When
        dockerTemplate.stopContainer(containerId, timeout);

        // Then
        verify(dockerClient).stopContainerCmd(containerId);
        verify(stopContainerCmd).withTimeout(timeout);
        verify(stopContainerCmd).exec();
    }

    @Test
    void shouldRemoveContainer() {
        // Given
        String containerId = "container123";
        when(dockerClient.removeContainerCmd(containerId)).thenReturn(removeContainerCmd);
        doNothing().when(removeContainerCmd).exec();

        // When
        dockerTemplate.removeContainer(containerId);

        // Then
        verify(dockerClient).removeContainerCmd(containerId);
        verify(removeContainerCmd).exec();
    }

    @Test
    void shouldRemoveContainerWithOptions() {
        // Given
        String containerId = "container123";
        when(dockerClient.removeContainerCmd(containerId)).thenReturn(removeContainerCmd);
        when(removeContainerCmd.withForce(true)).thenReturn(removeContainerCmd);
        when(removeContainerCmd.withRemoveVolumes(true)).thenReturn(removeContainerCmd);
        doNothing().when(removeContainerCmd).exec();

        // When
        dockerTemplate.removeContainer(containerId, true, true);

        // Then
        verify(dockerClient).removeContainerCmd(containerId);
        verify(removeContainerCmd).withForce(true);
        verify(removeContainerCmd).withRemoveVolumes(true);
        verify(removeContainerCmd).exec();
    }

    @Test
    void shouldGetDockerClient() {
        // When
        DockerClient result = dockerTemplate.getDockerClient();

        // Then
        assertThat(result).isEqualTo(dockerClient);
    }
}

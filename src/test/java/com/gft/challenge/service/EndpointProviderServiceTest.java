package com.gft.challenge.service;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EndpointProviderServiceTest {

    @Test
    public void shouldContainNewlyCreatedEndpointForSessionID() {
        EndpointProviderService endpointProviderService = new EndpointProviderService();

        endpointProviderService.addEndpoint("1");

        assertThat(endpointProviderService.getEndpoint("1")).isPresent();
        assertThat(endpointProviderService.getEndpoint("1")).hasValue(1);
    }

    @Test
    public void shouldReturnOptionalEmptyWhenNoEndpointForGivenSessionID() {
        EndpointProviderService endpointProviderService = new EndpointProviderService();

        assertThat(endpointProviderService.getEndpoint("1")).isEmpty();
    }

    @Test
    public void shouldRemoveExistingEndpointForGivenSessionID() {
        EndpointProviderService endpointProviderService = new EndpointProviderService();

        endpointProviderService.addEndpoint("1");

        assertThat(endpointProviderService.getEndpoint("1")).isPresent();

        endpointProviderService.removeEndpoint("1");

        assertThat(endpointProviderService.getEndpoint("1")).isEmpty();
    }

}

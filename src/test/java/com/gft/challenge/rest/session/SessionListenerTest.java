package com.gft.challenge.rest.session;

import com.gft.challenge.service.EndpointProviderService;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSessionEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SessionListenerTest {

    @Test
    public void shouldAddEndpointWhenOnSessionCreateMethodCalled() {
        EndpointProviderService endpointProviderService = new EndpointProviderService();
        DefaultSessionListener sessionListener = new DefaultSessionListener(endpointProviderService);
        MockHttpSession mockHttpSession = new MockHttpSession();
        assertThat(endpointProviderService.getEndpoint(mockHttpSession.getId())).isEmpty();
        HttpSessionEvent mockHttpSessionEvent = mock(HttpSessionEvent.class);
        when(mockHttpSessionEvent.getSession()).thenReturn(mockHttpSession);

        sessionListener.sessionCreated(mockHttpSessionEvent);

        assertThat(endpointProviderService.getEndpoint(mockHttpSession.getId())).isPresent();
        assertThat(endpointProviderService.getEndpoint(mockHttpSession.getId())).hasValue(1);
    }

    @Test
    public void shouldRemoveEndpointWhenOnSessionDestroyMethodCalled() {
        EndpointProviderService endpointProviderService = new EndpointProviderService();
        DefaultSessionListener sessionListener = new DefaultSessionListener(endpointProviderService);
        MockHttpSession mockHttpSession = new MockHttpSession();
        endpointProviderService.addEndpoint(mockHttpSession.getId());

        HttpSessionEvent mockHttpSessionEvent = mock(HttpSessionEvent.class);
        when(mockHttpSessionEvent.getSession()).thenReturn(mockHttpSession);

        sessionListener.sessionDestroyed(mockHttpSessionEvent);

        assertThat(endpointProviderService.getEndpoint(mockHttpSession.getId())).isEmpty();
    }
}

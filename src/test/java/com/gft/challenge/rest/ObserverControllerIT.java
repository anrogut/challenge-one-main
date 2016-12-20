package com.gft.challenge.rest;

import com.gft.challenge.rx.SubscriptionHandler;
import com.gft.challenge.service.EndpointProviderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ObserverControllerIT {

    @Mock
    private SubscriptionHandler subscriptionHandler;

    @Mock
    private EndpointProviderService endpointProviderService;

    @InjectMocks
    private ObserverController observerController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws IOException {
        this.mockMvc = MockMvcBuilders.standaloneSetup(observerController).build();
    }

    @Test
    public void getConnectShouldReturn200OK() throws Exception {
        when(endpointProviderService.getEndpoint("1")).thenReturn(Optional.of(1));
        mockMvc.perform(get("/connect")).andExpect(status().isOk());
        verify(subscriptionHandler, times(1)).observeDirectory(anyString(), anyInt());
    }

    @Test
    public void getStructureShouldReturn200O0K() throws Exception {
        when(endpointProviderService.getEndpoint("2")).thenReturn(Optional.of(2));
        mockMvc.perform(get("/structure")).andExpect(status().isOk());
        verify(subscriptionHandler, times(1)).sendDirectoryStructure(anyString(), anyInt());
    }
}

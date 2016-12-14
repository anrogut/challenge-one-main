package com.gft.challenge.rest;

import com.gft.challenge.service.ObserverService;
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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ObserverControllerIT {

    @Mock
    private ObserverService observerService;

    @InjectMocks
    private ObserverController observerController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws IOException {
        this.mockMvc = MockMvcBuilders.standaloneSetup(observerController).build();
    }

    @Test
    public void shouldReturn200OK() throws Exception {
        mockMvc.perform(get("/connect")).andExpect(status().isOk());
        verify(observerService, times(1)).observeDirectory(anyString());
    }
}

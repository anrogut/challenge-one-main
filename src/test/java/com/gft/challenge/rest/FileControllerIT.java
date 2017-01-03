package com.gft.challenge.rest;

import com.gft.challenge.service.FileService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileControllerIT {

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileController fileController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws IOException {
        this.mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }

    @Test
    public void shouldReturn400WhenFileNameNotGiven() throws Exception {
        mockMvc.perform(get("/addFile")).andExpect(status().isBadRequest());
        mockMvc.perform(get("/addFile?name")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturn200OK() throws Exception {
        mockMvc.perform(get("/addFile?name=1/2/3.txt")).andExpect(status().isOk());
    }
}

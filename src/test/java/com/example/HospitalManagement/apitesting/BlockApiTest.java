package com.example.HospitalManagement.apitesting;

import com.example.HospitalManagement.Entity.BlockId;
import com.example.HospitalManagement.Repository.BlockRepository;
import com.example.HospitalManagement.Repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BlockApiTest {

    private static final String BLOCK_JSON = "{\"blockFloor\":7,\"blockCode\":2}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void cleanDatabase() {
        roomRepository.deleteAll();
        blockRepository.deleteAll();
    }

    @Test
    @DisplayName("Test 1: Create a Block and It should Exists")
    void createBlock_AndItShouldExists() throws Exception {
        mockMvc.perform(post("/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLOCK_JSON))
                .andExpect(status().isCreated());

        assertThat(blockRepository.existsById(new BlockId(7, 2))).isTrue();
    }

    @Test
    @DisplayName("Test 2: Create a Duplicate Block It Shoud Return 409 Confict error")
    void createDuplicateBlockAndItShouldReturnConfict() throws Exception {
        mockMvc.perform(post("/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLOCK_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLOCK_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Test 3: Create With Improper and Malfunctioned Request Body and it should return 400 Bad Request")
    void createWithInvalidReq_AndItShouldReturnConfict() throws Exception {
        mockMvc.perform(post("/blocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test 4: Create With No Body and it should return 400 Bad Request")
    void createWithNoBody_AndItShouldReturnConfict() throws Exception {
        mockMvc.perform(post("/blocks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

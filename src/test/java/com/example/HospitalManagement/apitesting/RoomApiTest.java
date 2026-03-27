package com.example.HospitalManagement.apitesting;

import com.example.HospitalManagement.Entity.Block;
import com.example.HospitalManagement.Entity.Room;
import com.example.HospitalManagement.Repository.BlockRepository;
import com.example.HospitalManagement.Repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BlockRepository blockRepository;

    private Block testBlock;

    @BeforeEach
    void setUp() {
        testBlock = new Block(1, 100);
        blockRepository.save(testBlock);

        Room room1 = new Room(101, "ICU", false, testBlock);
        Room room2 = new Room(102, "General", false, testBlock);
        Room room3 = new Room(103, "ICU", true, testBlock);

        roomRepository.saveAll(List.of(room1, room2, room3));
    }

    @AfterEach
    void tearDown() {
        roomRepository.deleteAll();
        blockRepository.deleteAll();
    }

    @Test
    @DisplayName("API Test1: findByUnavailable=false should return 200 with room data")
    void testFindByUnavailable_False_ReturnsOk() throws Exception {
        mockMvc.perform(get("/rooms/search/findByUnavailable")
                        .param("unavailable", "false")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms").isArray())
                .andExpect(jsonPath("$._embedded.rooms[*].unavailable").exists())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    @DisplayName("API Test2: findByRoomType=ICU should return 200 with room data")
    void testFindByRoomType_ICU_ReturnsOk() throws Exception {
        mockMvc.perform(get("/rooms/search/findByRoomType")
                        .param("type", "ICU")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms").isArray())
                .andExpect(jsonPath("$._embedded.rooms[*].roomType").exists())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    @DisplayName("API Test3: findByRoomType=NonExistent should return 200 with empty result")
    void testFindByRoomType_NonExistent_ReturnsEmpty() throws Exception {
        mockMvc.perform(get("/rooms/search/findByRoomType")
                        .param("type", "NonExistent")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }


    @Test
    @DisplayName("API Test 4: Create Room With Correct Data and it should get Created")
    void createRoom_WithValidData_ReturnsCreated() throws Exception {
        String body = """
                {
                  "roomNumber": 201,
                  "roomType": "Observation",
                  "blockFloor": 1,
                  "blockCode": 100,
                  "unavailable": false
                }
                """;

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        assertThat(roomRepository.existsById(201)).isTrue();
    }

    @Test
    @DisplayName("API Test 5: Create Room With No Existing Block (blockFloor and blockCode) and it should return 400 Bad Request")
    void createRoom_WithNonExistentBlock_ReturnsBadRequest() throws Exception {
        String body = """
                {
                  "roomNumber": 202,
                  "roomType": "ICU",
                  "blockFloor": 99,
                  "blockCode": 99,
                  "unavailable": false
                }
                """;

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("API Test 6: Create Room With No blockFloor and blockCode in JSON and it should return 400 Bad Request")
    void createRoom_WithoutBlockFloorAndBlockCode_ReturnsBadRequest() throws Exception {
        String body = """
                {
                  "roomNumber": 301,
                  "roomType": "ICU",
                  "unavailable": false
                }
                """;

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("API Test 7: Create Room With No body and it should return 400 Bad Request")
    void createRoom_WithEmptyBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("API Test 8: Create Room with Invalid Data and it should return 400 Bad Request")
    void createRoom_WithMalformedJson_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-json"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRoomCreateBodies")
    @DisplayName("Create Room: invalid field combinations return 400 Bad Request")
    void createRoom_InvalidInputCombinations_ReturnsExpectedStatus(String description, String jsonBody,
            int expectedHttpStatus) throws Exception {
        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().is(expectedHttpStatus));
    }

    private static Stream<Arguments> invalidRoomCreateBodies() {
        String roomType31Chars = "A".repeat(31);
        int badRequest = HttpStatus.BAD_REQUEST.value();
        return Stream.of(
                Arguments.of("blank roomType",
                        "{\"roomNumber\":411,\"roomType\":\"\",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                        badRequest),
                Arguments.of("whitespace-only roomType",
                        "{\"roomNumber\":412,\"roomType\":\"   \",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                        badRequest),
                Arguments.of("roomType longer than 30 characters",
                        "{\"roomNumber\":413,\"roomType\":\"" + roomType31Chars
                                + "\",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                        badRequest),
                Arguments.of("null roomType",
                        "{\"roomNumber\":414,\"roomType\":null,\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                        badRequest),
                Arguments.of("null unavailable",
                        "{\"roomNumber\":415,\"roomType\":\"ICU\",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":null}",
                        badRequest),
                Arguments.of("null blockFloor",
                        "{\"roomNumber\":416,\"roomType\":\"ICU\",\"blockFloor\":null,\"blockCode\":100,\"unavailable\":false}",
                        badRequest),
                Arguments.of("null blockCode",
                        "{\"roomNumber\":417,\"roomType\":\"ICU\",\"blockFloor\":1,\"blockCode\":null,\"unavailable\":false}",
                        badRequest),
                Arguments.of("null roomNumber (Hibernate requires assigned id before persist)",
                        "{\"roomNumber\":null,\"roomType\":\"ICU\",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                        badRequest),
                Arguments.of("roomNumber has wrong JSON type",
                        "{\"roomNumber\":\"not-a-number\",\"roomType\":\"ICU\",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                        badRequest),
                Arguments.of("missing roomNumber property (Hibernate requires assigned id before persist)",
                        "{\"roomType\":\"ICU\",\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                        badRequest),
                Arguments.of("missing roomType property",
                        "{\"roomNumber\":418,\"blockFloor\":1,\"blockCode\":100,\"unavailable\":false}",
                        badRequest),
                Arguments.of("missing unavailable property",
                        "{\"roomNumber\":419,\"roomType\":\"ICU\",\"blockFloor\":1,\"blockCode\":100}",
                        badRequest)
        );
    }
}

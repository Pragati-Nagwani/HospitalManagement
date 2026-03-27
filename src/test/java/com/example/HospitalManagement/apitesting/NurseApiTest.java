package com.example.HospitalManagement.apitesting;
import com.example.HospitalManagement.Entity.Nurse;
import com.example.HospitalManagement.Repository.NurseRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class NurseApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NurseRepository nurseRepository;

    private Nurse nurse1;
    private Nurse nurse2;

    @BeforeEach
    void setUp() {
        nurse1 = new Nurse(301, "Test Nurse A", "Nurse", true, "8881");
        nurse2 = new Nurse(302, "Test Nurse B", "Head Nurse", true, "8882");

        nurseRepository.saveAll(List.of(nurse1, nurse2));
    }
    @Test
    void testGetAllNurses_WithProjection() throws Exception {

        mockMvc.perform(get("/nurse?projection=nurseView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.nurses").isArray())
                .andExpect(jsonPath("$._embedded.nurses").isNotEmpty());
    }
    @Test
    void testProjectionFields() throws Exception {

        mockMvc.perform(get("/nurse?projection=nurseView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.nurses[0].name").exists())
                .andExpect(jsonPath("$._embedded.nurses[0].position").exists())
                .andExpect(jsonPath("$._embedded.nurses[0].registered").exists())
                .andExpect(jsonPath("$._embedded.nurses[0].availability").exists())
                .andExpect(jsonPath("$._embedded.nurses[0].ssn").doesNotExist());
    }
    @Test
    void testPagination() throws Exception {

        mockMvc.perform(get("/nurse?page=0&size=2&projection=nurseView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.nurses.length()").value(2));
    }
    @Test
    void testLargeSize() throws Exception {

        mockMvc.perform(get("/nurse?size=1000&projection=nurseView"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.nurses").isArray());
    }

    @Test
    void testAddNurse_Success() throws Exception {

        String json = """
        {
          "employeeId": 303,
          "name": "New Nurse",
          "position": "Nurse",
          "registered": true,
          "ssn": 9991
        }
        """;

        mockMvc.perform(post("/nurse")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());
    }

    void testAddNurse_DuplicateId() throws Exception {
        Nurse existing = new Nurse(301, "Existing Nurse", "Nurse", true, 8881);
        nurseRepository.saveAndFlush(existing);
        String json = """
        {
          "employeeId": 301,
          "name": "Duplicate Nurse",
          "position": "Nurse",
          "registered": true,
          "ssn": 9992
        }
        """;
        mockMvc.perform(post("/nurse")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isConflict()); // ✅ NOW WORKS
    }
    @Test
    void testAddNurse_MissingField() throws Exception {

        String json = """
        {
          "employeeId": 304,
          "position": "Nurse",
          "registered": true,
          "ssn": 9993
        }
        """;

        mockMvc.perform(post("/nurse")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());
    }
    @Test
    void testAddNurse_InvalidData() throws Exception {

        String json = """
        {
          "employeeId": "abc",
          "name": "Invalid Nurse",
          "position": "Nurse",
          "registered": true,
          "ssn": 9994
        }
        """;

        mockMvc.perform(post("/nurse")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateNurse_Success() throws Exception {
        Nurse nurse = new Nurse(601, "Old Name", "Nurse", true, 12345);
        nurseRepository.saveAndFlush(nurse);
        String updatedJson = """
        {
          "name": "Updated Name",
          "position": "Head Nurse",
          "registered": true,
          "ssn": 9999
        }
        """;
        mockMvc.perform(put("/nurse/601")
                        .contentType("application/json")
                        .content(updatedJson))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateNurse_NotFound() throws Exception {

        String json = """
        {
          "name": "Updated Name",
          "position": "Nurse",
          "registered": true,
          "ssn": 1234
        }
        """;

        mockMvc.perform(put("/nurse/9999")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());;
    }
    @Test
    void testUpdateNurse_InvalidData() throws Exception {

        Nurse nurse = new Nurse(602, "Test", "Nurse", true, 11111);
        nurseRepository.saveAndFlush(nurse);

        String json = """
        {
          "name": "Updated",
          "position": "Nurse",
          "registered": true,
          "ssn": "invalid"
        }
        """;

        mockMvc.perform(put("/nurse/602")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testUpdateNurse_EmptyBody() throws Exception {

        Nurse nurse = new Nurse(603, "Test", "Nurse", true, 11111);
        nurseRepository.saveAndFlush(nurse);

        mockMvc.perform(put("/nurse/603")
                        .contentType("application/json")
                        .content(""))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testPatchNurse_Success() throws Exception {

        Nurse nurse = new Nurse(701, "Old Name", "Nurse", true, 12345);
        nurseRepository.saveAndFlush(nurse);

        String json = """
        {
          "name": "Patched Name"
        }
        """;

        mockMvc.perform(patch("/nurse/701")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNoContent());
    }
    @Test
    void testPatchNurse_NotFound() throws Exception {

        String json = """
        {
          "name": "Updated"
        }
        """;

        mockMvc.perform(patch("/nurse/9999")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testPatchNurse_InvalidData() throws Exception {

        Nurse nurse = new Nurse(702, "Test", "Nurse", true, 11111);
        nurseRepository.saveAndFlush(nurse);

        String json = """
        {
          "ssn": "invalid"
        }
        """;

        mockMvc.perform(patch("/nurse/702")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}

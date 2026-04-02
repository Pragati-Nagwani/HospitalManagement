package com.example.HospitalManagement.apitesting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;

import com.example.HospitalManagement.Entity.Medication;
import com.example.HospitalManagement.Repository.MedicationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MedicationRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MedicationRepository repo;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        repo.deleteAll();
        repo.flush();
    }

    // ✅ Test 1 : Get All
    @Test
    void testGetAllMedications() throws Exception {

        Medication m1 = new Medication();
        m1.setCode(10);
        m1.setName("MedOne");
        m1.setBrand("BrandOne");
        m1.setDescription("Description One");

        Medication m2 = new Medication();
        m2.setCode(20);
        m2.setName("MedTwo");
        m2.setBrand("BrandTwo");
        m2.setDescription("Description Two");

        repo.save(m1);
        repo.save(m2);
        repo.flush();

        mockMvc.perform(get("/allMedications")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.medications", hasSize(2)));
    }

    // ✅ Test 2 : Get by ID
    @Test
    void testGetMedicationById() throws Exception {

        Medication m = new Medication();
        m.setCode(30);
        m.setName("Paracetamol");
        m.setBrand("ABC");
        m.setDescription("Pain Relief Medicine");

        repo.save(m);

        mockMvc.perform(get("/medications/30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Paracetamol"));
    }

    // ✅ Test 3 : Create
    @Test
    void testCreateMedication() throws Exception {

        Medication med = new Medication();
        med.setCode(101);
        med.setName("NewMed101");          // ✅ valid
        med.setBrand("BrandX");            // ✅ valid
        med.setDescription("Test Medicine"); // ✅ safe

        String json = objectMapper.writeValueAsString(med);

        mockMvc.perform(post("/allMedications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());

        List<Medication> list = repo.findByName("NewMed101");
        assertFalse(list.isEmpty());
    }

    // ✅ Test 4 : Update
    @Test
    void testUpdateMedication() throws Exception {

        Medication m = new Medication();
        m.setCode(50);
        m.setName("OldName");
        m.setBrand("Brand");
        m.setDescription("Description");

        repo.save(m);

        String payload = "{\"name\": \"UpdatedName\"}";

        mockMvc.perform(patch("/allMedications/50")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().is2xxSuccessful());

        Medication updated = repo.findById(50).orElseThrow();
        assertEquals("UpdatedName", updated.getName());
    }

    // ✅ Test 5 : Not Found
    @Test
    void testMedicationNotFound() throws Exception {
        mockMvc.perform(get("/allMedications/9999"))
                .andExpect(status().isNotFound());
    }
}
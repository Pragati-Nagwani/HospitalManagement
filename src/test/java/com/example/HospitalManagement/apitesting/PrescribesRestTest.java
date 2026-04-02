package com.example.HospitalManagement.apitesting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.HospitalManagement.Entity.Medication;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Entity.Prescribes;
import com.example.HospitalManagement.Repository.MedicationRepository;
import com.example.HospitalManagement.Repository.PatientRepository;
import com.example.HospitalManagement.Repository.PhysicianRepository;
import com.example.HospitalManagement.Repository.PrescribesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PrescribesRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrescribesRepository repo;

    @Autowired
    private PhysicianRepository physicianRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private MedicationRepository medicationRepo;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ Setup valid base data
    private void setupBaseData() {

        Physician doc = new Physician();
        doc.setEmployeeId(1000);
        doc.setName("Doctor Test");
        doc.setPosition("General");
        doc.setSsn(99999);
        physicianRepo.save(doc);

        Patient p = new Patient();
        p.setSsn(2000);
        p.setName("Patient Test");
        p.setAddress("Address");
        p.setPhone("1234567890");
        p.setInsuranceID(555);
        p.setPcp(doc);
        patientRepo.save(p);

        Medication m = new Medication();
        m.setCode(3000);
        m.setName("MedTest1");
        m.setBrand("BrandX");
        m.setDescription("Description");
        medicationRepo.save(m);
    }

    private Prescribes createPrescribes(Date date) {
        return new Prescribes(
                1000, 2000, 3000,
                date, null, "500mg",
                null, null, null, null);
    }

    // ✅ GET all
    @Test
    void testGetAllPrescribes() throws Exception {

        setupBaseData();
        repo.save(createPrescribes(new Date()));

        mockMvc.perform(get("/prescribes"))
                .andExpect(status().isOk());
    }

    // ✅ CREATE
    @Test
    void testCreatePrescribes() throws Exception {

        setupBaseData();

        Prescribes p = createPrescribes(new Date());
        String json = objectMapper.writeValueAsString(p);

        mockMvc.perform(post("/prescribes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    // ✅ FIND BY PATIENT
    @Test
    void testGetByPatient() throws Exception {

        setupBaseData();
        repo.save(createPrescribes(new Date()));

        mockMvc.perform(get("/prescribes/search/findByPatient")
                .param("patient", "2000"))
                .andExpect(status().isOk());
    }

    // ✅ FIND BY PHYSICIAN
    @Test
    void testFindByPhysician() throws Exception {

        setupBaseData();
        repo.save(createPrescribes(new Date()));

        mockMvc.perform(get("/prescribes/search/findByPhysician")
                .param("physician", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.prescriptions").exists());
    }

    // ✅ FIND BY MEDICATION (WITH PAGINATION REQUIRED)
    @Test
    void testFindByMedication() throws Exception {

        setupBaseData();
        repo.save(createPrescribes(new Date()));

        mockMvc.perform(get("/prescribes/search/findByMedication")
                .param("medication", "3000")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.prescriptions").exists());
    }

    // ✅ FIND BY DOSE
    @Test
    void testFindByDose() throws Exception {

        setupBaseData();
        repo.save(createPrescribes(new Date()));

        mockMvc.perform(get("/prescribes/search/findByDose")
                .param("dose", "500mg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.prescriptions").exists());
    }

    // ✅ NO RESULT CASE
    @Test
    void testFindByPatient_NoResult() throws Exception {

        setupBaseData();

        mockMvc.perform(get("/prescribes/search/findByPatient")
                .param("patient", "9999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.prescriptions").isEmpty());
    }

    // ✅ PAGINATION TEST
    @Test
    void testPagination() throws Exception {

        setupBaseData();

        for (int i = 0; i < 5; i++) {
            repo.save(createPrescribes(
                    new Date(System.currentTimeMillis() + i * 1000)));
        }

        mockMvc.perform(get("/prescribes")
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(2));
    }

    // ✅ MISSING DOSE (Spring Data REST allows it → 201)
    @Test
    void testCreatePrescribes_MissingDose() throws Exception {

        setupBaseData();

        String json = """
                {
                    "physician": 1000,
                    "patient": 2000,
                    "medication": 3000,
                    "date": "2024-01-01T10:00:00"
                }
                """;

        mockMvc.perform(post("/prescribes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    // ✅ INVALID FK (no validation → still 201)
    @Test
    void testCreatePrescribes_InvalidFK() throws Exception {

        String json = """
                {
                    "physician": 9999,
                    "patient": 8888,
                    "medication": 7777,
                    "date": "2024-01-01T10:00:00",
                    "dose": "500mg"
                }
                """;

        mockMvc.perform(post("/prescribes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    // ✅ DUPLICATE PK (Spring may allow overwrite → 201)
    @Test
    void testDuplicatePrescribes() throws Exception {

        setupBaseData();

        Date date = new Date();
        Prescribes p = createPrescribes(date);
        repo.save(p);

        String json = objectMapper.writeValueAsString(p);

        mockMvc.perform(post("/prescribes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    // ✅ INVALID DATE
    @Test
    void testCreatePrescribes_InvalidDate() throws Exception {

        setupBaseData();

        String json = """
                {
                    "physician": 1000,
                    "patient": 2000,
                    "medication": 3000,
                    "date": "invalid-date",
                    "dose": "500mg"
                }
                """;

        mockMvc.perform(post("/prescribes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
}
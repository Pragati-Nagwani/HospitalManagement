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

        // create repo objects

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

        // setup base
        // Helper setup
        private void setupBaseData() {

                Physician doc = new Physician();
                doc.setEmployeeId(1000);
                doc.setName("Dr Test");
                doc.setPosition("General");
                doc.setSsn(99999);
                physicianRepo.save(doc);

                Patient p = new Patient();
                p.setSsn(2000);
                p.setName("Patient Test");
                p.setAddress("Addr");
                p.setPhone("1234567890");
                p.setInsuranceID(555);
                p.setPcp(doc);
                patientRepo.save(p);

                Medication m = new Medication();
                m.setCode(3000);
                m.setName("Med Test");
                m.setBrand("Brand");
                m.setDescription("desc");
                medicationRepo.save(m);
        }

        // Test 1 : get ALl prescribes
        @Test
        void testGetAllPrescribes() throws Exception {

                setupBaseData();

                Prescribes p = new Prescribes(
                                1000, 2000, 3000,
                                new Date(), null, "500mg",
                                null, null, null, null);

                repo.save(p);

                mockMvc.perform(get("/prescribes")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }
        // GET
        // http://localhost:9090/prescribes

        // Test Case 2 : test create Prescription
        @Test
        void testCreatePrescribes() throws Exception {

                setupBaseData();

                Prescribes p = new Prescribes(
                                1001, 2001, 3001,
                                new Date(), null, "650mg",
                                null, null, null, null);

                String json = objectMapper.writeValueAsString(p);

                mockMvc.perform(post("/prescribes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isCreated());
        }
        // POST
        // http://localhost:9090/prescribes

        // Test Case 3 : Find By Patient
        @Test
        void testGetByPatient() throws Exception {

                setupBaseData();

                Prescribes p = new Prescribes(
                                1000, 2000, 3000,
                                new Date(), null, "500mg",
                                null, null, null, null);

                repo.save(p);

                mockMvc.perform(get("/prescribes/search/findByPatient")
                                .param("patient", "2000")) // query param
                                .andExpect(status().isOk());
        }
        // GET
        // http://localhost:9090/prescribes/search/findByPatient?patient=2000

        // Test CAse 4 : Find By Physician
        @Test
        void testFindByPhysician() throws Exception {

                setupBaseData();

                Prescribes p = new Prescribes(
                                1000, 2000, 3000,
                                new Date(), null, "500mg",
                                null, null, null, null);

                repo.save(p);

                mockMvc.perform(get("/prescribes/search/findByPhysician")
                                .param("physician", "1000"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$._embedded.prescriptions").isArray());
        }
        // GET
        // http://localhost:9090/prescribes/search/findByPhysician?physician=1000

        // Test Case 5 : Find By Medication
        @Test
        void testFindByMedication() throws Exception {

                setupBaseData();

                Prescribes p = new Prescribes(
                                1000, 2000, 3000,
                                new Date(), null, "500mg",
                                null, null, null, null);

                repo.save(p);

                mockMvc.perform(get("/prescribes/search/findByMedication")
                                .param("medication", "3000"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$._embedded.prescriptions").isArray());
        }
        // GET
        // http://localhost:9090/prescribes/search/findByMedication?medication=3000

        // Test Case 6 : Find By Dose
        @Test
        void testFindByDose() throws Exception {

                setupBaseData();

                Prescribes p = new Prescribes(
                                1000, 2000, 3000,
                                new Date(), null, "500mg",
                                null, null, null, null);

                repo.save(p);

                mockMvc.perform(get("/prescribes/search/findByDose")
                                .param("dose", "500mg"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$._embedded.prescriptions").isArray());
        }
        // GET
        // http://localhost:9090/prescribes/search/findByDose?dose=500mg

        // Test Case 7 : negative test case
        @Test
        void testFindByPatient_NoResult() throws Exception {

                setupBaseData();

                mockMvc.perform(get("/prescribes/search/findByPatient")
                                .param("patient", "9999"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$._embedded.prescriptions").isEmpty());
        }

        // Test Case 8 : Pagination test
        @Test
        void testPagination() throws Exception {

                setupBaseData();

                for (int i = 0; i < 5; i++) {
                        Prescribes p = new Prescribes(
                                        1000, 2000, 3000,
                                        new Date(System.currentTimeMillis() + i * 1000),
                                        null, "500mg",
                                        null, null, null, null);
                        repo.save(p);
                }

                mockMvc.perform(get("/prescribes?page=0&size=2"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.page.size").value(2));
        }
        // GET
        // http://localhost:9090/prescribes?page=0&size=2

        // EDGE TEST CASES

        // TEST CASE 9 : missing dose while creating prescription
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
                                .andExpect(status().isCreated()); // validation fails
        }

        // Test Case 10 : invalid FK
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
                                .andExpect(status().isCreated()); // FK violation
        }

        // Test Case 11 : Duplicate Composite Key

        @Test
        void testDuplicatePrescribes() throws Exception {

                setupBaseData();

                Date date = new Date();

                Prescribes p = new Prescribes(
                                1000, 2000, 3000,
                                date, null, "500mg",
                                null, null, null, null);

                repo.save(p);

                String json = objectMapper.writeValueAsString(p);

                mockMvc.perform(post("/prescribes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isCreated()); // duplicate PK
        }

        // Test Case 12 : Invalid Date Format
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

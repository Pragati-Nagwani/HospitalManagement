package com.example.HospitalManagement.apitesting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Repository.PhysicianRepository;

import jakarta.transaction.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PatientApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PhysicianRepository physicianRepository;

    @BeforeEach
    void seedPcpPhysician() {
        Physician pcp = new Physician();
        pcp.setEmployeeId(100);
        pcp.setName("Dr. PCP");
        pcp.setPosition("General");
        pcp.setSsn(888888888);
        physicianRepository.save(pcp);
    }

    // pagination test
    @Test
    void testGetAllPatients_PaginationSuccess() throws Exception {
        mockMvc.perform(get("/patients?page=0&size=5"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void testGetAllPatients_PageOutOfRange() throws Exception {
        mockMvc.perform(get("/patients?page=10&size=5"))
                .andExpect(status().isOk());
    }


    @Test
    void testGetAllPatients_NoDataExists() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk());
    }

    // add patient
    @Test
    void testCreatePatient_ValidData_ReturnsCreated() throws Exception {

        String patientJson = """
                {
                    "ssn": 100000018,
                    "name": "ved",
                    "address": "NGP",
                    "phone": "9999999999",
                    "insuranceID": 12345,
                    "pcp": "/allPhysician/100"
                }
                """;

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientJson))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @Test
    void testCreatePatient_InvalidFormat_ReturnsBadRequest() throws Exception {

        String invalidJson = """
                {
                    "ssn": "AB123",
                    "name": "",
                    "address": "Pune",
                    "phone": "9999999999",
                    "insuranceID": 12345,
                    "pcp": "http://localhost:9090/allPhysician/100"
                }
                """;

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    // update the patient
    @Test
    void testUpdatePatientPhone_Success() throws Exception {
        // 1. Create a Patient first (Linking to Physician 100 from your @BeforeEach)
        String createJson = """
                {
                    "ssn": 100000020,
                    "name": "Arjun",
                    "address": "Nagpur",
                    "phone": "1234567890",
                    "insuranceID": 55555,
                    "pcp": "/physicians/100"
                }
                """;

        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
                .andExpect(status().isCreated());

        // 2. Update only the phone number
        String updateJson = """
                {
                    "phone": "9876543210"
                }
                """;

        mockMvc.perform(patch("/patients/100000020")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isNoContent()); // Spring Data REST returns 204 No Content for PATCH/PUT

        // 3. Verify the change
        mockMvc.perform(get("/patients/100000020"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("9876543210"));
    }

    @Test
    void testUpdatePatient_NotFound() throws Exception {
        String updateJson = "{\"address\": \"Mumbai\"}";

        // Attempting to update a non-existent SSN
        mockMvc.perform(patch("/patients/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isNotFound());
    }


@Test
void testSearchByName_ExactMatch_ReturnsPatient() throws Exception {
    // 1. Create patient
    String patientJson = """
            {
                "ssn": 100000040,
                "name": "Amit Sharma",
                "address": "Nagpur",
                "phone": "9890000001",
                "insuranceID": 11111,
                "pcp": "/allPhysician/100"
            }
            """;

    mockMvc.perform(post("/patients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(patientJson))
            .andExpect(status().isCreated());

    // 2. Search by exact name
    mockMvc.perform(get("/patients/search/findByNameContainingIgnoreCase?name=Amit Sharma&page=0&size=5"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.patients[0].name").value("Amit Sharma"));
}

@Test
void testSearchByName_PartialFirstName_ReturnsMultiple() throws Exception {
    // 1. Create two patients with same first name
    String p1 = """
            {
                "ssn": 100000041,
                "name": "Sneha Reddy",
                "address": "Hyderabad",
                "phone": "9890000002",
                "insuranceID": 22222,
                "pcp": "/allPhysician/100"
            }
            """;
    String p2 = """
            {
                "ssn": 100000042,
                "name": "Sneha Kulkarni",
                "address": "Pune",
                "phone": "9890000003",
                "insuranceID": 33333,
                "pcp": "/allPhysician/100"
            }
            """;

    mockMvc.perform(post("/patients").contentType(MediaType.APPLICATION_JSON).content(p1))
            .andExpect(status().isCreated());
    mockMvc.perform(post("/patients").contentType(MediaType.APPLICATION_JSON).content(p2))
            .andExpect(status().isCreated());

    // 2. Partial search — both Sneha patients should appear
    mockMvc.perform(get("/patients/search/findByNameContainingIgnoreCase?name=Sneha&page=0&size=5"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.patients", hasSize(greaterThanOrEqualTo(2))));
}

@Test
void testSearchByName_CaseInsensitive_ReturnsResult() throws Exception {
    // 1. Create patient
    String patientJson = """
            {
                "ssn": 100000043,
                "name": "Rahul Patil",
                "address": "Pune",
                "phone": "9890000004",
                "insuranceID": 44444,
                "pcp": "/allPhysician/100"
            }
            """;

    mockMvc.perform(post("/patients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(patientJson))
            .andExpect(status().isCreated());

    // 2. Lowercase input — should still match
    mockMvc.perform(get("/patients/search/findByNameContainingIgnoreCase?name=rahul patil&page=0&size=5"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.patients[0].name").value("Rahul Patil"));
}

@Test
void testSearchByName_PartialLastName_ReturnsResult() throws Exception {
    // 1. Create patient
    String patientJson = """
            {
                "ssn": 100000044,
                "name": "Arjun Nair",
                "address": "Kochi",
                "phone": "9890000005",
                "insuranceID": 55555,
                "pcp": "/allPhysician/100"
            }
            """;

    mockMvc.perform(post("/patients")
            .contentType(MediaType.APPLICATION_JSON)
            .content(patientJson))
            .andExpect(status().isCreated());

    // 2. Search by last name only
    mockMvc.perform(get("/patients/search/findByNameContainingIgnoreCase?name=Nair&page=0&size=5"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.patients[0].name").value("Arjun Nair"));
}

@Test
void testSearchByName_NoMatch_ReturnsEmptyList() throws Exception {
    mockMvc.perform(get("/patients/search/findByNameContainingIgnoreCase?name=NonExistentXYZ&page=0&size=5"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.patients").isArray())
            .andExpect(jsonPath("$._embedded.patients").isEmpty());
}

@Test
void testSearchByName_WithPagination_TotalPagesCorrect() throws Exception {
    // 1. Create two patients with a unique name
    String p1 = """
            {
                "ssn": 100000045,
                "name": "Zyvanto Sharma",
                "address": "Nagpur",
                "phone": "9890000006",
                "insuranceID": 66666,
                "pcp": "/allPhysician/100"
            }
            """;
    String p2 = """
            {
                "ssn": 100000046,
                "name": "Zyvanto Joshi",
                "address": "Delhi",
                "phone": "9890000007",
                "insuranceID": 77777,
                "pcp": "/allPhysician/100"
            }
            """;

    mockMvc.perform(post("/patients").contentType(MediaType.APPLICATION_JSON).content(p1))
            .andExpect(status().isCreated());
    mockMvc.perform(post("/patients").contentType(MediaType.APPLICATION_JSON).content(p2))
            .andExpect(status().isCreated());

    // 2. size=1 → only 1 per page, at least 2 total
    mockMvc.perform(get("/patients/search/findByNameContainingIgnoreCase?name=Zyvanto&page=0&size=1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.patients", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(2)))
            .andExpect(jsonPath("$.page.totalPages",    greaterThanOrEqualTo(2)));
}
}

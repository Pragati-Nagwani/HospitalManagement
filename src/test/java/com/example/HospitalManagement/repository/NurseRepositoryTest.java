package com.example.HospitalManagement.repository;

import com.example.HospitalManagement.Entity.Nurse;
import com.example.HospitalManagement.Repository.NurseRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
class NurseRepositoryTest {

    @Autowired
    private NurseRepository nurseRepository;

    private Nurse nurse1;
    private Nurse nurse2;
    @BeforeEach
    void setUp() {
        nurse1 = new Nurse(201, "Test Nurse 1", "Nurse", true, "9991");
        nurse2 = new Nurse(202, "Test Nurse 2", "Nurse", true, "9992");

        nurseRepository.saveAll(List.of(nurse1, nurse2));
    }

    @Test
    void testFindAll_DataExists() {

        Page<Nurse> result = nurseRepository.findAll(PageRequest.of(0, 5));

        assertNotNull(result);
        assertTrue(result.getContent().size() >= 2);
    }
    @Test
    void testFindAll_Pagination() {

        Page<Nurse> result = nurseRepository.findAll(PageRequest.of(0, 2));

        assertEquals(2, result.getContent().size());
    }
    @Test
    void testFindAll_InvalidPage() {

        assertThrows(IllegalArgumentException.class, () -> {
            nurseRepository.findAll(PageRequest.of(-1, 5));
        });
    }
    @Test
    void testSave_NurseSuccess() {
        Nurse nurse = new Nurse(401, "Repo Nurse", "Nurse", true, 12345);
        Nurse saved = nurseRepository.save(nurse);
        assertNotNull(saved);
        assertEquals(401, saved.getEmployeeId());
    }

    @Test
    void testSave_MissingField() {
        Nurse nurse = new Nurse();
        nurse.setEmployeeId(403);
        assertThrows(Exception.class, () -> {
            nurseRepository.saveAndFlush(nurse);
        });
    }

}

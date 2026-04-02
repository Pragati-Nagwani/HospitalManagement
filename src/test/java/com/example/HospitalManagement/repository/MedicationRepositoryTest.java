package com.example.HospitalManagement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.HospitalManagement.Entity.Medication;
import com.example.HospitalManagement.Repository.MedicationRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class MedicationRepositoryTest {
    @Autowired
    private MedicationRepository repository;

    // create Base Data
    @BeforeEach
    void setup() {
        Medication med = new Medication();
        med.setCode(1);
        med.setName("ProcrastinX");
        med.setBrand("TestBrand");
        med.setDescription("Test");

        repository.save(med);
    }

    // debug test
    @Test
    void debugTest() {
        System.out.println("Count = " + repository.count());
        repository.findAll().forEach(System.out::println);
    }

    // Test case 1 : find by a particular Id
    @Test
    void testFindById_Exists() {
        Optional<Medication> getmed = repository.findById(1);

        assertTrue(getmed.isPresent());
        assertEquals("ProcrastinX", getmed.get().getName());
    }

    // Test case 2 : check if an Id not Exists
    @Test
    void testFindById_NotExists() {
        Optional<Medication> med = repository.findById(9999);
        assertFalse(med.isPresent());
    }

    // Test 3 : get all medications
    @Test
    void testFindAllMedications() {
        List<Medication> med_list = repository.findAll();

        assertFalse(med_list.isEmpty());
    }

    // Test case 4 : findByName Test case
    @Test
    void testFindMedicationByName() {
        List<Medication> medication_list = repository.findByName("ProcrastinX");

        // list should not be Empty
        assertFalse(medication_list.isEmpty());

        // check whether correct name retrieved from DB
        assertEquals("ProcrastinX", medication_list.get(0).getName());
    }

    // Test 5 : Find By Brand Test Case
    @Test
    void testFindMedicationByBrandName() {
        List<Medication> medication_list = repository.findByBrand("TestBrand");

        // list should not be Empty
        assertFalse(medication_list.isEmpty());

        // check whether correct name retrieved from DB
        assertEquals("TestBrand", medication_list.get(0).getBrand());
    }

    // Test 6 : save new Medication
    @Test
    void testSaveNewMedication() {

        // create a Medication object and set values
        Medication new_med = new Medication();

        new_med.setCode(3);
        new_med.setName("Dolo");
        new_med.setBrand("DoloCompany");
        new_med.setDescription("Fever Cure");

        // save
        Medication saved = repository.save(new_med);

        // check it is not null
        assertNotNull(saved);
        assertEquals("Dolo", saved.getName());
    }

    // Test 7 : Test count of Medications
    @Test
    void testMedicationCount() {
        Long count = repository.count();

        assertTrue(count > 0);
    }
}

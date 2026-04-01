package com.example.HospitalManagement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.HospitalManagement.Entity.Medication;
import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Entity.Physician;
import com.example.HospitalManagement.Entity.Prescribes;
import com.example.HospitalManagement.Entity.PrescribesId;
import com.example.HospitalManagement.Repository.MedicationRepository;
import com.example.HospitalManagement.Repository.PatientRepository;
import com.example.HospitalManagement.Repository.PhysicianRepository;
import com.example.HospitalManagement.Repository.PrescribesRepository;

import jakarta.transaction.Transactional;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PrescribesRepositoryTest {

    // create required repositories
    @Autowired
    private PrescribesRepository prescribesRepository;

    @Autowired
    private PhysicianRepository physicianRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicationRepository medicationRepository;

    // Helper method to insert required parent data
    private void insertBaseData() {

        Physician doctor = new Physician();
        doctor.setEmployeeId(1);
        doctor.setName("Dr. Strange");
        doctor.setPosition("Surgeon");
        doctor.setSsn(1111);
        physicianRepository.save(doctor);

        Patient patient = new Patient();
        patient.setSsn(101);
        patient.setName("John Doe");
        patient.setAddress("Test Address");
        patient.setPhone("1234567890");
        patient.setInsuranceID(555);
        patient.setPcp(doctor);
        patientRepository.save(patient);

        Medication med = new Medication();
        med.setCode(201);
        med.setName("Paracetamol");
        med.setBrand("Cipla");
        med.setDescription("Pain relief medicine");
        medicationRepository.save(med);
    }

    // Test case 1 : Save Prescribes
    @Test
    void testSavePrescribes() {

        // insert required base data for testing purpose
        insertBaseData();

        // create new prescription
        Prescribes p = new Prescribes();
        p.setPhysician(1);
        p.setPatient(101);
        p.setMedication(201);
        p.setDate(new Date());
        p.setDose("500mg");

        // save newly created prescription
        Prescribes saved = prescribesRepository.save(p);

        // check and Test
        assertNotNull(saved);
        assertEquals("500mg", saved.getDose());
    }

    // Test case 2 : test Find by Id
    @Test
    void testFindById() {

        // insert base data
        insertBaseData();

        // create a prescription for testing and save
        Date date = new Date();
        Prescribes p = new Prescribes(1, 101, 201, date, null, "500mg",
                null, null, null, null);
        prescribesRepository.save(p);

        // craete new id
        PrescribesId id = new PrescribesId(1, 101, 201, date);

        // find by id
        Optional<Prescribes> result = prescribesRepository.findById(id);

        // check test
        assertTrue(result.isPresent());
    }

    // Test Case 3 : Test Find By Id Not Exists
    @Test
    void testFindById_NotExists() {

        PrescribesId id = new PrescribesId(99, 99, 99, new Date());

        Optional<Prescribes> result = prescribesRepository.findById(id);

        assertFalse(result.isPresent());
    }

    // Test Case 4 : Find All
    @Test
    void testFindAll() {

        // insert base data for testing
        insertBaseData();

        // create multiple prescriptions
        Prescribes p1 = new Prescribes(1, 101, 201, new Date(), null, "500mg",
                null, null, null, null);

        Prescribes p2 = new Prescribes(1, 101, 201, new Date(System.currentTimeMillis() + 1000),
                null, "650mg", null, null, null, null);

        // save them
        prescribesRepository.save(p1);
        prescribesRepository.save(p2);

        // fetch All
        List<Prescribes> list = prescribesRepository.findAll();

        // chek size and Test whether all retrieved or not
        assertEquals(2, list.size());
    }

    // Test case 5 : Find By patient
    // @Test
    // void testFindByPatient() {

    // createPhysician();
    // createPatient();
    // createMedication();

    // Date date = new Date();

    // Prescribes p = new Prescribes(1, 101, 201, date, null, "500mg",
    // null, null, null, null);

    // prescribesRepository.save(p);

    // List<Prescribes> list = prescribesRepository.findByPatient(101);

    // assertFalse(list.isEmpty());
    // assertEquals(101, list.get(0).getPatient());
    // }
}

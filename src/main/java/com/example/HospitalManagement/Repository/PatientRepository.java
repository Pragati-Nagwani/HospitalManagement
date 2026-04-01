package com.example.HospitalManagement.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.validation.annotation.Validated;

import com.example.HospitalManagement.Entity.Patient;
import com.example.HospitalManagement.Projection.PatientProjection;

@RepositoryRestResource(path = "patients", excerptProjection = PatientProjection.class)
@Validated
public interface PatientRepository extends JpaRepository<Patient, Integer>{

    List<Patient> findByNameIgnoreCase(String name);
    Page<Patient> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}

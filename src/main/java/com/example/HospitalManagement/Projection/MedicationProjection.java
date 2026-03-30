package com.example.HospitalManagement.Projection;

import org.springframework.data.rest.core.config.Projection;
import com.example.HospitalManagement.Entity.Medication;

@Projection(name = "medicationView", types = Medication.class)
public interface MedicationProjection {

    Integer getCode();
    String getName();
    String getBrand();
    String getDescription();
}

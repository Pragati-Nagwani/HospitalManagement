package com.example.HospitalManagement.Projection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import com.example.HospitalManagement.Entity.Patient;

@Projection(name = "patientSummary", types = { Patient.class })
public interface PatientProjection {
Integer getSsn();
    String getName();
    String getAddress();
    String getPhone();

    @Value("#{target.pcp.name}")
    String getPcpName();
}
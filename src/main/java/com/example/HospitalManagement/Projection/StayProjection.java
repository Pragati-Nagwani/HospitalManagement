package com.example.HospitalManagement.Projection;

import com.example.HospitalManagement.Entity.Stay;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "stayDetails", types = { Stay.class })
public interface StayProjection {

    @Value("#{target.patientEntity.name}")
    String getPatientName();
    
    Date getStayStart();
    
    Date getStayEnd();
    
    @Value("#{target.stayEnd != null and target.stayEnd.before(new java.util.Date()) ? 'Completed' : 'Active'}")
    String getStatus();
}

package com.example.HospitalManagement.Projection;

import java.util.Date;
import org.springframework.data.rest.core.config.Projection;
import com.example.HospitalManagement.Entity.Prescribes;


@Projection(name = "prescribesView" , types = Prescribes.class)
public interface PrescribesProjection {
    
    Integer getPhysician();
    Integer getPatient();
    Integer getMedication();
    Date getDate();
    String getDose();
}

// search in postman

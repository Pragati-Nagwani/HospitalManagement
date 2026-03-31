package com.example.HospitalManagement.Projection;

import org.springframework.data.rest.core.config.Projection;
import com.example.HospitalManagement.Entity.Procedure;

@Projection(name = "procedureSummary", types = { Procedure.class })
public interface ProcedureProjection {
    Integer getCode();
    String getName();
    Double getCost();
}


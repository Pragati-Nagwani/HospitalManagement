package com.example.HospitalManagement.Repository;

import java.util.Date;
import java.util.List;

import com.example.HospitalManagement.Entity.Nurse;
import com.example.HospitalManagement.Projection.AppointmentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.example.HospitalManagement.Entity.Appointment;

@RepositoryRestResource(    path = "appointments",
        excerptProjection = AppointmentProjection.class)
public interface AppointmentRepository extends JpaRepository<Appointment, Integer>{

    List<Appointment> findByStartoBetween(Date start, Date end);

    List<Appointment> findByPatientName(String name);

    @RestResource(path = "byNurse", rel = "byNurse")
    List<Appointment> findByPrepNurse(@Param("nurse") Nurse nurse);
}

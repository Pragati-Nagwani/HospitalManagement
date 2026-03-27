package com.example.HospitalManagement.Entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Physician")
public class Physician implements Persistable<Integer> {

    @Id
    @Column(name = "EmployeeID")
    private Integer employeeId;

    @NotNull
    @Size(max = 30)
    @Column(name = "Name", nullable = false, length = 30)
    private String name;

    @NotNull
    @Size(max = 30)
    @Column(name = "Position", nullable = false, length = 30)
    private String position;

    @NotNull
    @Column(name = "SSN", nullable = false)
    private Integer ssn;

    @JsonIgnore
    @OneToMany(mappedBy = "head", fetch = FetchType.LAZY)
    private List<Department> departmentsHeaded;

    @JsonIgnore
    @OneToMany(mappedBy = "physician", fetch = FetchType.LAZY)
    private List<AffiliatedWith> affiliations;

    @JsonIgnore
    @OneToMany(mappedBy = "physician", fetch = FetchType.LAZY)
    private List<TrainedIn> trainings;

    @JsonIgnore
    @OneToMany(mappedBy = "pcp", fetch = FetchType.LAZY)
    private List<Patient> patients;

    @JsonIgnore
    @OneToMany(mappedBy = "physician", fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @JsonIgnore
    @OneToMany(mappedBy = "physician", fetch = FetchType.LAZY)
    private List<Prescribes> prescriptions;

    @JsonIgnore
    @OneToMany(mappedBy = "physician", fetch = FetchType.LAZY)
    private List<Undergoes> proceduresPerformed;

    @Transient
    private boolean isNew=true;
   
    @Override
    public boolean isNew() {
        // TODO Auto-generated method stub
        return this.isNew;
    }

    @PostLoad
    void makrAsNotNew(){
        this.isNew=false;
    }

    @Override
    @Nullable
    public Integer getId() {
        // TODO Auto-generated method stub
        
        return this.employeeId;
    }
}
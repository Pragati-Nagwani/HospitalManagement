package com.example.HospitalManagement.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Nurse")
public class Nurse {

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
    @Column(name = "Registered", nullable = false)
    private Boolean registered;

    @NotNull
    @Column(name = "SSN", nullable = false)
    private Integer ssn;

    @JsonIgnore
    @OneToMany(mappedBy = "prepNurse", fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @JsonIgnore
    @OneToMany(mappedBy = "nurse", fetch = FetchType.LAZY)
    private List<OnCall> onCallSchedules;

    @JsonIgnore
    @OneToMany(mappedBy = "assistingNurse", fetch = FetchType.LAZY)
    private List<Undergoes> assistedProcedures;

    public Nurse(Integer employeeId, String name, String position, Boolean registered, String ssn) {
        this.employeeId = employeeId;
        this.name = name;
        this.position = position;
        this.registered = registered;
        this.ssn = Integer.parseInt(ssn);
    }

    public Nurse(int employeeId, String nurseA, String nurse, boolean registered, int ssn) {
        this.employeeId = employeeId;
        this.name = nurseA;
        this.position = nurse;
        this.registered = registered;
        this.ssn = ssn;
    }

    @Transient
    public String getAvailability() {
        return "AVAILABLE";
    }
}

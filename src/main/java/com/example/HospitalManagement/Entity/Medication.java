package com.example.HospitalManagement.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import org.springframework.data.domain.Persistable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Medication")
public class Medication implements Persistable<Integer>{

    @Id
    @Column(name = "Code")
    @NotNull(message = "Code is required")
    @Max(value = 1000000, message = "Code must be less than 1000000")
    private Integer code;

    @NotNull
    @Column(name = "Name", nullable = false, length = 30)
    @NotBlank(message = "Name is required")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])[A-Za-z0-9 ]+$",
        message = "Name must contain at least one alphabet and can include numbers"
    )
    @Size(max = 50, message = "Name too long")
    private String name;


    @NotBlank(message = "Brand is required")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])[A-Za-z0-9 ]+$",
        message = "Brand must contain alphabets and no special characters"
    )
    @Size(max = 50, message = "Brand too long")
    private String brand;


    @Column(name = "Description", nullable = false, length = 200)
    @NotBlank(message = "Description is required")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])[A-Za-z0-9 .,()-]+$",
        message = "Description contains invalid characters"
    )
    @Size(max = 200, message = "Description too long (max 200)")
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "medicationEntity", fetch = FetchType.LAZY)
    private List<Prescribes> prescriptions;


    // override methods
    @Override
    public Integer getId() {
        // TODO Auto-generated method stub
        return this.code;
    }

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        // TODO Auto-generated method stub
        return true;
    }  
}
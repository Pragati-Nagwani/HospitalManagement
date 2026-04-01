package com.example.HospitalManagement.Repository;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.example.HospitalManagement.Entity.Prescribes;
import com.example.HospitalManagement.Entity.PrescribesId;

@RepositoryRestResource(path = "prescribes", collectionResourceRel = "prescriptions")
public interface PrescribesRepository extends JpaRepository<Prescribes , PrescribesId> {

    List<Prescribes> findByPatient(Integer patient);
    List<Prescribes> findByPhysician(Integer physician);
    // List<Prescribes> findByMedication(Integer medication);
    Page<Prescribes> findByMedication(Integer medication, Pageable pageable);
    List<Prescribes> findByDose(String dose);  
}

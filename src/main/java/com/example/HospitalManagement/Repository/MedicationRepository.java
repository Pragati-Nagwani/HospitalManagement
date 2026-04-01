package com.example.HospitalManagement.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.example.HospitalManagement.Entity.Medication;

@RepositoryRestResource(path = "allMedications" , collectionResourceRel = "medications")
public interface MedicationRepository extends JpaRepository<Medication , Integer> {
     List<Medication> findByName(String name);
     List<Medication> findByBrand(String brandName);

     // searching methods
     // Partial search
    Page<Medication> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String name,
        String brand,
        String description,
        Pageable pageable
     );

}

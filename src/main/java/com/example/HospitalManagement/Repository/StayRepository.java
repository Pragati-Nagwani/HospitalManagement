package com.example.HospitalManagement.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.HospitalManagement.Entity.Stay;
import com.example.HospitalManagement.Projection.StayProjection;
import org.springframework.data.domain.Pageable;

@RepositoryRestResource(path = "stays", excerptProjection = StayProjection.class)
public interface StayRepository extends JpaRepository<Stay, Integer> {

    Page<Stay> findByRoom_RoomNumber(Integer roomNumber, Pageable pageable);

}

package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.Block;
import com.example.HospitalManagement.Entity.BlockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "blocks")
public interface BlockRepository extends JpaRepository<Block, BlockId> {
}

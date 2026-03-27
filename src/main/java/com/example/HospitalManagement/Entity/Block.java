package com.example.HospitalManagement.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.util.List;

@Entity
@Table(name = "Block")
@Getter
@Setter
@IdClass(BlockId.class)
@NoArgsConstructor
public class Block implements Persistable<BlockId> {

    @Id
    @Column(name = "BlockFloor")
    private Integer blockFloor;

    @Id
    @Column(name = "BlockCode")
    private Integer blockCode;

    @JsonIgnore
    @OneToMany(mappedBy = "block", fetch = FetchType.LAZY)
    private List<Room> rooms;

    @JsonIgnore
    @OneToMany(mappedBy = "block", fetch = FetchType.LAZY)
    private List<OnCall> onCallSchedules;

    @JsonIgnore
    @Transient
    private boolean isNew = true;

    @PostLoad
    @PostPersist
    void markAsNotNew() {
        this.isNew = false;
    }

    @Override
    public BlockId getId() {
        // Build and return the composite key object
        return new BlockId(blockFloor, blockCode);
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return isNew;
    }

    public Block(Integer blockFloor, Integer blockCode) {
        this.blockFloor = blockFloor;
        this.blockCode = blockCode;
    }
}
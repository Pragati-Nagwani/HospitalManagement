package com.example.HospitalManagement.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Room")
public class Room implements Persistable<Integer> {

    @Id
    @Column(name = "RoomNumber" , unique = true)
    private Integer roomNumber;

    @NotNull
    @NotBlank
    @Size(max = 30)
    @Column(name = "RoomType", nullable = false, length = 30)
    private String roomType;

    @NotNull
    @Column(name = "BlockFloor", nullable = false)
    private Integer blockFloor;

    @NotNull
    @Column(name = "BlockCode", nullable = false)
    private Integer blockCode;

    @NotNull
    @Column(name = "Unavailable", nullable = false)
    private Boolean unavailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "BlockFloor", referencedColumnName = "BlockFloor", insertable = false, updatable = false),
            @JoinColumn(name = "BlockCode", referencedColumnName = "BlockCode", insertable = false, updatable = false)
    })
    private Block block;

    @JsonIgnore
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Stay> stays;

    public Room(Integer roomNumber, String roomType, Boolean unavailable, Block block) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.unavailable = unavailable;
        this.block = block;
        if (block != null) {
            this.blockFloor = block.getBlockFloor();
            this.blockCode = block.getBlockCode();
        }
    }


    @JsonIgnore
    @Transient
    private boolean isNew = true;

    @PostLoad
    @PostPersist
    void markAsNotNew() {
        this.isNew = false;
    }

    @Override
    public Integer getId() {
        return roomNumber;
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return isNew;  // JPA uses this instead of the null check
    }
}

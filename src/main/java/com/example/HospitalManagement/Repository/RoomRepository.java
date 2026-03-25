package com.example.HospitalManagement.Repository;


        import com.example.HospitalManagement.Entity.Room;
        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.data.rest.core.annotation.RepositoryRestResource;

        import java.util.List;
        import java.util.Optional;


@RepositoryRestResource(path = "rooms")

public interface RoomRepository extends JpaRepository<Room,Integer> {

        @Override
        List<Room> findAll();

        public Optional<Room> findByRoomNumber(Integer roomNumber);
}
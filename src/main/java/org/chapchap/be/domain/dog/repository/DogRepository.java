package org.chapchap.be.domain.dog.repository;

import org.chapchap.be.domain.dog.entity.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, Long> {
    Optional<Dog> findByIdAndOwnerId(Long id, Long ownerId);
    boolean existsByOwnerIdAndName(Long ownerId, String name);
}

package io.github.sagapoctryone.repository;


import io.github.sagapoctryone.model.AuxiliaryMovement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuxiliaryMovementRepository extends CrudRepository<AuxiliaryMovement, String> {

    AuxiliaryMovement findByChoreographyId(String choreographyId);
}

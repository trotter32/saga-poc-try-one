package io.github.sagapoctryone.repository;


import io.github.sagapoctryone.model.Auxiliary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuxiliaryRepository extends CrudRepository<Auxiliary, String> {

    Auxiliary findByChoreographyId(String choreographyId);
}

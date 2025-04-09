package io.github.sagapoctryone.service;


import io.github.sagapoctryone.model.Auxilary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuxRepository extends CrudRepository<Auxilary, String> {
}

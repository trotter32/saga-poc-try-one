package io.github.sagapoctryone.repository;


import io.github.sagapoctryone.model.RepoCallArg;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepoCallArgRepository extends CrudRepository<RepoCallArg, String> {

    List<RepoCallArg> findByAuxiliaryIdOrderByIdAsc(String auxiliaryId);
}

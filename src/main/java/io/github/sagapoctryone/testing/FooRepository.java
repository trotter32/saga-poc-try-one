package io.github.sagapoctryone.testing;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FooRepository extends CrudRepository<Foo, String> {

}

package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.User;
import java.util.List;


public interface UserRepository extends CrudRepository<User,Long>{
    public boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.credentials c WHERE c.role = :role")
    List<User> findUsersByCredentialRole(String role);
}

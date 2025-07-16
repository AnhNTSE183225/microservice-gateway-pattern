package com.mss301.msaccount_se183225.authentication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// CHANGE: name, entity, and ID type
public interface UserRepository extends JpaRepository<User, Integer> {

    // CHANGE: query, parameter
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findAuthentication(String username);

}

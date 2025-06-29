package com.Hannigrumis.api.user;

import org.springframework.stereotype.Repository;

import com.Hannigrumis.api.DTO.UserDTO;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    Optional<User> findById(Long id);

    @Query("SELECT new com.Hannigrumis.api.DTO.UserDTO(u.id, u.name, u.email, u.verified) FROM User u")
    List<UserDTO> getAllUsers();
}

package com.example.market.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.market.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{
	Optional<User> findByEmail(String email);
}

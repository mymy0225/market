package com.example.market.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.market.entities.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long>{

	List<Item> findByUser_id(long id);
	Optional<Item> findById(long id);
	List<Item> findByUser_idNotOrderByCreatedatDesc(long id);
	List<Item> findAllByOrderByCreatedatDesc();
}

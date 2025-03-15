package com.spring.ekart.repository;

import java.util.LinkedHashMap;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.ekart.dto.Item;

public interface ItemRepository extends JpaRepository<Item, Integer>{

	LinkedHashMap<String, Item> findByNameLike(String name);

}

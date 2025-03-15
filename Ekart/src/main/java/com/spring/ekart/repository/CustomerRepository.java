package com.spring.ekart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.ekart.dto.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{

	boolean existsByEmail(String email);

	boolean existsByMobile(long mobile);

	Customer findByEmail(String email);

}

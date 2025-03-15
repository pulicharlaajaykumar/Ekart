package com.spring.ekart.repository;

import com.spring.ekart.dto.Customer;
import com.spring.ekart.dto.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<Order, Integer> {

	List<Order> findByCustomer(Customer customer);
}

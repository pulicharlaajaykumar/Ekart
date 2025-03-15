package com.spring.ekart.repository;

import com.spring.ekart.dto.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {
	boolean existsByEmail(String email);

	boolean existsByMobile(long mobile);

	Vendor findByEmail(String email);

}
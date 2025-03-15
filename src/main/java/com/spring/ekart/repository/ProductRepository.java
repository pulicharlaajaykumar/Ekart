package com.spring.ekart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.ekart.dto.Product;
import com.spring.ekart.dto.Vendor;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findByVendor(Vendor vendor);

	List<Product> findByApprovedTrue();

	List<Product> findByNameLikeAndApprovedTrue(String toSearch);

	List<Product> findByDescriptionLikeAndApprovedTrue(String toSearch);

	List<Product> findByCategoryLikeAndApprovedTrue(String toSearch);
}

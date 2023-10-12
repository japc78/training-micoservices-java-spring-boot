package com.paymentchain.customer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.paymentchain.customer.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	// @Query("SELECT c FROM Customer c WHERE c.code = ?1")
	public Optional<Customer> findByCode(String code);


	// @Query("SELECT c FROM Customer c WHERE c.iban = ?1")
	public Optional<Customer>  findByIban(String iban);
}

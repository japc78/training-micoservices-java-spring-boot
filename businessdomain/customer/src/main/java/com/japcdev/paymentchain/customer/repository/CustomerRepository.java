package com.japcdev.paymentchain.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.japcdev.paymentchain.customer.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}

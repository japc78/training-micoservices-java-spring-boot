package com.paymentchain.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.repository.CustomerRepository;

@RestController
@RequestMapping("/customer")
public class CustomerRestController {

	@Autowired
	CustomerRepository customerRepository;

	@GetMapping()
	public ResponseEntity<?> findAll() {
		try {
			return new ResponseEntity<>(customerRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> find(@PathVariable Long id) {
		try {
			return new ResponseEntity<>(customerRepository.findById(id).orElse(null), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping()
	public ResponseEntity<?> create(@RequestBody Customer customer) {
		try {
			customerRepository.save(customer);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping()
	public ResponseEntity<?> update(@RequestBody Customer customer) {
		try {
			if (!customerRepository.findById(customer.getId()).isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(customerRepository.save(customer), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		try {
			if (!customerRepository.findById(id).isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			customerRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

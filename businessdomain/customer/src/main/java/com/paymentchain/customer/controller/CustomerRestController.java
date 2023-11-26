package com.paymentchain.customer.controller;

import java.net.UnknownHostException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.paymentchain.customer.business.transactions.BusinessTransactions;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.exception.BussinesException;
import com.paymentchain.customer.repository.CustomerRepository;

@RestController
@RequestMapping("/customer")
public class CustomerRestController {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BusinessTransactions businessTransactions;

	@Value("${user.role}")
	private String role;

	@GetMapping()
	public ResponseEntity<?> findAll() {
		try {
			if (customerRepository.findAll().isEmpty() || !(customerRepository.findAll().size() > 0))  {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(customerRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> find(@PathVariable Long id) {
		try {
			// Optional<Customer> customer = customerRepository.findById(id);

			// if (!customer.isPresent()) {
			// 	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			// }

			// return new ResponseEntity<>(customer, HttpStatus.OK);

			return customerRepository.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());

		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/hello")
	public ResponseEntity<String> sayHello(){
		return new ResponseEntity<String>("Hello your role is: " + role, HttpStatus.NOT_FOUND);
	}

	@PostMapping()
	public ResponseEntity<?> create(@RequestBody Customer customer) throws BussinesException, UnknownHostException {
			businessTransactions.save(customer);
			return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping()
	public ResponseEntity<?> update(@RequestBody Customer customer) {
		try {
			if (!customerRepository.findById(customer.getId()).isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			customerRepository.save(customer);
			return new ResponseEntity<>(HttpStatus.OK);
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
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/full")
	public ResponseEntity<?> getByCode(@RequestParam String code) throws BussinesException, UnknownHostException {
			return new ResponseEntity<>(businessTransactions.getByCode(code), HttpStatus.OK);
	}
}

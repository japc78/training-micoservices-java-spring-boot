package com.paymentchain.transaction.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.paymentchain.transaction.entities.Transaction;
import com.paymentchain.transaction.repository.TransactionRepository;

@RestController
@RequestMapping("/transaction")
public class TransactionRestController {

	@Autowired
	TransactionRepository transactionRepository;

	@GetMapping()
	public ResponseEntity<?> findAll() {
		try {
			return new ResponseEntity<>(transactionRepository.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> find(@PathVariable Long id) {
		try {
			Optional<Transaction> transaction = transactionRepository.findById(id);

			if (!transaction.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(transaction, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@GetMapping("/customer/transactions")
	public ResponseEntity<?> find(@RequestParam String accountIban) {
		try {
			Optional<List<Transaction>> listTransaction = transactionRepository.findByAccountIban(accountIban);

			if (listTransaction.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(listTransaction, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping()
	public ResponseEntity<?> create(@RequestBody Transaction transaction) {
		try {
			transactionRepository.save(transaction);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping()
	public ResponseEntity<?> update(@RequestBody Transaction transaction) {
		try {
			if (transactionRepository.findById(transaction.getId()).isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			transactionRepository.save(transaction);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		try {
			if (transactionRepository.findById(id).isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			transactionRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

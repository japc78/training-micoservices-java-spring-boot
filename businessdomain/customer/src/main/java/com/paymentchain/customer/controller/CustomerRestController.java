package com.paymentchain.customer.controller;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.repository.CustomerRepository;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@RestController
@RequestMapping("/customer")
public class CustomerRestController {

	@Autowired
	CustomerRepository customerRepository;

	private final WebClient.Builder webClientBuilder;

	/**
	 * @param webClientBuilder
	 */
	public CustomerRestController(WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}

	//webClient requires HttpClient library to work propertly
	HttpClient httpClient = HttpClient.create()
		//Connection Timeout: is a period within which a connection between a client and a server must be established
		.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
		.option(ChannelOption.SO_KEEPALIVE, true)
		.option(EpollChannelOption.TCP_KEEPIDLE, 300)
		.option(EpollChannelOption.TCP_KEEPINTVL, 60)
		//Response Timeout: The maximun time we wait to receive a response after sending a request
		.responseTimeout(Duration.ofSeconds(1))
		// Read and Write Timeout: A read timeout occurs when no data was read within a certain
		//period of time, while the write timeout when a write operation cannot finish at a specific time
		.doOnConnected(connection -> {
				connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
				connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
		});

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
			Optional<Customer> customer = customerRepository.findById(id);

			if (!customer.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(customer, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping()
	public ResponseEntity<?> create(@RequestBody Customer customer) {
		try {
			//A cada producto le asignamos el customer.
			customer.getProducts().forEach(product -> product.setCustomer(customer));

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
	public ResponseEntity<?> getByCode(@RequestParam String code) {
		try {
			Optional<Customer> customer = customerRepository.findByCode(code);

			if (!customer.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			List<CustomerProduct> products = customer.get().getProducts();

			products.forEach(product -> {
				String productName = getProductName(product.getProductId());
				product.setProductName(productName);
			});

			return new ResponseEntity<>(customer, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	private String getProductName(Long id) {
		WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
			.baseUrl("http://localhost:8083/product")
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.defaultUriVariables(Collections.singletonMap("url", "http://localhost:8083/product"))
			.build();

		JsonNode block = webClient.method(HttpMethod.GET).uri("/" + id)
			.retrieve().bodyToMono(JsonNode.class).block();

		String name = block.get("name").asText();
		return name;
	}
}

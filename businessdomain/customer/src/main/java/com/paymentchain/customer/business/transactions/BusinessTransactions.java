package com.paymentchain.customer.business.transactions;

import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.exception.BussinesException;
import com.paymentchain.customer.repository.CustomerRepository;
import com.paymentchain.customer.util.ErrorCode;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Service
public class BusinessTransactions {

	@Autowired
	CustomerRepository customerRepository;

	private final WebClient.Builder webClientBuilder;

	/**
	 * @param webClientBuilder
	 */
	public BusinessTransactions(WebClient.Builder webClientBuilder) {
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


	public Customer getByCode(String code) throws BussinesException, UnknownHostException {

		Optional<Customer> optionalCustomer = customerRepository.findByCode(code);

		if (!optionalCustomer.isPresent()) {
			String message = String.format("Customer with code %s not found", code);
			throw new BussinesException(ErrorCode.CUSTOMER_NOT_FOUND.getCode(), message, HttpStatus.NOT_FOUND);
		}

		Customer customer = getCustomerFullProducts(optionalCustomer.get());

		// find all transactions that belong this account number.
		List<?> transactions = getTransactions(customer.getIban());
		customer.setTransactions(transactions);

		return customer;
	}


	public void save(Customer customer) throws UnknownHostException, BussinesException {
		customerRepository.save(getCustomerFullProducts(customer));
	}


	private Customer getCustomerFullProducts(Customer customer) throws BussinesException, UnknownHostException {
		Iterator<CustomerProduct> ip = customer.getProducts().iterator();

		while (ip.hasNext()) {
			CustomerProduct customerProduct = ip.next();

			String productName = getProductName(customerProduct.getProductId());

			if (productName.isEmpty()) {
				String message = String.format("Product with code %s not found", customerProduct.getId());
				throw new BussinesException(ErrorCode.PRODUCT_NOT_FOUND.getCode(), message, HttpStatus.NOT_FOUND);
			}

			customerProduct.setCustomer(customer);
			customerProduct.setProductName(productName);
		}

		return customer;
	}


	private String getProductName(Long id) throws UnknownHostException {
		String name = null;
		try {
			WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
				.baseUrl("http://businessdomain-product/product")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", "http://businessdomain-product/product"))
				.build();

			JsonNode block = webClient.method(HttpMethod.GET).uri("/" + id)
				.retrieve().bodyToMono(JsonNode.class).block();

			name = block.get("name").asText();

		} catch (WebClientResponseException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				name = "";
			}
		} catch (Exception e ) {
			throw new UnknownHostException(e.getMessage());
		}
		return name;
	}


	private List<?> getTransactions(String accountIban) throws UnknownHostException {
		List<?> transactions = null;

		try {
			WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
				.baseUrl("http://businessdomain-transactions/transaction")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();

				 transactions = webClient.method(HttpMethod.GET).uri(uriBuilder -> uriBuilder
				.path("/customer/transactions")
				.queryParam("accountIban", accountIban)
				.build())
				.retrieve().bodyToFlux(Object.class).collectList().block();

		} catch (Exception e) {
			throw new UnknownHostException(e.getMessage());
		}
		return transactions;
	}
}

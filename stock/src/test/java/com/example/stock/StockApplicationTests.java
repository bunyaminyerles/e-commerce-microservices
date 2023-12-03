package com.example.stock;

import com.example.stock.domain.model.StockDto;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = TestConfig.class)
class StockApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldReturnAStockWhenDataIsSaved() {
		ResponseEntity<String> response = restTemplate
				.getForEntity("/stock/100", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$.id");
		assertThat(id).isEqualTo(100);

		Number productId = documentContext.read("$.productId");
		assertThat(productId).isEqualTo(100);

		Number stock = documentContext.read("$.stock");
		assertThat(stock).isEqualTo(50);
	}

	@Test
	void shouldNotReturnAStockWithAnUnknownId() {
		ResponseEntity<String> response = restTemplate
				.getForEntity("/stock/4", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@Test
	@DirtiesContext
	void shouldCreateANewStock() {
		StockDto newStock = StockDto.builder()
				.id(null)
				.productId(104L)
				.stock(10L)
				.build();
		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity("/stock", newStock, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI locationOfNewStock = createResponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate
				.getForEntity(locationOfNewStock, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		Number productId = documentContext.read("$.productId");
		Number stock = documentContext.read("$.stock");

		assertThat(id).isNotNull();
		assertThat(productId.longValue()).isEqualTo((Long)newStock.getProductId());
		assertThat(stock.longValue()).isEqualTo((Long)newStock.getStock());
	}

	@Test
	@DirtiesContext
	void shouldUpdateAnExistingStock() {
		StockDto stockUpdate = StockDto.builder()
				.stock(20L)
				.build();
		HttpEntity<StockDto> request = new HttpEntity<>(stockUpdate);
		ResponseEntity<Void> response = restTemplate
				.exchange("/stock/100", HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate
				.getForEntity("/stock/100", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		Number stock = documentContext.read("$.stock");

		assertThat(id).isEqualTo(100);
		assertThat(stock.longValue()).isEqualTo(stockUpdate.getStock());
	}

	@Test
	@DirtiesContext
	void shouldDeleteAnExistingStock() {
		ResponseEntity<Void> response = restTemplate
				.exchange("/stock/100", HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// Add the following code:
		ResponseEntity<String> getResponse = restTemplate
				.getForEntity("/stock/100", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotDeleteAStockThatDoesNotExist() {
		ResponseEntity<Void> deleteResponse = restTemplate
				.exchange("/stock/99999", HttpMethod.DELETE, null, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}


}

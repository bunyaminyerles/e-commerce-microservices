package com.example.product;

import com.example.product.domain.model.ProductDto;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
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
class ProductApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldReturnAProductWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/product/100", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(100);

        String name = documentContext.read("$.name");
        assertThat(name).isEqualTo("Chips");

        String description = documentContext.read("$.description");
        assertThat(description).isEqualTo("Potato chips");

        Number price = documentContext.read("$.price");
        assertThat(price).isEqualTo(1000.0);

        String category = documentContext.read("$.category");
        assertThat(category).isEqualTo("Snack");
    }

    @Test
    void shouldNotReturnAProductWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/product/4", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    @DirtiesContext
    void shouldCreateANewProduct() {
        ProductDto newProduct = ProductDto.builder()
                .id(null)
                .name("Fanta")
                .description("Soft drink")
                .price(4000.00)
                .category("Beverage")
                .build();
        ResponseEntity<Void> createResponse = restTemplate
                .postForEntity("/product", newProduct, Void.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewProduct = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity(locationOfNewProduct, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String name = documentContext.read("$.name");
        String description = documentContext.read("$.description");
        Number price = documentContext.read("$.price");
        String category = documentContext.read("$.category");

        System.out.println("id = " + id);

        assertThat(id).isNotNull();
        assertThat(name).isEqualTo(newProduct.getName());
        assertThat(description).isEqualTo(newProduct.getDescription());
        assertThat(price).isEqualTo(newProduct.getPrice());
        assertThat(category).isEqualTo(newProduct.getCategory());
    }

    @Test
    void shouldReturnAllProductsWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/product/all", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int productCount = documentContext.read("$.length()");
        assertThat(productCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(100, 101, 102);

        JSONArray amounts = documentContext.read("$..name");
        assertThat(amounts).containsExactlyInAnyOrder("Chips", "Chocolate", "Coca Cola");

        JSONArray descriptions = documentContext.read("$..description");
        assertThat(descriptions).containsExactlyInAnyOrder("Potato chips", "Chocolate bar", "Soft drink");

        JSONArray prices = documentContext.read("$..price");
        assertThat(prices).containsExactlyInAnyOrder(1000.0, 2000.0, 3000.0);

        JSONArray categories = documentContext.read("$..category");
        assertThat(categories).containsExactlyInAnyOrder("Snack", "Snack", "Beverage");
    }

    @Test
    void shouldReturnAllProductsByCategory() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/product/all?category=Snack", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int productCount = documentContext.read("$.length()");
        assertThat(productCount).isEqualTo(2);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(100, 101);

        JSONArray amounts = documentContext.read("$..name");
        assertThat(amounts).containsExactlyInAnyOrder("Chips", "Chocolate");

        JSONArray descriptions = documentContext.read("$..description");
        assertThat(descriptions).containsExactlyInAnyOrder("Potato chips", "Chocolate bar");

        JSONArray prices = documentContext.read("$..price");
        assertThat(prices).containsExactlyInAnyOrder(1000.0, 2000.0);

        JSONArray categories = documentContext.read("$..category");
        assertThat(categories).containsExactlyInAnyOrder("Snack", "Snack");
    }

    @Test
    void shouldReturnAPageOfProducts() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/product/all?page=0&size=1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnASortedPageOfProducts() {
        ResponseEntity<String> response = restTemplate
                .getForEntity("/product/all?page=0&size=1&sort=price,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray read = documentContext.read("$[*]");
        assertThat(read.size()).isEqualTo(1);

        double amount = documentContext.read("$[0].price");
        assertThat(amount).isEqualTo(3000.0);
    }


    @Test
    @DirtiesContext
    void shouldUpdateAnExistingProduct() {
        ProductDto productUpdate = ProductDto.builder()
                .name("Lays")
                .description("Potato chips")
                .price(1500.0)
                .category("Snack")
                .build();
        HttpEntity<ProductDto> request = new HttpEntity<>(productUpdate);
        ResponseEntity<Void> response = restTemplate
                .exchange("/product/100", HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/product/100", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String name = documentContext.read("$.name");
        String description = documentContext.read("$.description");
        Double price = documentContext.read("$.price");
        String category = documentContext.read("$.category");

        assertThat(id).isEqualTo(100);
        assertThat(name).isEqualTo(productUpdate.getName());
        assertThat(description).isEqualTo(productUpdate.getDescription());
        assertThat(price).isEqualTo(productUpdate.getPrice());
        assertThat(category).isEqualTo(productUpdate.getCategory());
    }

    @Test
    @DirtiesContext
    void shouldDeleteAnExistingProduct() {
        ResponseEntity<Void> response = restTemplate
                .exchange("/product/100", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Add the following code:
        ResponseEntity<String> getResponse = restTemplate
                .getForEntity("/product/100", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteAProductThatDoesNotExist() {
        ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/product/99999", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}

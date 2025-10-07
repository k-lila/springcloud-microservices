package backend.ConfigServer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigServerTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void isConfigForClientServiceAvailable() {
		ResponseEntity<Map> response = restTemplate.getForEntity(
			"http://localhost:" + port + "/client-service/default",
			Map.class
		);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		Integer port = JsonPath.read(
			response.getBody(),
			"$.propertySources[0].source['server.port']"
		);
		assertEquals(8081, port);
	}

	@Test
	void isConfigForProductServiceIsAvailable() {
		ResponseEntity<Map> response = restTemplate.getForEntity(
			"http://localhost:" + port + "/product-service/default",
			Map.class
		);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		Integer port = JsonPath.read(
			response.getBody(),
			"$.propertySources[0].source['server.port']"
		);
		assertEquals(8082, port);
	}

	@Test
	void isConfigForStockServiceIsAvailable() {
		ResponseEntity<Map> response = restTemplate.getForEntity(
			"http://localhost:" + port + "/stock-service/default",
			Map.class
		);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		Integer port = JsonPath.read(
			response.getBody(),
			"$.propertySources[0].source['server.port']"
		);
		assertEquals(8083, port);
	}

	@Test
	void isConfigForSalesServiceIsAvailable() {
		ResponseEntity<Map> response = restTemplate.getForEntity(
			"http://localhost:" + port + "/sale-service/default",
			Map.class
		);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		Integer port = JsonPath.read(
			response.getBody(),
			"$.propertySources[0].source['server.port']"
		);
		assertEquals(8084, port);
	}

}

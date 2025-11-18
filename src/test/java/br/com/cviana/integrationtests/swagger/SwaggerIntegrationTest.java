package br.com.cviana.integrationtests.swagger;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.cviana.config.TestConfigs;
import br.com.cviana.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.RestAssured;

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
class SwaggerIntegrationTest extends AbstractIntegrationTest {

	@Test
	void shouldDisplaySwaggerUiPage() {
		var content = RestAssured
			.given()
				.basePath("/swagger-ui/index.html")
				.port(TestConfigs.SERVER_PORT)
			.when()
				.get()
			.then()
				.statusCode(200)
				.extract().body().asString();
		Assert.assertTrue(content.contains("Swagger UI"));
	}

}

package br.com.cviana.integrationtests.controllers;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cviana.config.TestConfigs;
import br.com.cviana.integrationtests.dto.PersonDTO;
import br.com.cviana.integrationtests.dto.security.AccountCredentialsDTO;
import br.com.cviana.integrationtests.dto.security.TokenDTO;
import br.com.cviana.integrationtests.testcontainers.AbstractIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerCorsTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static PersonDTO person;
    private static AccountCredentialsDTO creds;
    private static TokenDTO token;
    private final String dataType = MediaType.APPLICATION_JSON_VALUE;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        if(person == null) {
            person = new PersonDTO();
            mockPerson();
        }

        specification = new RequestSpecBuilder()
            .setBasePath("/api/person/v1")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .setContentType(dataType)
            .setAccept(dataType)
            .build();

        creds = new AccountCredentialsDTO("leandro", "admin123");
        
        token = RestAssured
			.given(specification)
                .body(creds)
                .basePath("/auth")
			.when()
				.post("/signin")
			.then()
				.statusCode(200)
				.extract().body().as(TokenDTO.class);
    }

    private void mockPerson() {
        person.setFirstName("Leonardo");
        person.setLastName("Da Vinci");
        person.setAddress("Vinci Street, 1453 - Italy");
        person.setGender("Male");
        person.setEnabled(true);
    }

    @Test
    @Order(1)
    void testCreate() throws JsonMappingException, JsonProcessingException {
        var content = RestAssured
			.given(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_VALID)
                .header("Authorization", "Bearer "+token.getAccessToken())
                .body(person)
			.when()
				.post()
			.then()
				.statusCode(200)
				.extract().body().asString();
        
        PersonDTO response = objectMapper.readValue(content, PersonDTO.class);
        person = response;
        Assert.assertTrue(response.getId() > 0);
        Assert.assertEquals("Leonardo", response.getFirstName());
        Assert.assertEquals("Da Vinci", response.getLastName());
        Assert.assertEquals("Vinci Street, 1453 - Italy", response.getAddress());
        Assert.assertEquals("Male", response.getGender());
        Assert.assertTrue(response.isEnabled());
    }

    @Test
    @Order(2)
    void testCreateWithInvalidOrigin() throws JsonMappingException, JsonProcessingException {
        var content = RestAssured
			.given(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_INVALID)
                .header("Authorization", "Bearer "+token.getAccessToken())
                .body(person)
			.when()
				.post()
			.then()
				.statusCode(403)
				.extract().body().asString();
        
        Assert.assertEquals("Invalid CORS request", content);
    }

    @Test
    @Order(3)
    void testFindById() throws JsonMappingException, JsonProcessingException {
        PersonDTO response;

        if(person.getId()==null) {
            var contentCreateEntity = RestAssured
                .given(specification)
                    .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_VALID)
                .header("Authorization", "Bearer "+token.getAccessToken())
                    .body(person)
                .when()
                    .post()
                .then()
                    .statusCode(200)
                    .extract().body().asString();
            response = objectMapper.readValue(contentCreateEntity, PersonDTO.class);
            person = response;
        }

        var content = RestAssured
            .given(specification)
                .header("Authorization", "Bearer "+token.getAccessToken())
                .pathParam("id", person.getId())
            .when()
                .get("/{id}")
            .then()
                .statusCode(200)
                .extract().body().asString();
        
        response = objectMapper.readValue(content, PersonDTO.class);
        
        Assert.assertTrue(response.getId() > 0);
        Assert.assertEquals("Leonardo", response.getFirstName());
        Assert.assertEquals("Da Vinci", response.getLastName());
        Assert.assertEquals("Vinci Street, 1453 - Italy", response.getAddress());
        Assert.assertEquals("Male", response.getGender());
        Assert.assertTrue(response.isEnabled());
    }

    @Test
    @Order(4)
    void testFindByIdWithInvalidOrigin() throws JsonMappingException, JsonProcessingException {
        var content = RestAssured
            .given(specification)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_INVALID)
                .header("Authorization", "Bearer "+token.getAccessToken())
                .pathParam("id", 1)
            .when()
                .get("/{id}")
            .then()
                .statusCode(403)
                .extract().body().asString();
        
        Assert.assertEquals("Invalid CORS request", content);
    }

}

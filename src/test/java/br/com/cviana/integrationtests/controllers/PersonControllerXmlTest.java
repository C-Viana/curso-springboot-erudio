package br.com.cviana.integrationtests.controllers;

import java.util.List;

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
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.cviana.config.TestConfigs;
import br.com.cviana.integrationtests.dto.PersonDTO;
import br.com.cviana.integrationtests.dto.security.AccountCredentialsDTO;
import br.com.cviana.integrationtests.dto.security.TokenDTO;
import br.com.cviana.integrationtests.dto.wrapper.PersonWrapperXml;
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
public class PersonControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper xmlMapper;
    private static PersonDTO person;
    private static AccountCredentialsDTO creds;
    private static TokenDTO token;
    private final String dataType = MediaType.APPLICATION_XML_VALUE;
    private final String firstName = "Ruan";
    private final String lastName = "Teles Corrêa";
    private final String address = "Rua Tertulino Ares Moraes, 366 - Rondonópolis/MT";
    private final String gender = "Male";

    @BeforeEach
    void setUp() throws JsonMappingException, JsonProcessingException {
        xmlMapper = new XmlMapper();
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        if(person == null) {
            person = new PersonDTO();
            mockPerson();
        }

        if(specification == null) {
            specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_VALID)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .setContentType(dataType)
                .setAccept(dataType)
                .build();
        }

        creds = new AccountCredentialsDTO("leandro", "admin123");
        token = RestAssured
			.given(specification)
                .basePath("/auth")
                .body(creds)
			.when()
				.post("/signin")
			.then()
				.statusCode(200)
				.extract().body().as(TokenDTO.class);
    }

    private void mockPerson() {
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setAddress(address);
        person.setGender(gender);
        person.setEnabled(true);
    }

    @Test
    @Order(1)
    void testCreate() throws JsonMappingException, JsonProcessingException {
        var content = RestAssured
			.given(specification)
                .header("Authorization", "Bearer "+token.getAccessToken())
                .body(person)
			.when()
				.post()
			.then()
				.statusCode(200)
				.extract().body().asString();
        
        PersonDTO response = xmlMapper.readValue(content, PersonDTO.class);
        person = response;
        Assert.assertTrue(response.getId() > 0);
        Assert.assertEquals(firstName, response.getFirstName());
        Assert.assertEquals(lastName, response.getLastName());
        Assert.assertEquals(address, response.getAddress());
        Assert.assertEquals(gender, response.getGender());
        Assert.assertTrue(response.isEnabled());
    }

    @Test
    @Order(2)
    void testUpdateDisablePerson() throws JsonMappingException, JsonProcessingException {
        var content = RestAssured
            .given(specification)
                .header("Authorization", "Bearer "+token.getAccessToken())
                .pathParam("id", person.getId())
            .when()
                .patch("/{id}")
            .then()
                .statusCode(200)
                .extract().body().asString();
        
        PersonDTO response = xmlMapper.readValue(content, PersonDTO.class);
        
        Assert.assertTrue(response.getId() > 0);
        Assert.assertFalse(response.isEnabled());
    }

    @Test
    @Order(3)
    void testFindById() throws JsonMappingException, JsonProcessingException {
        var content = RestAssured
            .given(specification)
                .header("Authorization", "Bearer "+token.getAccessToken())
                .pathParam("id", person.getId())
            .when()
                .get("/{id}")
            .then()
                .statusCode(200)
                .extract().body().asString();
        
        PersonDTO response = xmlMapper.readValue(content, PersonDTO.class);
        
        Assert.assertTrue(response.getId() > 0);
        Assert.assertFalse(response.isEnabled());
    }

    @Test
    @Order(4)
    void findAllTest() throws JsonMappingException, JsonProcessingException {
        var content = RestAssured
            .given(specification)
                .header("Authorization", "Bearer "+token.getAccessToken())
                .queryParams("page", 3, "size", 12, "direction", "asc")
            .when()
                .get()
            .then()
                .statusCode(200)
                .extract().body().asString();
        
        PersonWrapperXml wrapper = xmlMapper.readValue(content, PersonWrapperXml.class);
        List<PersonDTO> people = wrapper.getContent();

        Assert.assertTrue(people.size() > 1);

        for (int i = 0; i < 3; i++) {
            Assert.assertNotNull(people.get(i).getFirstName());
        }
    }

    @Test
    @Order(5)
    void findByNameTest() throws JsonMappingException, JsonProcessingException {
        String searchNameCriteria = "wil";
        var content = RestAssured
            .given(specification)
                .header("Authorization", "Bearer "+token.getAccessToken())
                .pathParam("name", searchNameCriteria)
                .queryParams("page", 1, "size", 4, "direction", "asc")
            .when()
                .get("/findByName/{name}")
            .then()
                .statusCode(200)
                .extract().body().asString();
        
        PersonWrapperXml wrapper = xmlMapper.readValue(content, PersonWrapperXml.class);
        List<PersonDTO> people = wrapper.getContent();

        Assert.assertTrue(people.size() > 1);

        for (int i = 0; i < 3; i++) {
            String firstName = people.get(i).getFirstName().toLowerCase();
            String lastName = people.get(i).getLastName().toLowerCase();

            Assert.assertTrue(firstName.contains(searchNameCriteria) || lastName.contains(searchNameCriteria));
        }
    }

}

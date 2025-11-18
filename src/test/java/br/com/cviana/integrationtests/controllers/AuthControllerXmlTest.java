package br.com.cviana.integrationtests.controllers;

import static org.junit.Assert.*;

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

import br.com.cviana.config.TestConfigs;
import br.com.cviana.integrationtests.dto.security.AccountCredentialsDTO;
import br.com.cviana.integrationtests.dto.security.TokenDTO;
import br.com.cviana.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

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
public class AuthControllerXmlTest extends AbstractIntegrationTest {
    private static RequestSpecification specification;
    private static XmlMapper objectMapper;
    private static AccountCredentialsDTO creds;
    private static TokenDTO token;
    
    @BeforeEach
    void setUp() {
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        creds = new AccountCredentialsDTO();
        setRequestSpecification();
    }

    private void setRequestSpecification() {
        specification = new RequestSpecBuilder()
            .setContentType(MediaType.APPLICATION_XML_VALUE)
            .setAccept(MediaType.APPLICATION_XML_VALUE)
            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_VALID)
            .setBasePath("/auth")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    }

    @Test
    @Order(1)
    void signin_success() throws JsonMappingException, JsonProcessingException {
        creds = new AccountCredentialsDTO("leandro", "admin123");
        
        token = RestAssured
			.given(specification)
                .body(creds)
			.when()
				.post("/signin")
			.then()
				.statusCode(200)
				.extract().body().as(TokenDTO.class);
        
        assertEquals(creds.getUsername(), token.getUsername());
        assertEquals(true, token.getAutenthicated());
        assertNotNull(token.getCreated());
        assertNotNull(token.getExpiration());
        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());
    }

    @Test
    @Order(2)
    void refresh_success() {
        creds = new AccountCredentialsDTO("leandro", "admin123");

        token = RestAssured
			.given(specification)
                .body(creds)
			.when()
				.post("/signin")
			.then()
				.statusCode(200)
				.extract().body().as(TokenDTO.class);
        
        var token2 = RestAssured
			.given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("username", creds.getUsername())
                .header("Authorization", "Bearer "+token.getRefreshToken())
			.when()
				.put("/refresh/{username}")
			.then()
				.statusCode(200)
				.extract().body().as(TokenDTO.class);
        
        assertEquals(creds.getUsername(), token2.getUsername());
        assertEquals(true, token2.getAutenthicated());
        assertNotNull(token2.getCreated());
        assertNotNull(token2.getExpiration());
        assertNotNull(token2.getAccessToken());
        assertNotNull(token2.getRefreshToken());
    }
    
}
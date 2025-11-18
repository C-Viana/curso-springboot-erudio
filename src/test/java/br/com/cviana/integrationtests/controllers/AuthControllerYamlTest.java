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
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import br.com.cviana.config.TestConfigs;
import br.com.cviana.integrationtests.dto.security.AccountCredentialsDTO;
import br.com.cviana.integrationtests.dto.security.TokenDTO;
import br.com.cviana.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.cviana.mapper.YamlMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;


@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerYamlTest extends AbstractIntegrationTest {
    private static RequestSpecification specification;
    private static YAMLMapper yamlMapper;
    private static AccountCredentialsDTO creds;
    private static TokenDTO token;
    
    @BeforeEach
    void setUp() {
        yamlMapper = new YAMLMapper();
        yamlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        creds = new AccountCredentialsDTO();
        setRequestSpecification();
    }

    private void setRequestSpecification() {
        specification = new RequestSpecBuilder()
            .setContentType(MediaType.APPLICATION_YAML_VALUE)
            .setAccept(MediaType.APPLICATION_YAML_VALUE)
            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_VALID)
            .setBasePath("/auth")
            .setPort(TestConfigs.SERVER_PORT)
            .setConfig(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
            .build();
    }

    @Test
    @Order(1)
    void signin_success() throws JsonMappingException, JsonProcessingException {
        creds = new AccountCredentialsDTO("leandro", "admin123");
        
        var response = RestAssured
			.given(specification)
                .body(YamlMapper.toStringYaml(creds))
			.when()
				.post("/signin")
			.then()
                .log().all()
                .contentType(MediaType.APPLICATION_YAML_VALUE)
				.statusCode(200)
				.extract().body().asString();
        
        token =  yamlMapper.readValue(response, TokenDTO.class);

        assertEquals(creds.getUsername(), token.getUsername());
        assertEquals(true, token.getAutenthicated());
        assertNotNull(token.getCreated());
        assertNotNull(token.getExpiration());
        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());
    }

    @Test
    @Order(2)
    void refresh_success() throws JsonMappingException, JsonProcessingException {
        creds = new AccountCredentialsDTO("leandro", "admin123");

        var response = RestAssured
			.given(specification)
                .body(YamlMapper.toStringYaml(creds))
			.when()
				.post("/signin")
			.then()
                .log().all()
                .contentType(MediaType.APPLICATION_YAML_VALUE)
				.statusCode(200)
				.extract().body().asString();
        
        var token =  yamlMapper.readValue(response, TokenDTO.class);

        response = RestAssured
			.given(specification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("username", creds.getUsername())
                .header("Authorization", "Bearer "+token.getRefreshToken())
			.when()
				.put("/refresh/{username}")
			.then()
                .contentType(MediaType.APPLICATION_YAML_VALUE)
				.statusCode(200)
				.extract().body().asString();
        
        var tokenRefreshed =  yamlMapper.readValue(response, TokenDTO.class);
        
        assertEquals(creds.getUsername(), tokenRefreshed.getUsername());
        assertEquals(true, tokenRefreshed.getAutenthicated());
        assertNotNull(tokenRefreshed.getCreated());
        assertNotNull(tokenRefreshed.getExpiration());
        assertNotNull(tokenRefreshed.getAccessToken());
        assertNotNull(tokenRefreshed.getRefreshToken());
    }
    
}
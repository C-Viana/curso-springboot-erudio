package br.com.cviana.integrationtests.controllers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

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
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import br.com.cviana.config.TestConfigs;
import br.com.cviana.integrationtests.dto.BookDTO;
import br.com.cviana.integrationtests.dto.security.AccountCredentialsDTO;
import br.com.cviana.integrationtests.dto.security.TokenDTO;
import br.com.cviana.integrationtests.dto.wrapper.BookWrapperXml;
import br.com.cviana.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.cviana.mapper.YamlMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YAMLMapper yamlMapper;
    private static BookDTO book;
    private static AccountCredentialsDTO creds;
    private static TokenDTO token;
    private final String dataType = MediaType.APPLICATION_YAML_VALUE;
    private final String author = "Heinrich Sommer";
    private final String title = "Ein Vollmond zu Sehen";
    private final Date date = new Date();
    private final double price = 99.9D;

    @BeforeEach
    void setUp() throws JsonMappingException, JsonProcessingException {
        yamlMapper = new YAMLMapper();
        yamlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        if(book == null) {
            book = new BookDTO();
            mockBook();
        }

        if(specification == null) {
            specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_VALID)
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .setContentType(dataType)
                .setAccept(dataType)
                .setConfig(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(dataType, ContentType.TEXT)))
                .build();
        }

        creds = new AccountCredentialsDTO("leandro", "admin123");
        var response = RestAssured
			.given(specification)
                .basePath("/auth")
                .body(YamlMapper.toStringYaml(creds))
			.when()
				.post("/signin")
			.then()
				.statusCode(200)
				.extract().body().asString();
        
        token =  yamlMapper.readValue(response, TokenDTO.class);
    }

    private void mockBook() {
        book.setAuthor(author);
        book.setTitle(title);
        book.setPublishedDate(date);
        book.setPrice(price);
    }

    @Test
    @Order(1)
    void testCreate() throws JsonMappingException, JsonProcessingException {
        var content = RestAssured
			.given(specification)
                .header("Authorization", "Bearer "+token.getAccessToken())
                .body(YamlMapper.toStringYaml(book))
			.when()
				.post()
			.then()
				.statusCode(200)
				.extract().body().asString();
        
        BookDTO response = yamlMapper.readValue(content, BookDTO.class);
        book = response;
        Assert.assertTrue(response.getId() > 0);
        Assert.assertEquals(author, response.getAuthor());
        Assert.assertEquals(title, response.getTitle());
        Instant inst = response.getPublishedDate().toInstant();
        String responseDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format( LocalDateTime.ofInstant(inst, ZoneId.systemDefault()));
        Assert.assertTrue( Pattern.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}", responseDate) );
        Assert.assertEquals(price, response.getPrice(), 0.001);
    }

    @Test
    @Order(3)
    void testFindById() throws JsonMappingException, JsonProcessingException {
        var content = RestAssured
            .given(specification)
            .header("Authorization", "Bearer "+token.getAccessToken())
                .pathParam("id", book.getId())
            .when()
                .get("/{id}")
            .then()
                .statusCode(200)
                .extract().body().asString();
        
        BookDTO response = yamlMapper.readValue(content, BookDTO.class);
        
        Assert.assertTrue(response.getId() > 0);
    }

    @Test
    @Order(4)
    void findAllTest() throws JsonMappingException, JsonProcessingException {
        var content = RestAssured
            .given(specification)
            .header("Authorization", "Bearer "+token.getAccessToken())
                .queryParams("page", 2, "quantity", 5, "sort", "asc")
            .when()
                .get()
            .then()
                .statusCode(200)
                .extract().body().asString();
        
        BookWrapperXml wrapper = yamlMapper.readValue(content, BookWrapperXml.class);
        List<BookDTO> people = wrapper.getContent();

        Assert.assertTrue(people.size() > 1);

        for (int i = 0; i < 3; i++) {
            Assert.assertNotNull(people.get(i).getTitle());
        }
    }

}

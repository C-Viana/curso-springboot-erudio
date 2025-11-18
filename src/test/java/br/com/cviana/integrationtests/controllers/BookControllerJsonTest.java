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
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cviana.config.TestConfigs;
import br.com.cviana.integrationtests.dto.BookDTO;
import br.com.cviana.integrationtests.dto.security.AccountCredentialsDTO;
import br.com.cviana.integrationtests.dto.security.TokenDTO;
import br.com.cviana.integrationtests.dto.wrapper.BookWrapperJson;
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
public class BookControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static BookDTO book;
    private static AccountCredentialsDTO creds;
    private static TokenDTO token;
    private final String dataType = MediaType.APPLICATION_JSON_VALUE;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
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
                .build();
        }

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

    private void mockBook() {
        book.setAuthor("Heinrich Sommer");
        book.setTitle("Ein Vollmond zu Sehen");
        book.setPublishedDate(new Date());
        book.setPrice(99.9D);
    }

    @Test
    @Order(1)
    void testCreate() throws JsonMappingException, JsonProcessingException {
        var content = RestAssured
			.given(specification)
                .header("Authorization", "Bearer "+token.getAccessToken())
                .body(book)
			.when()
				.post()
			.then()
				.statusCode(200)
				.extract().body().asString();
        
        BookDTO response = objectMapper.readValue(content, BookDTO.class);
        book = response;
        Assert.assertTrue(response.getId() > 0);
        Assert.assertEquals("Heinrich Sommer", response.getAuthor());
        Assert.assertEquals("Ein Vollmond zu Sehen", response.getTitle());
        Instant inst = response.getPublishedDate().toInstant();
        String responseDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format( LocalDateTime.ofInstant(inst, ZoneId.systemDefault()));
        Assert.assertTrue( Pattern.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}", responseDate) );
        Assert.assertEquals(99.9D, response.getPrice(), 0.001);
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
        
        BookDTO response = objectMapper.readValue(content, BookDTO.class);
        
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
        
        BookWrapperJson wrapper = objectMapper.readValue(content, BookWrapperJson.class);
        List<BookDTO> people = wrapper.getEmbedded().getBooks();

        Assert.assertTrue(people.size() > 1);

        for (int i = 0; i < 3; i++) {
            Assert.assertNotNull(people.get(i).getTitle());
        }
    }

}

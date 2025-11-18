package br.com.cviana.repository;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import br.com.cviana.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.cviana.models.Person;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    PersonRepository repository;

    private static Person person;

    @BeforeAll
    static void setUp() {
        person = new Person();
    }

    @Test
    @Order(1)
    void testFindByName() {
        Pageable pageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findByName("tzar", pageable).getContent().getFirst();
        Assert.assertNotNull(person);
        Assert.assertNotNull(person.getFirstName());
        Assert.assertNotNull(person.getLastName());
        Assert.assertNotNull(person.getGender());
        Assert.assertNotNull(person.getAddress());
        Assert.assertTrue(person.isEnabled());
    }

    @Test
    @Order(2)
    void testDisablePerson() {
        Long id = person.getId();
        repository.disablePerson(id);
        person = repository.findById(id).get();
        Assert.assertNotNull(person);
        Assert.assertNotNull(person.getFirstName());
        Assert.assertNotNull(person.getLastName());
        Assert.assertNotNull(person.getGender());
        Assert.assertNotNull(person.getAddress());
        Assert.assertFalse(person.isEnabled());
    }
}

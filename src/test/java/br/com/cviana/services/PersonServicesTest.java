package br.com.cviana.services;

import br.com.cviana.data.dto.PersonDTO;
import br.com.cviana.exceptions.RequiredObjectIsNullException;
import br.com.cviana.exceptions.ResourceNotFoundException;
import br.com.cviana.models.Person;
import br.com.cviana.repository.PersonRepository;
import br.com.cviana.unittests.mapper.mocks.MockPerson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServicesTest {

    @InjectMocks
    private PersonService personServices;

    @Mock
    private PersonRepository repository;

    private MockPerson input;

    @SuppressWarnings("null")
    private void validatePersonAttributes(PersonDTO dto) {
        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals("First Name Test"+dto.getId(), dto.getFirstName());
        assertEquals("Last Name Test"+dto.getId(), dto.getLastName());
        assertEquals("Addres Test"+dto.getId(), dto.getAddress());
        String expectedGender = (dto.getId() % 2)==0 ? "Male" : "Female";
        assertEquals(expectedGender, dto.getGender());
        assertTrue(dto.getLinks().toList().size() > 0);

        assertTrue(dto.getLinks().toList().stream().anyMatch(link -> 
            link.getRel().value().equals("self") 
            && link.getHref().endsWith("/api/person/v1/" + dto.getId())
            && link.getTitle().equals("GET")
            )
        );
        assertTrue(dto.getLinks().toList().stream().anyMatch(link -> 
            link.getRel().value().equals("findAll") 
            && link.getHref().endsWith("/api/person/v1?page=1&size=12&direction=asc")
            && link.getTitle().equals("GET")
            )
        );
        assertTrue(dto.getLinks().toList().stream().anyMatch(link -> 
            link.getRel().value().equals("create") 
            && link.getHref().endsWith("/api/person/v1")
            && link.getTitle().equals("POST")
            )
        );
        assertTrue(dto.getLinks().toList().stream().anyMatch(link -> 
            link.getRel().value().equals("update") 
            && link.getHref().endsWith("/api/person/v1")
            && link.getTitle().equals("PUT")
            )
        );
        assertTrue(dto.getLinks().toList().stream().anyMatch(link -> 
            link.getRel().value().equals("delete") 
            && link.getHref().endsWith("/api/person/v1/" + dto.getId())
            && link.getTitle().equals("DELETE")
            )
        );
    }

    @BeforeEach
    void setUp() {
        input = new MockPerson();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Disabled("TEST UNDER MAINTENANCE DUE PAGEABLE IMPLEMENTATION")
    void testFindAll_success() {
        /*List<Person> list = input.mockEntityList();
        when(repository.findAll()).thenReturn(list);

        var people = personServices.findAll();
        assertNotNull(people);
        assertEquals(14, people.size());

        for (int i = 0; i < people.size(); i++) {
            validatePersonAttributes(people.get(i));
        }*/
    }

    @Test
    void testFindById_success() {
        Person person = input.mockEntity(1);
        person.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(person));
        var result = personServices.findById(1L);

        validatePersonAttributes(result);
    }

    @Test
    void testFindById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> personServices.findById(1L));
    }

    @Test
    void testFindById_NullEntityCreation() {
        Exception ex = assertThrows(RequiredObjectIsNullException.class, () -> personServices.create(null));
        String expectedMessage = "Not allowed to persist a null object!";
        assertEquals(expectedMessage, ex.getMessage());
    }

    @SuppressWarnings("null")
    @Test
    void testCreate_success() {
        Person person = input.mockEntity(1);
        Person persisted = person;
        PersonDTO dto = input.mockDTO(1);

        when(repository.save(person)).thenReturn(persisted);
        var result = personServices.create(dto);

        validatePersonAttributes(result);
    }

    @Test
    void testUpdate_NullEntityCreation() {
        Exception ex = assertThrows(RequiredObjectIsNullException.class, () -> personServices.update(null));
        String expectedMessage = "Not allowed to persist a null object!";
        assertEquals(expectedMessage, ex.getMessage());
    }

    @SuppressWarnings("null")
    @Test
    void testUpdate_success() {
        Person person = input.mockEntity(1);
        Person persisted = person;
        PersonDTO dto = input.mockDTO(1);

        when(repository.findById(1L)).thenReturn(Optional.of(person));
        when(repository.save(person)).thenReturn(persisted);

        var result = personServices.update(dto);

        validatePersonAttributes(result);
    }

    @SuppressWarnings("null")
    @Test
    void testDelete_success() {
        Person person = input.mockEntity(1);

        when(repository.findById(1L)).thenReturn(Optional.of(person));
        personServices.delete(person.getId());
        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(Person.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testDelete_NotFound() {
        assertThrows(ResourceNotFoundException.class, () -> personServices.delete(999L));
    }
}
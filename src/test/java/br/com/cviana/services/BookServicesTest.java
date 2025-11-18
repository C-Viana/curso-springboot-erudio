package br.com.cviana.services;

import br.com.cviana.data.dto.BookDTO;
import br.com.cviana.exceptions.RequiredObjectIsNullException;
import br.com.cviana.exceptions.ResourceNotFoundException;
import br.com.cviana.models.Book;
import br.com.cviana.repository.BookRepository;
import br.com.cviana.unittests.mapper.mocks.MockBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServicesTest {

    @InjectMocks
    private BookService bookServices;

    @Mock
    private BookRepository repository;

    private MockBook input;
    private String expectedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(LocalDate.now());
    private String testDate = null;

    @SuppressWarnings("null")
    private void validateBookAttributes(BookDTO dto) {
        Instant inst = Instant.ofEpochMilli(new Date().getTime());
        testDate = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(LocalDateTime.ofInstant(inst, TimeZone.getDefault().toZoneId()));
        
        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals("Author Name "+dto.getId(), dto.getAuthor());
        assertEquals("Book Title "+dto.getId(), dto.getTitle());
        assertEquals(expectedDate, testDate);
        assertEquals(30.0, dto.getPrice());
        assertTrue(dto.getLinks().toList().size() > 0);

        assertTrue(dto.getLinks().toList().stream().anyMatch(link -> 
            link.getRel().value().equals("self") 
            && link.getHref().endsWith("/api/book/v1/" + dto.getId())
            && link.getTitle().equals("GET")
            )
        );
        assertTrue(dto.getLinks().toList().stream().anyMatch(link -> 
            link.getRel().value().equals("findAll") 
            && link.getHref().endsWith("/api/book/v1?page=0&quantity=10&sort=asc")
            && link.getTitle().equals("GET")
            )
        );
        assertTrue(dto.getLinks().toList().stream().anyMatch(link -> 
            link.getRel().value().equals("create") 
            && link.getHref().endsWith("/api/book/v1")
            && link.getTitle().equals("POST")
            )
        );
        assertTrue(dto.getLinks().toList().stream().anyMatch(link -> 
            link.getRel().value().equals("update") 
            && link.getHref().endsWith("/api/book/v1")
            && link.getTitle().equals("PUT")
            )
        );
        assertTrue(dto.getLinks().toList().stream().anyMatch(link -> 
            link.getRel().value().equals("delete") 
            && link.getHref().endsWith("/api/book/v1/" + dto.getId())
            && link.getTitle().equals("DELETE")
            )
        );
    }

    @BeforeEach
    void setUp() {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Disabled("TEST UNDER MAINTENANCE DUE PAGEABLE IMPLEMENTATION")
    void testFindAll_success() {
        /*List<Book> list = input.mockEntityList();
        when(repository.findAll()).thenReturn(list);

        var people = bookServices.findAll();
        assertNotNull(people);
        assertEquals(14, people.size());

        for (int i = 0; i < people.size(); i++) {
            validateBookAttributes(people.get(i));
        }*/
    }

    @Test
    void testFindById_success() {
        Book book = input.mockEntity(1);
        book.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(book));
        var result = bookServices.findById(1L);
        validateBookAttributes(result);
    }

    @Test
    void testFindById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bookServices.findById(1L));
    }

    @Test
    void testFindById_NullEntityCreation() {
        Exception ex = assertThrows(RequiredObjectIsNullException.class, () -> bookServices.create(null));
        String expectedMessage = "SERVICE_LOG::FAILED Cannot create a null book record";
        assertEquals(expectedMessage, ex.getMessage());
    }

    @SuppressWarnings("null")
    @Test
    void testCreate_success() {
        Book book = input.mockEntity(2);
        BookDTO dto = input.mockDTO(2);

        when(repository.save(Mockito.any())).thenReturn(book);
        var result = bookServices.create(dto);

        validateBookAttributes(result);
        
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(repository).save(bookCaptor.capture());
        Book savedBook = bookCaptor.getValue();
        
        assertNotNull(savedBook);
        assertEquals(dto.getAuthor(), savedBook.getAuthor());
        assertEquals(dto.getTitle(), savedBook.getTitle());
        assertEquals(dto.getPrice(), savedBook.getPrice());
    }

    @Test
    void testUpdate_NullEntityCreation() {
        Exception ex = assertThrows(RequiredObjectIsNullException.class, () -> bookServices.update(null));
        String expectedMessage = "SERVICE_LOG::FAILED Cannot create a null book record";
        assertEquals(expectedMessage, ex.getMessage());
    }

    @SuppressWarnings("null")
    @Test
    void testUpdate_success() {
        Book book = input.mockEntity(1);
        Book persisted = book;
        BookDTO dto = input.mockDTO(1);

        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(repository.save(any(Book.class))).thenReturn(persisted);

        var result = bookServices.update(dto);

        validateBookAttributes(result);
    }

    @SuppressWarnings("null")
    @Test
    void testDelete_success() {
        Book book = input.mockEntity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(book));

        bookServices.delete(book.getId());
        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(Book.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testDelete_NotFound() {
        assertThrows(ResourceNotFoundException.class, () -> bookServices.delete(999L));
    }
}
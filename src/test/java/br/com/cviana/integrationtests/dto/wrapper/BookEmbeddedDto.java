package br.com.cviana.integrationtests.dto.wrapper;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.cviana.integrationtests.dto.BookDTO;


public class BookEmbeddedDto implements Serializable {

	private static final long serialVersionUID = 1L;

    @JsonProperty("books")
    private List<BookDTO> people;

    public BookEmbeddedDto() {}

    public List<BookDTO> getBooks() {
        return people;
    }

    public void setBooks(List<BookDTO> people) {
        this.people = people;
    }
}

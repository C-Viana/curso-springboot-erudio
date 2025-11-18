package br.com.cviana.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cviana.models.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}

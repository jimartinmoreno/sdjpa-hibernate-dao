package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;

import java.util.List;
import java.util.Optional;

/**
 * Created by jt on 8/29/21.
 */
public interface BookDao {
    Book findByISBN(String isbn);

    List<Book> findAll();

    List<Book> findAllNamed();

    Book getById(Long id);

    Optional<Book> findById(Integer id);

    Optional<Book> findByTitle(String title);

    Optional<Book> findByTitleNamedQuery(String title);

    Book findBookByTitleCriteria(String title);

    Book findBookByTitleNative(String title);

    Book findBookByTitle(String title);

    Book saveNewBook(Book book);

    Optional<Book> save(Book book);

    Book updateBook(Book book);

    void deleteBookById(Long id);

}

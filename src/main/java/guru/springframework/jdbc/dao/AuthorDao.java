package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;

import java.util.List;
import java.util.Optional;

/**
 * Created by jt on 8/22/21.
 */
public interface AuthorDao {

    List<Author> findAllNamed();

    List<Author> listAuthorByLastNameLike(String lastName);

    Optional<Author> findById(Integer id);

    List<Author> findAll();

    Optional<Author> findByName(String firstName, String lastName);

    Optional<Author> findByNamedName(String firstName, String lastName);

    Author findAuthorByNameCriteria(String firstName, String lastName);

    Author findAuthorByNameNative(String firstName, String lastName);

    Optional<Author> save(Author author);

    Author getById(Long id);

    Author findAuthorByName(String firstName, String lastName);

    Author saveNewAuthor(Author author);

    Author updateAuthor(Author author);

    void deleteAuthorById(Long id);
}

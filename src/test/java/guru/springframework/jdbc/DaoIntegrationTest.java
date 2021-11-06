package guru.springframework.jdbc;

import guru.springframework.jdbc.dao.AuthorDao;
import guru.springframework.jdbc.dao.BookDao;
import guru.springframework.jdbc.domain.Author;
import guru.springframework.jdbc.domain.Book;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by jt on 8/28/21.
 */
@ActiveProfiles("local")
@DataJpaTest
@ComponentScan(basePackages = {"guru.springframework.jdbc.dao"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DaoIntegrationTest {
    @Autowired
    AuthorDao authorDao;

    @Autowired
    BookDao bookDao;

    @Test
    void testFindAllAuthors() {
        List<Author> authors = authorDao.findAllNamed();

        assertThat(authors).isNotNull();
        assertThat(authors.size()).isPositive();
    }

    @Test
    void testFindBookByISBN() {
        Book book = new Book();
        book.setIsbn("1234" + RandomString.make());
        book.setTitle("ISBN TEST");
        bookDao.saveNewBook(book);

        Book fetched = bookDao.findByISBN(book.getIsbn());
        assertThat(fetched).isNotNull();
    }

    @Test
    void testListAuthorByLastNameLike() {
        List<Author> authors = authorDao.listAuthorByLastNameLike("Wall");

        assertThat(authors).isNotNull();
        assertThat(authors.size()).isPositive();
    }

    @Test
    void testDeleteBook() {
        Book book = new Book();
        book.setIsbn("1234");
        book.setPublisher("Self");
        book.setTitle("my book");
        Book saved = bookDao.saveNewBook(book);

        bookDao.deleteBookById(saved.getId());

        Book deleted = bookDao.getById(saved.getId());

        assertThat(deleted).isNull();
    }

    @Test
    void updateBookTest() {
        Book book = new Book();
        book.setIsbn("1234");
        book.setPublisher("Self");
        book.setTitle("my book");

        Author author = new Author();
        author.setFirstName("John");
        author.setLastName("Thompson");
        Author savedAuthor = authorDao.saveNewAuthor(author);

        book.setAuthor(savedAuthor);
        Book savedBook = bookDao.saveNewBook(book);

        savedBook.setTitle("New Book");
        bookDao.updateBook(savedBook);

        Book fetched = bookDao.getById(savedBook.getId());

        assertThat(fetched.getTitle()).isEqualTo("New Book");

        authorDao.deleteAuthorById(savedAuthor.getId());
    }

    @Test
        //@Commit
    void testSaveBook() {
        Book book = new Book();
        book.setIsbn("1234");
        book.setPublisher("Self");
        book.setTitle("my book");

        Author author = new Author();
        author.setFirstName("John");
        author.setLastName("Thompson");
        Author savedAuthor = authorDao.saveNewAuthor(author);

        book.setAuthor(savedAuthor);
        Book savedBook = bookDao.saveNewBook(book);

        assertThat(savedBook).isNotNull();

        authorDao.deleteAuthorById(savedAuthor.getId());
        Author deletedAuthor = authorDao.getById(savedAuthor.getId());
        Book deletedBook = bookDao.getById(savedBook.getId());
        assertThat(deletedAuthor).isNull();
        assertThat(deletedBook).isNull();
    }

    @Test
    void testSaveBookAndAuthor() {
        Book book = new Book();
        book.setIsbn("1234");
        book.setPublisher("Self");
        book.setTitle("my book");

        Author author = new Author();
        author.setFirstName("John");
        author.setLastName("Thompson");
        author.addBook(book);
        Author savedAuthor = authorDao.saveNewAuthor(author);

        assertThat(savedAuthor).isNotNull();
        assertThat(savedAuthor.getBooks().size()).isPositive();

        authorDao.deleteAuthorById(savedAuthor.getId());
        Author deletedAuthor = authorDao.getById(savedAuthor.getId());
        Book deletedBook = bookDao.getById(book.getId());
        assertThat(deletedAuthor).isNull();
        assertThat(deletedBook).isNull();
    }

    @Test
    void testGetBookByTitle() {
        Book book = bookDao.findBookByTitle("Clean Code");
        assertThat(book).isNotNull();
    }

    @Test
    void testGetBookByTitleCriteria() {
        Book book = bookDao.findBookByTitleCriteria("Clean Code");
        assertThat(book).isNotNull();
    }

    @Test
    void testGetBookByTitleNative() {
        Book book = bookDao.findBookByTitleNative("Clean Code");
        assertThat(book).isNotNull();
    }

    @Test
    void testGetBook() {
        Book book = bookDao.getById(3L);
        assertThat(book.getId()).isNotNull();
    }

    @Test
    void testDeleteAuthor() {
        Author author = new Author();
        author.setFirstName("john");
        author.setLastName("t");

        Author saved = authorDao.saveNewAuthor(author);

        authorDao.deleteAuthorById(saved.getId());

        Author deleted = authorDao.getById(saved.getId());
        assertThat(deleted).isNull();
        assertThat(authorDao.getById(saved.getId())).isNull();
    }

    @Test
    void testUpdateAuthor() {
        Author author = new Author();
        author.setFirstName("john");
        author.setLastName("t");

        Author saved = authorDao.saveNewAuthor(author);

        saved.setLastName("Thompson");
        Author updated = authorDao.updateAuthor(saved);

        assertThat(updated.getLastName()).isEqualTo("Thompson");

        authorDao.deleteAuthorById(updated.getId());
    }

    @Test
    void testSaveAuthor() {
        Author author = new Author();
        author.setFirstName("John");
        author.setLastName("Thompson");
        Author saved = authorDao.saveNewAuthor(author);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();

        authorDao.deleteAuthorById(saved.getId());
    }

    @Test
    void testGetAuthorByName() {
        Author author = authorDao.findAuthorByName("Craig", "Walls");
        assertThat(author).isNotNull();
    }

    @Test
    void testGetAuthorByNameCriteria() {
        Author author = authorDao.findAuthorByNameCriteria("Craig", "Walls");
        assertThat(author).isNotNull();
    }

    @Test
    void testGetAuthorByNameNative() {
        Author author = authorDao.findAuthorByNameNative("Craig", "Walls");
        assertThat(author).isNotNull();
    }

    @Test
    void testGetAuthor() {
        Author author = authorDao.getById(1L);
        assertThat(author).isNotNull();
    }

    @Test
    void testGetAllAuthor() {
        List<Author> authors = authorDao.findAll();
        assertThat(authors).isNotNull();
        assertThat(authors.size()).isPositive();

    }
}
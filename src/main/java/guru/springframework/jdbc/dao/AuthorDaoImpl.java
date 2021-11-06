package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

/**
 * Created by jt on 8/28/21.
 */
@Component
public class AuthorDaoImpl implements AuthorDao {

    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public List<Author> findAllNamed() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Author> typedQuery = em.createNamedQuery("author_find_all", Author.class);

            return typedQuery.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Author> listAuthorByLastNameLike(String lastName) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Author> query = em.createQuery("SELECT a from Author a where a.lastName like :last_name", Author.class);
            query.setParameter("last_name", lastName + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Author> findById(Integer id) {
        EntityManager em = getEntityManager();
        Author author = em.find(Author.class, id);
        em.close();
        return author != null ? Optional.of(author) : Optional.empty();
    }

    @Override
    public List<Author> findAll() {
        EntityManager em = getEntityManager();
        TypedQuery<Author> query = em.createQuery("SELECT a FROM Author a", Author.class);
        List<Author> authors = query.getResultList();
        em.close();
        System.out.println("authors: " + authors);
        return authors;
    }

    @Override
    public Optional<Author> findByNamedName(String firstName, String lastName) {
        EntityManager em = getEntityManager();
        TypedQuery<Author> query = em.createNamedQuery("Author.findByName", Author.class);
        query.setParameter("first_name", firstName);
        query.setParameter("last_name", lastName);
        Author author = query.getSingleResult();
        em.close();
        return author != null ? Optional.of(author) : Optional.empty();
    }

    @Override
    public Optional<Author> findByName(String firstName, String lastName) {
        EntityManager em = getEntityManager();
        TypedQuery<Author> query = em.createQuery("SELECT a FROM Author a " +
                "WHERE a.firstName = :first_name and a.lastName = :last_name", Author.class);
        query.setParameter("first_name", firstName);
        query.setParameter("last_name", lastName);
        Author author = query.getSingleResult();
        em.close();
        return author != null ? Optional.of(author) : Optional.empty();
    }

    @Override
    public Author findAuthorByNameCriteria(String firstName, String lastName) {
        EntityManager em = getEntityManager();

        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Author> criteriaQuery = criteriaBuilder.createQuery(Author.class);

            Root<Author> root = criteriaQuery.from(Author.class);

            ParameterExpression<String> firstNameParam = criteriaBuilder.parameter(String.class);
            ParameterExpression<String> lastNameParam = criteriaBuilder.parameter(String.class);

            Predicate firstNamePred = criteriaBuilder.equal(root.get("firstName"), firstNameParam);
            Predicate lastNamePred = criteriaBuilder.equal(root.get("lastName"), lastNameParam);

            criteriaQuery.select(root).where(criteriaBuilder.and(firstNamePred, lastNamePred));

            TypedQuery<Author> typedQuery = em.createQuery(criteriaQuery);
            typedQuery.setParameter(firstNameParam, firstName);
            typedQuery.setParameter(lastNameParam, lastName);

            return typedQuery.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Author findAuthorByNameNative(String firstName, String lastName) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNativeQuery("SELECT * FROM author a WHERE a.first_name = ? and a.last_name = ?", Author.class);
            query.setParameter(1, firstName);
            query.setParameter(2, lastName);
            return (Author) query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Author> save(Author author) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(author);
            em.flush();
            em.getTransaction().commit();
            em.close();
            return Optional.of(author);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Author getById(Long id) {
        EntityManager em = getEntityManager();
        Author author = em.find(Author.class, id);
        em.close();
        System.out.println("getById author OUT: " + author);
        return author;
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        EntityManager em = getEntityManager();

        //Typed query por posicion "Positional Parameters"

//        TypedQuery<Author> query = em.createQuery("SELECT a FROM Author a " +
//                "WHERE a.firstName = ?1 and a.lastName = ?2", Author.class);
//        query.setParameter(1, firstName);
//        query.setParameter(2, lastName);

        //Typed query por nombre "Named Parameters"
        TypedQuery<Author> query = em.createQuery("SELECT a FROM Author a " +
                "WHERE a.firstName = :first_name and a.lastName = :last_name", Author.class);
        query.setParameter("first_name", firstName);
        query.setParameter("last_name", lastName);

        Author author = query.getSingleResult();
        em.close();
        return author;
    }

    @Override
    public Author saveNewAuthor(Author author) {
        EntityManager em = getEntityManager();

        /**
         *  persist() -> JPA por defecto hace un lazy save en la BD, para forzarlo hay que incluirlo
         *  en una transacción con joinTransaction o iniciandola nosotros explicitamente:
         *
         *  em.getTransaction().begin();
         *  em.getTransaction().commit();
         *
         *  joinTransaction() lanza un excepcion si no hay una transacción en progreso mejor crearlo
         *  explicitamente.
         */

        em.getTransaction().begin();
        //em.joinTransaction(); //
        em.persist(author);
        em.flush();
        em.getTransaction().commit();
        em.close();

        System.out.println("author: " + author);
        return author;
    }

    @Override
    public Author updateAuthor(Author author) {
        System.out.println("author IN: " + author);
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.merge(author);
        // flush fuerza hibernate a actualizar la entidad en la bd no solo en el context
        em.flush();
        // clear fuerza hibernate a limpiar esta entidad de la cache e ir a buscarla de nuevo a la BD
        em.clear();
        em.getTransaction().commit();
        em.close();

        return author;
    }

    @Override
    public void deleteAuthorById(Long id) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Author author = em.find(Author.class, id);
        em.remove(author);
        em.flush();
        em.getTransaction().commit();
        em.close();
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}

















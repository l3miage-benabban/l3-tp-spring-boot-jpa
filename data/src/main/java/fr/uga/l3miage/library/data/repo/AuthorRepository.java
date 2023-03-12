package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.Author;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Enumeration;
import java.util.List;

@Repository
public class AuthorRepository implements CRUDRepository<Long, Author> {

    private final EntityManager entityManager;

    @Autowired
    public AuthorRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Author save(Author author) {
        entityManager.persist(author);
        return author;
    }

    @Override
    public Author get(Long id) {
        return entityManager.find(Author.class, id);
    }


    @Override
    public void delete(Author author) {
        entityManager.remove(author);
    }

    /**
     * Renvoie tous les auteurs
     *
     * @return une liste d'auteurs trié par nom
     */
    @Override
    public List<Author> all() {
        TypedQuery<Author> query = entityManager.createQuery(
                "SELECT a FROM Author a ORDER BY a.fullName", Author.class);
        return query.getResultList();
    }

    /**
     * Recherche un auteur par nom (ou partie du nom) de façon insensible  à la casse.
     *
     * @param namePart tout ou partie du nomde l'auteur
     * @return une liste d'auteurs trié par nom
     */
    public List<Author> searchByName(String namePart) {
        TypedQuery<Author> query = entityManager.createQuery(
                "SELECT a FROM Author a WHERE a.fullName LIKE CONCAT('%', :namePart, '%')",
                Author.class);
        query.setParameter("namePart", namePart);
        return query.getResultList();
    }

    /**
     * Recherche si l'auteur a au moins un livre co-écrit avec un autre auteur
     *
     * @return true si l'auteur partage
     */
    public boolean checkAuthorByIdHavingCoAuthoredBooks(long authorId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(b.id) FROM Book b WHERE b.id IN " +
                        "(SELECT book.id FROM Book book JOIN book.authors author " +
                        "WHERE author.id = :authorId) AND SIZE(b.authors) > 1", Long.class);
        query.setParameter("authorId", authorId);
        Long count = query.getSingleResult();
        return count > 0;
    }

}

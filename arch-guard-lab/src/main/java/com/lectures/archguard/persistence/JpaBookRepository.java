package com.lectures.archguard.persistence;

import com.lectures.archguard.domain.Book;
import com.lectures.archguard.domain.BookRepository;
import com.lectures.archguard.domain.Isbn;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class JpaBookRepository implements BookRepository {

    @PersistenceContext(unitName = "archGuardPU")
    private EntityManager entityManager;

    @Override
    public Optional<Book> findByIsbn(Isbn isbn) {
        return Optional.ofNullable(entityManager.find(BookEntity.class, isbn.value()))
                .map(BookEntity::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return entityManager.createQuery("select b from BookEntity b", BookEntity.class)
                .getResultList()
                .stream()
                .map(BookEntity::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void save(Book book) {
        entityManager.merge(BookEntity.fromDomain(book));
    }
}

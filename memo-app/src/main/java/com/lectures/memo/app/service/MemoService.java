package com.lectures.memo.app.service;

import com.lectures.memo.app.entity.Memo;
import com.lectures.memo.app.service.dto.CategoryCount;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Random;
import java.util.Set;

@ApplicationScoped
@Transactional
public class MemoService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("title", "dateCreated", "category");
    private static final String DEFAULT_SORT_FIELD = "dateCreated";

    @PersistenceContext
    EntityManager entityManager;

    public Memo findMemoById(Long id) {
        return entityManager.find(Memo.class, id);
    }

    public Memo findRandom() {
        List<Long> ids = entityManager.createQuery("SELECT m.id FROM Memo m", Long.class).getResultList();
        if (ids.isEmpty()) {
            return null;
        }
        Long randomId = ids.get(new Random().nextInt(ids.size()));
        return findMemoById(randomId);
    }

    public List<Memo> findAll(String sortBy, String direction) {
        String field = (sortBy != null && ALLOWED_SORT_FIELDS.contains(sortBy)) ? sortBy : DEFAULT_SORT_FIELD;
        String dir = "asc".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        return entityManager
                .createQuery("SELECT m FROM Memo m ORDER BY m." + field + " " + dir, Memo.class)
                .getResultList();
    }

    public long countAll() {
        return entityManager.createQuery("SELECT COUNT(m) FROM Memo m", Long.class).getSingleResult();
    }

    public List<CategoryCount> countByCategory() {
        return entityManager.createQuery(
                "SELECT NEW com.lectures.memo.app.service.dto.CategoryCount(m.category, COUNT(m)) "
                + "FROM Memo m GROUP BY m.category",
                CategoryCount.class)
                .getResultList();
    }

    public Memo createMemo(Memo memo) {
        entityManager.persist(memo);
        return memo;
    }

    public Memo updateMemo(Memo memo) {
        entityManager.merge(memo);
        return memo;
    }

    public void deleteMemo(Long id) {
        var memo = findMemoById(id);
        if (memo != null) {
            entityManager.remove(memo);
        }
    }
}

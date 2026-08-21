package com.lectures.service;

import com.lectures.entity.Todo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class TodoService {

    @PersistenceContext
    EntityManager entityManager;

    public Todo findTodoById(Long id) {
        return entityManager.find(Todo.class, id);
    }

    public List<Todo> getTodos() {
        return entityManager
                .createQuery("SELECT t from Todo t", Todo.class)
                .getResultList();
    }

    public Todo createTodo(Todo todo) {
        entityManager.persist(todo);
        return todo;
    }

    public Todo updateTodo(Todo todo) {
        entityManager.merge(todo);
        return todo;
    }

    public void deleteTodo(Long id) {
        var todo = findTodoById(id);
        if (todo != null) {
            entityManager.remove(todo);
        }
    }
}

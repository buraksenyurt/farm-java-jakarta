package com.lectures.todoapp.event;

import com.lectures.todoapp.entity.Todo;

/*
    Bir todo oluşturulduğunda sisteme bunu duyurabileceğimi event sınıfı
    POJO
 */
public class TodoCreatedEvent {

    private final Todo todo;

    public TodoCreatedEvent(Todo todo) {
        this.todo = todo;
    }

    public Todo getTodo() {
        return todo;
    }
}

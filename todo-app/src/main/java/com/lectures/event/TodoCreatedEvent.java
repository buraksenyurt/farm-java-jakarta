package com.lectures.event;

import com.lectures.entity.Todo;

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

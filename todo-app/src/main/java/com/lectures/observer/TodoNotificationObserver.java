package com.lectures.observer;

import com.lectures.event.TodoCreatedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/*
    Olay dinleyici (Event Observer)

    Sistemde herhangibir yerde TodoCreatedEvent fırlatıldığında
otomatik olarak uyanır ve ilgili metod çalıştırılır.
 */
// Tüm uygulama boyunca tek bir instance(Singleton) kullanılacağını söylüyoruz
@ApplicationScoped
public class TodoNotificationObserver {

    public void onTodoCreated(@Observes TodoCreatedEvent event) {
        String title = event.getTodo().getTask();
        // Şimdilik yine terminale bilgi veriyoruz. Bir loglayıcı veya 
        // email bildirim sistemi de kullanılabilir
        System.out.println("[EVENT FIRED]" + " New todo `" + title + "` has been created.");
    }
}

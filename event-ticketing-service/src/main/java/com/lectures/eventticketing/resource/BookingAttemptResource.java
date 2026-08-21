package com.lectures.eventticketing.resource;

import com.lectures.eventticketing.model.BookingAttempt;
import com.lectures.eventticketing.repository.BookingAttemptRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

// Testler sırasında kullanacağımız yardımcı endpoint'lerden birisi
@Path("booking-attempts")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class BookingAttemptResource {

    @Inject
    private BookingAttemptRepository repository;

    @GET
    public List<BookingAttempt> getAll() {
        return repository.findAll();
    }
}

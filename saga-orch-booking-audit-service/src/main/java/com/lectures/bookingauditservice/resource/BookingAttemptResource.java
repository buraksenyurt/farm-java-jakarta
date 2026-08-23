package com.lectures.bookingauditservice.resource;

import com.lectures.bookingauditservice.dto.BookingAttemptRequest;
import com.lectures.bookingauditservice.model.BookingAttempt;
import com.lectures.bookingauditservice.repository.BookingAttemptRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("booking-attempts")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookingAttemptResource {

    @Inject
    private BookingAttemptRepository repository;

    @POST
    @Transactional
    public Response create(BookingAttemptRequest request) {
        BookingAttempt attempt = new BookingAttempt();

        attempt.setEventId(request.eventId());
        attempt.setCustomerId(request.customerId());
        attempt.setSeatCount(request.seatCount());
        attempt.setStatus(request.status());
        attempt.setFailureReason(request.failureReason());
        attempt.setAttemptTime(LocalDateTime.now());

        repository.save(attempt);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public List<BookingAttempt> getAll() {
        return repository.findAll();
    }
}

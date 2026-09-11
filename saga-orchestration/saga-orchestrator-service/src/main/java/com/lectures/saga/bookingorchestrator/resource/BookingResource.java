package com.lectures.saga.bookingorchestrator.resource;

import com.lectures.saga.bookingorchestrator.dto.BookingRequest;
import com.lectures.saga.bookingorchestrator.service.BookingSagaOrchestrator;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("bookings")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookingResource {

    @Inject
    BookingSagaOrchestrator orchestrator;

    @POST
    public Response book(BookingRequest request) {
        var result = orchestrator.bookTickets(request.eventId(), request.customerId(), request.seatCount());
        return Response.status(Response.Status.CREATED).entity(result).build();
    }
}

package com.lectures.eventticketing.resource;

import com.lectures.eventticketing.dto.BookingRequest;
import com.lectures.eventticketing.model.Booking;
import com.lectures.eventticketing.service.TicketBookingService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("bookings")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookingResource {

    @Inject
    private TicketBookingService bookingService;

    @POST
    public Response book(BookingRequest request) {
        Booking booking = bookingService.bookTickets(request.eventId(), request.customerId(), request.seatCount());
        return Response.status(Response.Status.CREATED).entity(booking).build();
    }

}

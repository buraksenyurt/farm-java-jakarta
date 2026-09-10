package com.lectures.saga.eventservice.resource;

import com.lectures.saga.eventservice.dto.SeatRequest;
import com.lectures.saga.eventservice.model.Event;
import com.lectures.saga.eventservice.repository.EventRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("events")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    private EventRepository eventRepository;

    @GET
    @Path("{id}")
    public Event getById(@PathParam("id") Long id) {
        return eventRepository.findById(id);
    }

    @POST
    @Path("{id}/reserve-seats")
    @Transactional
    public Event reserveSeats(@PathParam("id") Long id, SeatRequest request) {
        Event event = eventRepository.findById(id);
        eventRepository.reserveSeats(event, request.seatCount());
        return event;
    }

    @POST
    @Path("{id}/release-seats")
    @Transactional
    public Response releaseSeats(@PathParam("id") Long id, SeatRequest request) {
        Event event = eventRepository.findById(id);
        eventRepository.releaseSeats(event, request.seatCount());
        return Response.ok().build();
    }
}

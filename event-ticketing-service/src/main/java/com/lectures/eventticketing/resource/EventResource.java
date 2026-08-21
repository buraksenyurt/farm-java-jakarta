package com.lectures.eventticketing.resource;

import com.lectures.eventticketing.model.Event;
import com.lectures.eventticketing.repository.EventRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

// Testler sırasında kullanacağımız yardımcı endpoint'lerden birisi
@Path("events")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    private EventRepository eventRepository;

    @GET
    @Path("{id}")
    public Event getById(@PathParam("id") Long id) {
        return eventRepository.findById(id);
    }
}

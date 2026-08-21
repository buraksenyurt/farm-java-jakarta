package com.lectures.inventory.events.service.resources;

import com.lectures.inventory.figures.messaging.Publisher;
import com.lectures.inventory.figures.model.Figure;
import com.lectures.inventory.figures.repository.FigureRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("inventory")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InventoryResource {

    private static final Logger logger = LoggerFactory.getLogger(InventoryResource.class);

    @Inject
    private FigureRepository repository;

    @Inject
    private Publisher publisher;

    @POST
    @Path("stock-arrival")
    public Response handleStockArrival(Figure figure) {
        logger.info("REST isteği alındı: {}", figure.getName());

        repository.save(figure);
        publisher.publishStockArrival(figure);

        return Response.status(Response.Status.ACCEPTED)
                .entity("Stok kaydı alındı ve event fırlatıldı.")
                .build();
    }
}

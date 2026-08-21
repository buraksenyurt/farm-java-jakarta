package com.lectures.inventory.notification.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("health")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {

    @GET
    public Response check() {
        return Response.ok("{\"status\":\"UP\"}").build();
    }
}
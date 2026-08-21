package com.lectures.gamecatalog.resources;

import com.lectures.gamecatalog.model.*;
import com.lectures.gamecatalog.service.GameService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.*;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;
import java.util.List;

@Path("games")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GameResource {

    @Inject
    private GameService gameService;

    @POST

    public Response create(@Valid Game game, @Context UriInfo uriInfo) {
        Game created = gameService.create(game);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(created.getId()))
                .build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("{id}")
    public Game update(@PathParam("id") Long id, @Valid Game game) {
        return gameService.update(id, game);
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        gameService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("{id}")
    public Game getById(@PathParam("id") Long id) {
        return gameService.getById(id);
    }

    @GET
    public List<Game> getAll(@QueryParam("genre") Genre genre, @QueryParam("platform") Platform platform) {
        if (genre != null) {
            return gameService.getByGenre(genre);
        }
        if (platform != null) {
            return gameService.getByPlatform(platform);
        }
        return gameService.getAll();
    }
}

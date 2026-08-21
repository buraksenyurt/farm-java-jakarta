package com.lectures.java.games.games.api;

import com.lectures.java.games.games.repository.GameRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author buraks
 */
@Path("games")
public class GameResource {

    @Inject
    private GameRepository gameRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGames() {
        var games = gameRepository.findAll();
        return Response.ok(games).build();
    }
}

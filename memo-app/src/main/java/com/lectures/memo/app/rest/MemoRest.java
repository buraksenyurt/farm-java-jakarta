package com.lectures.memo.app.rest;

import com.lectures.memo.app.entity.Memo;
import com.lectures.memo.app.service.MemoService;
import com.lectures.memo.app.service.dto.MemoStats;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;

@Path("memo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MemoRest {

    @Inject
    MemoService memoService;

    @Path("{id}")
    @GET
    public Memo getMemo(@PathParam("id") Long id) {
        // api/v1/memo/{id}
        return memoService.findMemoById(id);
    }

    @Path("random")
    @GET
    public Memo getMemoRandom() {
        // api/v1/memo/{id}
        return memoService.findRandom();
    }

    @Path("list")
    @GET
    public List<Memo> getMemos(@QueryParam("sortBy") String sortBy, @QueryParam("direction") String direction) {
        // api/v1/memo/list?sortBy=&direction=
        return memoService.findAll(sortBy, direction);
    }

    @Path("stats")
    @GET
    public MemoStats getStats() {
        // api/v1/memo/stats
        return new MemoStats(memoService.countAll(), memoService.countByCategory());
    }

    @Path("new")
    @POST
    public Response createMemo(Memo memo) {
        // api/v1/memo/new
        var created = memoService.createMemo(memo);
        var uri = UriBuilder.fromResource(MemoRest.class).path(created.getId().toString()).build();

        return Response.created(uri).build();
    }

    @Path("update")
    @PUT
    public Response updateMemo(Memo memo) {
        // api/v1/memo/update
        memoService.updateMemo(memo);
        return Response.ok(memo).build();
    }

    @Path("{id}")
    @DELETE
    public Response deleteMemo(@PathParam("id") Long id) {
        // api/v1/memo/{id}
        memoService.deleteMemo(id);
        return Response.noContent().build();
    }
}

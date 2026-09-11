package com.lectures.archguard.api;

import com.lectures.archguard.application.LoanService;
import com.lectures.archguard.domain.Book;
import com.lectures.archguard.domain.Isbn;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    private final LoanService loanService;

    protected BookResource() {
        this.loanService = null;
    }

    @Inject
    public BookResource(LoanService loanService) {
        this.loanService = loanService;
    }

    @GET
    public List<BookView> findAll() {
        return loanService.listAll().stream().map(BookView::from).toList();
    }

    @POST
    @Path("/{isbn}/loans")
    public Response borrow(@PathParam("isbn") String isbn) {
        Book book = loanService.borrow(new Isbn(isbn));
        return Response.ok(BookView.from(book)).build();
    }
}
package com.lectures.cdi.rest;

import com.lectures.cdi.service.InvoiceAcceptService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/payment")
@RequestScoped
public class InvoicePaymentResource {

    @Inject
    private InvoiceAcceptService acceptService;

    @GET
    @Path("/standard")
    public Response doStandard() {
        String result = acceptService.procesStandard(1000.0);
        return Response.ok(result).build();
    }

    @GET
    @Path("/crypto")
    public Response doCrypto() {
        String result = acceptService.processCrypto(15.0);
        return Response.ok(result).build();
    }
}

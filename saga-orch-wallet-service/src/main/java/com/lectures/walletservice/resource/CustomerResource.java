package com.lectures.walletservice.resource;

import com.lectures.walletservice.dto.ChargeRequest;
import com.lectures.walletservice.model.Customer;
import com.lectures.walletservice.repository.CustomerRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("customers")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    private CustomerRepository customerRepository;

    @GET
    @Path("{id}")
    public Customer getById(@PathParam("id") Long id) {
        return customerRepository.findById(id);
    }

    @POST
    @Path("{id}/charge")
    @Transactional
    public Customer charge(@PathParam("id") Long id, ChargeRequest request) {
        Customer customer = customerRepository.findById(id);
        customerRepository.charge(customer, request.amount());
        return customer;
    }

    @POST
    @Path("{id}/refund")
    @Transactional
    public Response refund(@PathParam("id") Long id, ChargeRequest request) {
        Customer customer = customerRepository.findById(id);
        customerRepository.refund(customer, request.amount());
        return Response.ok().build();
    }
}

package com.lectures.eventticketing.resource;

import com.lectures.eventticketing.model.Customer;
import com.lectures.eventticketing.repository.CustomerRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

// Testler sırasında kullanacağımız yardımcı endpoint'lerden bir diğeri
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
}

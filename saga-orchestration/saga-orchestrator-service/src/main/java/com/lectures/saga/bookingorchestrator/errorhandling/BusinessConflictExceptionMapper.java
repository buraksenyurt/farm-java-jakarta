package com.lectures.saga.bookingorchestrator.errorhandling;

import com.lectures.saga.bookingorchestrator.exception.BusinessConflictException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class BusinessConflictExceptionMapper implements ExceptionMapper<BusinessConflictException> {

    @Override
    public Response toResponse(BusinessConflictException e) {
        return Response.status(Response.Status.CONFLICT)
                .entity(Map.of("error", e.getMessage()))
                .build();
    }

}

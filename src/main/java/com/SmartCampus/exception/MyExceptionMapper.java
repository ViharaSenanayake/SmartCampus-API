
package com.SmartCampus.exception;



import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MyExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException ex) {
        return Response.status(ex.getResponse().getStatus())
                .entity("{\"errorMessage\": \"" + ex.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

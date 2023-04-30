package org.acme;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/hello")
public class GreetingResource {

    private static final Logger LOG = Logger.getLogger(GreetingResource.class);

    @RestClient
    ExchangeService exchangeService;
    

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello() {

        LOG.info("hello()");

        return exchangeService.get();
    }
}

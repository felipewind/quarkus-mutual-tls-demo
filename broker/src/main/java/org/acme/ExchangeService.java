package org.acme;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/hello")
@RegisterRestClient(configKey = "exchange-api")
public interface ExchangeService {

    @GET
    Response get();
    
}

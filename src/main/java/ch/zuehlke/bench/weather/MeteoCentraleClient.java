package ch.zuehlke.bench.weather;


import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/uploads/pics")
@RegisterRestClient
public interface MeteoCentraleClient {

    @GET
    @Path("/uwz-ch_foehn_de.png")
    @Produces("image/png")
    File getFoehnDiagram();

}

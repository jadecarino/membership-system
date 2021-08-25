package synoptic.project.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/query")
public class Query {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getProperties() {
        return "Hello world";
    }

}

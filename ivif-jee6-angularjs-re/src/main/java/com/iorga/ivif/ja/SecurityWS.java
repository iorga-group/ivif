package com.iorga.ivif.ja;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collection;

@Path("/security")
public class SecurityWS {
    @Inject @CurrentRoles
    Collection<String> currentRoles;

    @Path("/currentRoles")
    @GET
    @Produces("application/json")
    public Collection<String> getCurrentRoles() {
        return currentRoles;
    }
}

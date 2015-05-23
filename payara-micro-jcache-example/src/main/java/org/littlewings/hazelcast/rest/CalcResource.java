package org.littlewings.hazelcast.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.littlewings.hazelcast.service.CalcService;

@Path("calc")
@RequestScoped
public class CalcResource {
    @Inject
    private CalcService calcService;

    @GET
    @Path("add")
    @Produces(MediaType.TEXT_PLAIN)
    public int add(@QueryParam("a") @DefaultValue("0") int a, @QueryParam("b") @DefaultValue("0") int b) {
        return calcService.add(a, b);
    }
}

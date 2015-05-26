package org.littlewings.hazelcast.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.littlewings.hazelcast.service.MessageService;
import org.littlewings.hazelcast.service.TripleService;

@Path("dist")
@RequestScoped
public class DistributedMapResource {
    @Inject
    private MessageService messageService;

    @GET
    @Path("simple")
    @Produces(MediaType.TEXT_PLAIN)
    public String simple(@QueryParam("key") @DefaultValue("key") String key, @QueryParam("word") @DefaultValue("World") String word) {
        return messageService.build(key, word);
    }

    @Inject
    private TripleService tripleService;

    @GET
    @Path("expiry")
    @Produces(MediaType.TEXT_PLAIN)
    public int expiry(@QueryParam("key") @DefaultValue("key") String key, @QueryParam("value") @DefaultValue("0") int value) {
        return tripleService.execute(key, value);
    }
}

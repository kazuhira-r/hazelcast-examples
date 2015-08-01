package org.littlewings.hazelcast;

import java.util.concurrent.TimeUnit;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("cachedcalc")
@RequestScoped
public class CachedCalcResource {
    @GET
    @Path("add")
    @Produces(MediaType.TEXT_PLAIN)
    @CacheResult(cacheName = "calcCache")
    public int add(@QueryParam("a") int a, @QueryParam("b") int b) throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);

        return a + b;
    }
}

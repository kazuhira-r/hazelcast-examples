package org.littlewings.hazelcast;

import java.util.concurrent.TimeUnit;
import javax.cache.Cache;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fish.payara.cdi.jsr107.impl.NamedCache;

@Path("expirycalc")
@RequestScoped
public class ExpiryCalcResource {
    @NamedCache(cacheName = "expiryCache", expiryPolicyFactoryClass = MyExpiryPolicy.class)
    @Inject
    private Cache expiryCache;

    @GET
    @Path("add")
    @Produces(MediaType.TEXT_PLAIN)
    public String add(@QueryParam("a") int a, @QueryParam("b") int b) throws Exception {
        String key = a + "+" + b;

        if (expiryCache.containsKey(key)) {
            return String.format("CacheName = %s, result = %d", expiryCache.getName(), expiryCache.get(key));
        } else {
            TimeUnit.SECONDS.sleep(3);

            int result = a + b;
            expiryCache.put(key, result);

            return String.format("CacheName = %s, result = %d", expiryCache.getName(), result);
        }
    }
}

package org.littlewings.hazelcast;

import java.util.concurrent.TimeUnit;
import javax.cache.Cache;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fish.payara.cdi.jsr107.impl.NamedCache;

@Path("calc")
@RequestScoped
public class CalcResource {
    @NamedCache(cacheName = "myCache")
    @Inject
    private Cache myCache;
    // private Cache<String, Integer> myCache;

    @GET
    @Path("add")
    @Produces(MediaType.TEXT_PLAIN)
    public String add(@QueryParam("a") int a, @QueryParam("b") int b) throws Exception {
        ClassLoader cl = this.getClass().getClassLoader();
        while (cl != null) {
            System.out.println("My ClassLoader = " + cl + ", class = " + cl.getClass().getName());
            cl = cl.getParent();
        }

        String key = a + "+" + b;

        if (myCache.containsKey(key)) {
            return String.format("CacheName = %s, result = %d", myCache.getName(), myCache.get(key));
        } else {
            TimeUnit.SECONDS.sleep(3);

            int result = a + b;
            myCache.put(key, result);

            return String.format("CacheName = %s, result = %d", myCache.getName(), result);
        }
    }
}

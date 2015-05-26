package org.littlewings.hazelcast.rest;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.hazelcast.core.HazelcastInstance;
import fish.payara.nucleus.hazelcast.HazelcastCore;

@Path("lookup")
@RequestScoped
public class HazelcastLookupResource {
    @GET
    @Path("jndi")
    @Produces(MediaType.TEXT_PLAIN)
    public String jndi() throws NamingException {
        InitialContext context = new InitialContext();

        try {
            HazelcastInstance instance = (HazelcastInstance) context.lookup("payara/Hazelcast");
            return instance.getName();
        } finally {
            context.close();
        }
    }

    @Resource(name = "payara/Hazelcast")
    private HazelcastInstance hazelcastInstanceByResourcd;

    @GET
    @Path("resource")
    @Produces(MediaType.TEXT_PLAIN)
    public String resource() {
        return hazelcastInstanceByResourcd.getName();
    }

    @Inject
    private HazelcastInstance hazelcastInstanceByCdi;

    @GET
    @Path("cdi")
    @Produces(MediaType.TEXT_PLAIN)
    public String cdi() {
        return hazelcastInstanceByCdi.getName();
    }

    @GET
    @Path("internal")
    @Produces(MediaType.TEXT_PLAIN)
    public String internal() {
        HazelcastCore core = HazelcastCore.getCore();
        HazelcastInstance instance = core.getInstance();
        return instance.getName();
    }
}

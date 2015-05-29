package org.littlewings.hazelcast.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("session")
public class SessionResource {
    @GET
    @Path("put/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public User put(@PathParam("name") String name, @QueryParam("age") int age, @Context HttpServletRequest request) {
        User user = new User(name, age);
        request.getSession().setAttribute(name, user);
        return user;
    }

    @GET
    @Path("update/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public User update(@PathParam("name") String name, @QueryParam("age") int age, @Context HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(name);
        user.age = age;
        return user;
    }

    @GET
    @Path("get/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public User get(@PathParam("name") String name, @Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (User) session.getAttribute(name);
    }
}

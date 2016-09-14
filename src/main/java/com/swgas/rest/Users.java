package com.swgas.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.swgas.rest.dao.Dao;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArrayBuilder;

@Path("users")
public class Users {
    private final Dao dao = Dao.getInstance();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUsers() {
        JsonArrayBuilder aBuilder = Json.createArrayBuilder();
        dao.getAllUsers().forEach(map -> aBuilder.add(Json.createObjectBuilder().add("firstName", map.get("first_name")).add("lastName", map.get("last_name"))));
        return Json.createObjectBuilder().add("users", aBuilder).build().toString();
    }
    
    @Path("/info")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUsersWithInfo(){
        JsonArrayBuilder aBuilder = Json.createArrayBuilder();
        dao.getAllUsersWithInfo().forEach(map -> aBuilder.add(Json.createObjectBuilder().add("firstName", map.get("first_name")).add("lastName", map.get("last_name")).add("description", map.get("description")).add("location", map.get("location")).add("phone", map.get("phone")).add("email", map.get("email"))));
        return Json.createObjectBuilder().add("users", aBuilder).build().toString();
    }
}
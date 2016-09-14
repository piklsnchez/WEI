package com.swgas.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.swgas.rest.dao.Dao;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArrayBuilder;

@Path("planning_dates")
public class PlanningDates {
    private final Dao dao = Dao.getInstance();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPlanningDates() {
        JsonArrayBuilder aBuilder = Json.createArrayBuilder();
        dao.getPlanningDates().forEach(map -> aBuilder.add(Json.createObjectBuilder().add("date", map.get("date")).add("title", map.get("title"))));
        return Json.createObjectBuilder().add("planningDates", aBuilder).build().toString();
    }
}
package com.swgas.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import com.swgas.rest.dao.Dao;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArrayBuilder;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

@Path("sessions")
public class Sessions {
    private static final String CLASS = Sessions.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    private final Dao dao = Dao.getInstance();
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSessions() {
        JsonArrayBuilder aBuilder = Json.createArrayBuilder();
        dao.getAllSessions().forEach(map -> aBuilder.add(Json.createObjectBuilder().add("name", map.get("name")).add("title", map.get("title"))));
        return Json.createObjectBuilder().add("sessions", aBuilder).build().toString();
    }
    
    @Path("/facilitators")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSessionsWithFacilitators(){
        JsonArrayBuilder aBuilder = Json.createArrayBuilder();
        dao.getAllSessionsWithFacilitators().stream().collect(Collectors.groupingBy(map -> ((Map<String, String>)map).get("session_name")
            , TreeMap::new
            , Collectors.mapping(p->p, Collectors.toList())))
        .forEach((k,v) -> {
            JsonArrayBuilder mcBuilder = Json.createArrayBuilder();
            v.forEach(map -> mcBuilder.add(Json.createObjectBuilder().add("firstName", map.get("first_name")).add("lastName", map.get("last_name"))));
            aBuilder.add(Json.createObjectBuilder().add("name", v.get(0).get("session_name")).add("title", v.get(0).get("session_title")).add("mcs", mcBuilder));
        });
        return Json.createObjectBuilder().add("sessions", aBuilder).build().toString();
    }
    
    @Path("/notes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSessionsWithNotes(){
        JsonArrayBuilder aBuilder = Json.createArrayBuilder();
        dao.getAllSessionsWithNotes().stream().collect(Collectors.groupingBy(map -> ((Map<String, String>)map).get("session_name")
            , TreeMap::new
            , Collectors.mapping(p->p, Collectors.toList())))
        .forEach((k,v) -> {
            JsonArrayBuilder noteBuilder = Json.createArrayBuilder();
            v.forEach(map -> noteBuilder.add(Json.createObjectBuilder().add("id", map.get("note_id")).add("note", map.get("note"))));
            aBuilder.add(Json.createObjectBuilder().add("name", v.get(0).get("session_name")).add("title", v.get(0).get("session_title")).add("notes", noteBuilder));
        });
        return Json.createObjectBuilder().add("sessions", aBuilder).build().toString();
    }
    
    @Path("/note/{id}")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void postSessionNotes(@PathParam("id") Integer id, String postData){
        LOG.entering(CLASS, "postSessionNotes", id);
        LOG.info(postData);
        dao.updateNote(id, postData);
        LOG.exiting(CLASS, "postSessionNotes");
    }
}
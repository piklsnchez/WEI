package com.swgas.rest;

import java.util.logging.Logger;
import java.io.IOException;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    public static final String BASE_URI = "http://0.0.0.0:8080/wei/";
    
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        HttpHandler httpHandler = new CLStaticHttpHandler(HttpServer.class.getClassLoader(), "/");
        server.getServerConfiguration().addHttpHandler(httpHandler, "/");
        LOG.info(String.format("%nWEI microservices started.%nWADL available at %sapplication.wadl%nHit enter to stop it...%n", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
    
    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("com.swgas.rest");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
}
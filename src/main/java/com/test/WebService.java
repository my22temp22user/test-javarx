package com.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/service/test")
public class WebService {

    @GET
    @Path("/count/events/type/{type}")
    @Produces(MediaType.TEXT_PLAIN)
    public Long countEventsType(@PathParam("type") String type) {
        return Metrics.getInstance().numOfEventType(type);
    }

    @GET
    @Path("/count/data/word/{word}")
    @Produces(MediaType.TEXT_PLAIN)
    public Long countDataWord(@PathParam("word") String word) {
        return Metrics.getInstance().numOfWordsInData(word);
    }
}

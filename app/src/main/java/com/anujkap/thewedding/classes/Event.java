package com.anujkap.thewedding.classes;

import java.io.Serializable;

public class Event implements Serializable {
    String name;
    String timeStart;
    String venue;
    String description;

    public Event(){
        name = "Default";
    }
    public Event(String name, String timeStart, String venue, String description) {
        this.name = name;
        this.timeStart = timeStart;
        this.venue = venue;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getVenue() {
        return venue;
    }

    public String getDescription() {
        return description;
    }
}

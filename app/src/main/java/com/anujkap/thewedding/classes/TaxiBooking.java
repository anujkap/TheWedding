package com.anujkap.thewedding.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class TaxiBooking implements Serializable {
    public String from;
    public String to;
    public String timestamp;
    public String name;
    public String number;

    public TaxiBooking(String from, String to, String timestamp, String name, String number) {
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
        this.name = name;
        this.number = number;
    }

    @NonNull
    @Override
    public String toString() {
        return "Taxi booked by "+name+" pickup from "+from+" drop at "+to+".\n\nContact number: "+number;
    }
}

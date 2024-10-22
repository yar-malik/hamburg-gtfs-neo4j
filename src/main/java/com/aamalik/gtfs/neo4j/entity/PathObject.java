package com.aamalik.gtfs.neo4j.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class PathObject {
    @GraphId
    private Long id;

    @Property(name="arrival_time")
    private String arrivalTime;

    @Property(name="stop_sequence")
    private Integer stopSequence;

    @Property(name="departure_time_int")
    private Integer departureTimeInt;

    @Property(name="arrival_time_int")
    private Integer arrivalTimeInt;

    @Property(name="departure_time")
    private String departureTime;

    @Relationship(type = "LOCATED_AT", direction = Relationship.OUTGOING)
    private Set<Stop> stops;

    @Relationship(type = "PART_OF_TRIP")
    public Set<Trip> trips;

    @Relationship(type = "PRECEDES")
    public Set<Stoptime> precedesTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Integer getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(Integer stopSequence) {
        this.stopSequence = stopSequence;
    }

    public Integer getDepartureTimeInt() {
        return departureTimeInt;
    }

    public void setDepartureTimeInt(Integer departureTimeInt) {
        this.departureTimeInt = departureTimeInt;
    }

    public Integer getArrivalTimeInt() {
        return arrivalTimeInt;
    }

    public void setArrivalTimeInt(Integer arrivalTimeInt) {
        this.arrivalTimeInt = arrivalTimeInt;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public Set<Stop> getStops() {
        return stops;
    }

    public void setStops(Set<Stop> stops) {
        this.stops = stops;
    }

    public Set<Trip> getTrips() {
        return trips;
    }

    public void setTrips(Set<Trip> trips) {
        this.trips = trips;
    }

    public Set<Stoptime> getPrecedesTime() {
        return precedesTime;
    }

    public void setPrecedesTime(Set<Stoptime> precedesTime) {
        this.precedesTime = precedesTime;
    }
}
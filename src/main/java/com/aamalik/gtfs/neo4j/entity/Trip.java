package com.aamalik.gtfs.neo4j.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class Trip {

    @GraphId
    private Long id;

    @Property(name="id")
    private String tripId;

    @Property(name="service_id")
    private String serviceId;

    @Property(name="short_name")
    private String shortName;

    @Property(name="headsign")
    private String headsign;

    @Relationship(type = "USES")
    public Set<Route> routes;

    @Relationship(type = "PART_OF_TRIP", direction = Relationship.INCOMING)
    public Set<Stoptime> stoptimes;

    @Relationship(type = "RUNS_DURING")
    public Set<CalendarDate> calendarDates;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getShortName() { return shortName; }

    public void setShortName(String shortName) { this.shortName = shortName; }

    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(Set<Route> routes) {
        this.routes = routes;
    }

    public Set<Stoptime> getStoptimes() {
        return stoptimes;
    }

    public void setStoptimes(Set<Stoptime> stoptimes) {
        this.stoptimes = stoptimes;
    }

    public Set<CalendarDate> getCalendarDates() {
        return calendarDates;
    }

    public void setCalendarDates(Set<CalendarDate> calendarDates) {
        this.calendarDates = calendarDates;
    }
}

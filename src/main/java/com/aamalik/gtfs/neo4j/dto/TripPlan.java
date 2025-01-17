package com.aamalik.gtfs.neo4j.dto;

public class TripPlan {

    private String travelDate;
    private String origStation;
    private String origArrivalTimeLow;
    private String origArrivalTimeHigh;
    private String destStation;
    private String destArrivalTimeLow;
    private String destArrivalTimeHigh;

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String serviceId) {
        this.travelDate = serviceId;
    }

    public String getOrigStation() {
        return origStation;
    }

    public void setOrigStation(String origStation) {
        this.origStation = origStation;
    }

    public String getOrigArrivalTimeHigh() {
        return origArrivalTimeHigh;
    }

    public void setOrigArrivalTimeHigh(String origArrivalTimeHigh) {
        this.origArrivalTimeHigh = origArrivalTimeHigh;
    }

    public String getDestStation() {
        return destStation;
    }

    public void setDestStation(String destStation) {
        this.destStation = destStation;
    }

    public String getDestArrivalTimeHigh() {
        return destArrivalTimeHigh;
    }

    public void setDestArrivalTimeHigh(String destArrivalTimeHigh) {
        this.destArrivalTimeHigh = destArrivalTimeHigh;
    }

    public String getOrigArrivalTimeLow() {
        return origArrivalTimeLow;
    }

    public void setOrigArrivalTimeLow(String origArrivalTimeLow) {
        this.origArrivalTimeLow = origArrivalTimeLow;
    }

    public String getDestArrivalTimeLow() {
        return destArrivalTimeLow;
    }

    public void setDestArrivalTimeLow(String destArrivalTimeLow) {
        this.destArrivalTimeLow = destArrivalTimeLow;
    }

}

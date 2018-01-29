package com.aamalik.gtfs.neo4j.entity.projection.stoptime;

import com.aamalik.gtfs.neo4j.entity.Stoptime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

/**
 * Created by tgulesserian on 5/19/17.
 */
@Projection(name = "TripPlanResult", types = { Stoptime.class })
public interface TripPlanResultPjcn {

    public String getArrivalTime();
    public String getDepartureTime();
    public int getStopSequence();

    @Value("#{target.stops.iterator().next().getName()}")
    public String getStopName();

    @Value("#{target.stops.iterator().next().getLongitude()}")
    public String getLongitude();

    @Value("#{target.stops.iterator().next().getLatitude()}")
    public String getLatitude();

    @Value("#{target.trips.iterator().next().getTripId()}")
    public String getTripId();

    //Set<TripNoBackrefsPjcn> getTrips();
}

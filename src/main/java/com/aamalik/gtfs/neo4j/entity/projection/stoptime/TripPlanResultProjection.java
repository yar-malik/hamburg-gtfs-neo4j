package com.aamalik.gtfs.neo4j.entity.projection.stoptime;

import com.aamalik.gtfs.neo4j.entity.Stoptime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "TripPlanResult", types = { Stoptime.class })
public interface TripPlanResultProjection {

    @Value("#{target.trips.iterator().next().getShortName()}")
    public String getTripShortName();

    public Integer getStopSequence();
    public Integer getTrip_id();

    @Value("#{target.stops.iterator().next().getName()}")
    public String getStopName();

    @Value("#{target.trips.iterator().next().getHeadsign()}")
    public String getHeadsign();

    @Value("#{target.stops.iterator().next().getLongitude()}")
    public String getLongitude();

    @Value("#{target.stops.iterator().next().getLatitude()}")
    public String getLatitude();

    @Value("#{target.stops.iterator().next().getStopId()}")
    public String getStop_Id();

}

package com.aamalik.gtfs.neo4j.repository;

import com.aamalik.gtfs.neo4j.entity.Trip;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface TripRepository extends Neo4jRepository<Trip, Long> {
    Trip findByTripId(@Param("tripId") String tripId, @Depth @Param("depth") Integer depth);
}

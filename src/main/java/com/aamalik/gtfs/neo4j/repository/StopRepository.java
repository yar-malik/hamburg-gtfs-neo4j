package com.aamalik.gtfs.neo4j.repository;

import com.aamalik.gtfs.neo4j.entity.Stop;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;


public interface StopRepository extends Neo4jRepository<Stop, Long> {

    Stop findByName(@Param("stopName") String stopName,@Depth @Param("depth") Integer depth);

    Stop findByStopId(@Param("stopId") String stopId, @Depth @Param("depth") Integer depth);
}

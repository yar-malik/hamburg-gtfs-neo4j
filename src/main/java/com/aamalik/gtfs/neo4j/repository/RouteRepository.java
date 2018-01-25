package com.aamalik.gtfs.neo4j.repository;

import com.aamalik.gtfs.neo4j.entity.Route;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface RouteRepository extends Neo4jRepository<Route,Long>  {
    Route findByRouteId(@Param("routeId") String routeId, @Depth @Param("depth") int depth);
}

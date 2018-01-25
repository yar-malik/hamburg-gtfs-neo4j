package com.aamalik.gtfs.neo4j.repository;

import com.aamalik.gtfs.neo4j.entity.CalendarDate;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(collectionResourceRel = "calendarDate", path = "calendarDate")
public interface CalendarDateRepository extends Neo4jRepository<CalendarDate, Long> {
}

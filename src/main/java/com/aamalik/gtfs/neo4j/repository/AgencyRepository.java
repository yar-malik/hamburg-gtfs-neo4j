package com.aamalik.gtfs.neo4j.repository;

import com.aamalik.gtfs.neo4j.entity.Agency;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(collectionResourceRel = "agency", path = "agency")
public interface AgencyRepository extends Neo4jRepository<Agency, Long> {

    Agency findByAgencyId(@Param("agencyId") String agencyId, @Depth @Param("depth") int depth);

}
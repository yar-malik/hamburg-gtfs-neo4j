package com.aamalik.gtfs.neo4j.repository;

import com.aamalik.gtfs.neo4j.entity.Stoptime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface StoptimeRepository extends Neo4jRepository<Stoptime, Long> {

    //---------------------------SHORTEST PATH(DIJKSTRA lenght)-------------------------------------------------------------------------------
    @Query(" MATCH(s:Stop), (e:Stop)\n" +
                    " where s.name = {origStation} and e.name = {destStation} \n" +
                    " match p = shortestpath((s)-[*]-(e))\n" +
                    " return p, s.id as stopid " +
                    " ;")
    Page<Stoptime> shortestPath(@Param("origStation") String origStation,
                                       @Param("destStation") String destStation,
                                     Pageable pageRequest);


    //---------------------------specificTripOneDirection-------------------------------------------------------------------------------
    @Query(" match (s:Stop {name: {origStation} })--(st:Stoptime)  \n" +
            " with s, st  \n" +
            " match (e:Stop {name:{destStation}})--(et:Stoptime)  \n" +
            " where et.arrival_time > st.departure_time  \n" +
            " with e,et,s, st  \n" +
            " match p = allshortestpaths((st)-[*]->(et))  \n" +
            " with nodes(p) as n, s, e\n" +
            " unwind n as nodes  \n" +
            " match (nodes)-[r]-()  \n" +
            " return nodes,r,s,e, nodes.stop_sequence as stopSequence, nodes.trip_id as tripId")
    Page<Stoptime> specificTripOneDirection(
                        @Param("origStation") String origStation,
                        @Param("destStation") String destStation,
                        Pageable pageRequest);


    @Query("match (s:Stop)--(st:Stoptime)   \n" +
            "              with s, st      \n" +
            "              match (e:Stop)--(et:Stoptime)  \n" +
            "              where s.name IN [ 'Schlump', 'U Schlump']     \n" +
            "and e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ]         \n" +
            "              with e,et,s, st      \n" +
            "              match p = allshortestpaths((et)-[:PRECEDES*]-(st))      \n" +
            "              with nodes(p) as n, s, st , e    \n" +
            "              unwind n as nodes      \n" +
            "              match (nodes)-[r]-() \n" +
            "              return nodes,r,s,e, nodes.stop_sequence as stopSequence, nodes.trip_id as tripId")
    Page<Stoptime> specificTripBothDirection(Pageable pageRequest);


    @Query("MATCH(s:Stop)--(st:Stoptime),\n" +
            "(e:Stop)--(et:Stoptime)\n" +
            "WHERE s.name = 'Dörpsweg' \n" +
            "\tAND e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ] \n" +
            "AND et.arrival_time > st.departure_time \n" +
            "MATCH p = shortestPath((st)-[*]-(et))\n" +
            "WHERE ALL (r IN relationships(p) WHERE type(r) IN ['PRECEDES','LOCATED_AT','PART_OF_TRIP'])\n" +
            "RETURN  nodes(p) as nodes, rels(p) as rels ")
    Iterable<Map<String,Object>> findPathMitUmSteigen();


    @Query("MATCH(s:Stop)--(st:Stoptime),\n" +
            "(e:Stop)--(et:Stoptime)\n" +
            "where s.name IN [ 'Dörpsweg'] \n" +
            "\tand e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ]  \n" +
            "MATCH p = shortestpath((st)-[*]-(et))\n" +
            "WHERE ALL (r IN relationships(p) WHERE type(r) IN ['PRECEDES','LOCATED_AT'])\n" +
            "RETURN nodes(p) as nodes, rels(p) as rels,\n" +
            "reduce(distance=0, r in relationships(p) | distance+ toInt(r.weight)) AS totalWeight \n" +
            "       ORDER BY totalWeight ASC ")
    Iterable<Map<String,Object>> findPathMitWeights();


}
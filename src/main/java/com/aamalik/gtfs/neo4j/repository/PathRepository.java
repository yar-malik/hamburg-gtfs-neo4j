package com.aamalik.gtfs.neo4j.repository;

import com.aamalik.gtfs.neo4j.entity.PathObject;
import com.aamalik.gtfs.neo4j.entity.Stop;
import com.aamalik.gtfs.neo4j.entity.Stoptime;
import com.aamalik.gtfs.neo4j.entity.PathObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;
import java.util.Map;

public interface PathRepository extends Neo4jRepository<PathObject, Long> {

    @Query("match (s:Stop)--(st:Stoptime)   \n" +
            "              with s, st      \n" +
            "              match (e:Stop)--(et:Stoptime)  \n" +
            "              where s.name IN [ 'Schlump', 'U Schlump']     \n" +
            "and e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ]         \n" +
            "              with e,et,s, st      \n" +
            "              match p = allshortestpaths((et)-[:PRECEDES*]-(st))      \n" +
            "              with nodes(p) as n, s, st , e    \n" +
            "              return n as Paths")
    List<Map<String,Object>> specificTripBothDirectionCustom();

//    @Query("MATCH (s:Stop)--(st:Stoptime), \n" +
//            "(e:Stop)--(et:Stoptime)  \n" +
//            "WHERE s.name IN [ 'Schlump', 'U Schlump']     \n" +
//            "AND e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ]    \n" +
//            "WITH e,et,s, st      \n" +
////            "CALL apoc.algo.dijkstra(st, et, 'PRECEDES', 'duration') YIELD path, weight\n" +
//            "match p = allshortestpaths((et)-[:PRECEDES*]-(st))      \n" +
//            "              with nodes(p) as n, s, st , e    \n" +
//            "              unwind n as nodes      \n" +
//            "              match (nodes)-[r]-() \n" +
//            "              return nodes,r,s,e, nodes.stop_sequence as stopSequence, nodes.trip_id as tripId")
//    Iterable<Map<String, Object>> apocAlgoAllSimplePaths();

    @Query(" MATCH  (s:Stop)--(st:Stoptime)-[:PART_OF_TRIP]->(ts:Trip),\n" +
            " (e:Stop)--(et:Stoptime)-[:PART_OF_TRIP]->(te:Trip)\n" +
            " where s.name IN [ 'Schlump', 'U Schlump'] \n" +
            " and e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ]\n" +
            " call apoc.algo.dijkstra(st, et, 'PRECEDES', 'weight') YIELD path, weight \n" +
            " return nodes(path) as nodes, weight as weights, ts.id as tripId ")
    Page<PathObject> apocAlgoAllSimplePaths(Pageable pageable);


}


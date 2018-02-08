package com.aamalik.gtfs.neo4j.repository;

import com.aamalik.gtfs.neo4j.entity.PathObject;
import com.aamalik.gtfs.neo4j.entity.Stop;
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



    @Query("match (s:Stop)--(st:Stoptime), (e:Stop)--(et:Stoptime) \n" +
            "where s.name IN [ 'Schlump', 'U Schlump'] \n" +
            " and e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ] \n" +
            "CALL apoc.algo.allSimplePaths(st, et, 'PRECEDES', 20) yield path as path \n" +
            "RETURN path ")
    List<Map<String,Object>> apocAlgoAllSimplePaths();


    @Query("match (s:Stop), (e:Stop)    \n" +
            "where s.name IN ['U Schlump'] \n" +
            "\tand e.name IN ['Hauptbahnhof Süd', 'Hamburg Hbf', 'Hauptbahnhof Nord' ]\n" +
            "match p = shortestpath((s)-[*]-(e))\n" +
            "return p")
    List<Map<String,Stop>> checkStop();

}
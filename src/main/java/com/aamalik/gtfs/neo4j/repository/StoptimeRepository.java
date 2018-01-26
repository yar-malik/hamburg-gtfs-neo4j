package com.aamalik.gtfs.neo4j.repository;

import com.aamalik.gtfs.neo4j.entity.Stoptime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoptimeRepository extends Neo4jRepository<Stoptime, Long> {
    /*
        I got the sort parameter to pass in using spring data rest, but there seems to be a bug because the order is not
        coming back correct. I see it making into the cypher query - but JSON I get back has the first two records in the
        wrong order, but the rest are ok.
        http://localhost:8080/stoptimes/search/getMyTrips?serviceId=4&origStation=WESTWOOD&origArrivalTimeLow=06:30:00&origArrivalTimeHigh=07:10:00&destStation=HOBOKEN&destArrivalTimeLow=07:00:00&destArrivalTimeHigh=08:00:00&sort=stopSequence,asc
        http://localhost:8080/stoptimes/search/getMyTrips?serviceId=4&origStation=WESTWOOD&origArrivalTimeLow=06:30:00&origArrivalTimeHigh=07:10:00&destStation=HOBOKEN&destArrivalTimeLow=07:00:00&destArrivalTimeHigh=08:00:00&sort=departureTimeInt,asc
        With projection:
        http://localhost:8080/stoptimes/search/getMyTrips?serviceId=4&origStation=WESTWOOD&origArrivalTimeLow=06:30:00&origArrivalTimeHigh=07:10:00&destStation=HOBOKEN&destArrivalTimeLow=07:00:00&destArrivalTimeHigh=08:00:00&sort=departureTimeInt,asc&projection=TripPlanResult
    */
    //----------------------------------------------------------------------------------------------------------
    @Query(
            "MATCH\n" +
            "  (cd:CalendarDate)\n" +
            "WHERE \n" +
            "   cd.date = {travelDate} AND\n" +
            "   cd.exception_type = '1'\n" +
            "WITH \n" +
            "   cd\n" +
            "MATCH\n" +
            "  (orig:Stop {name: {origStation}})--(orig_st:Stoptime)-[r1:PART_OF_TRIP]->(trp:Trip)\n" +
            "WHERE\n"+
            "  orig_st.departure_time > {origArrivalTimeLow}\n" +
            "  AND orig_st.departure_time < {origArrivalTimeHigh}\n" +
            "  AND trp.service_id=cd.service_id\n" +
            "WITH\n"+
            "  orig, orig_st, cd\n" +
            "MATCH\n" +
            "    (dest:Stop {name: {destStation}})--(dest_st:Stoptime)-[r2:PART_OF_TRIP]->(trp2:Trip)\n" +
            "WHERE\n"+
            "    dest_st.arrival_time < {destArrivalTimeHigh}\n" +
            "    AND dest_st.arrival_time > {destArrivalTimeLow}\n" +
            "    AND dest_st.arrival_time > orig_st.departure_time\n"+
            "    AND trp2.service_id=cd.service_id\n" +
            "WITH\n"+
            "    dest,dest_st,orig, orig_st\n" +
            "MATCH\n" +
            "    p = allShortestPaths((orig_st)-[*]->(dest_st))\n" +
            "WITH\n" +
            "    nodes(p) AS n\n" +
            "UNWIND\n" +
            "    n AS stoptimes\n" +
            "MATCH\n" +
            "    p2=(stoptimes)-[r2:PART_OF_TRIP]->(trip)\n" +
            "MATCH\n" +
            "    p=(stoptimes)-[r:LOCATED_AT]->(stop)\n" +
            "RETURN\n" +
            "    p, p2,\n" +
            "   stoptimes.departure_time_int AS departureTimeInt, \n" +
            "   trip.id AS tripId"
            )
    Page<Stoptime> getMyTrips(@Param("travelDate") String travelDate,
                              @Param("origStation") String origStation,
                              @Param("origArrivalTimeLow") String origArrivalTimeLow,
                              @Param("origArrivalTimeHigh") String origArrivalTimeHigh,
                              @Param("destStation") String destStation,
                              @Param("destArrivalTimeLow") String destArrivalTimeLow,
                              @Param("destArrivalTimeHigh") String destArrivalTimeHigh,
                              Pageable pageRequest);

    //----------------------------------------------------------------------------------------------------------
    //this is not working for me. Dont know why. Have to change some stuff here
    @Query("MATCH (tu:Stop {name:\"U Schlump\"})--(st_tu:Stoptime)--(trip1:Trip),  \n" +
            " (ar:Stop {name:\"Sartoriusstraße\"})--(st_ar:Stoptime)--(trip2:Trip),  \n" +
            " p1=(st_tu)-[:PRECEDES*]->(st_midway_arr:Stoptime),  \n" +
            " (st_midway_arr)--(midway:Stop),  \n" +
            " (midway)--(st_midway_dep:Stoptime),  \n" +
            " p2=(st_midway_dep)-[:PRECEDES*]->(st_ar) \n" +
            " WHERE  \n" +
            " st_tu.departure_time > \"21:00:00\"  \n" +
            " AND st_tu.departure_time < \"21:08:00\"  \n" +
            " AND st_ar.arrival_time > \"21:00:00\"  \n" +
            " AND st_ar.arrival_time < \"21:23:00\"\n" +
            " AND st_midway_arr.arrival_time > st_tu.departure_time  \n" +
            " AND st_midway_dep.departure_time > st_midway_arr.arrival_time  \n" +
            " AND st_ar.arrival_time > st_midway_dep.departure_time  \n" +
            " RETURN  \n" +
            " tu,st_tu,ar,st_ar,p1,p2,midway, tu.arrival_time as stopid, trip1, trip2")
    Page<Stoptime> oneTransfer(@Param("origStation") String origStation,
                             @Param("origArrivalTimeLow") String origArrivalTimeLow,
                             @Param("origArrivalTimeHigh") String origArrivalTimeHigh,
                             @Param("destStation") String destStation,
                             @Param("destArrivalTimeLow") String destArrivalTimeLow,
                             @Param("destArrivalTimeHigh") String destArrivalTimeHigh,
                               Pageable pageRequest);

    //----------------------------------------------------------------------------------------------------------
    @Query(" MATCH(s:Stop), (e:Stop)\n" +
                    " where s.name = {origStation} and e.name = {destStation} \n" +
                    " match p = shortestpath((s)-[*]-(e))\n" +
                    " return p, s.id as stopid " +
                    " ;")
    Page<Stoptime> shortestPath(@Param("origStation") String origStation,
                                       @Param("destStation") String destStation,
                                     Pageable pageRequest);

    //----------------------------------------------------------------------------------------------------------
    @Query(" match (tu:Stop {name: {origStation} })--(tu_st:Stoptime)  \n" +
            " where tu_st.departure_time > {origArrivalTimeLow}  \n" +
            " AND tu_st.departure_time < {origArrivalTimeHigh}  \n" +
            " with tu, tu_st  \n" +
            " match (ant:Stop {name:{destStation}})--(ant_st:Stoptime)  \n" +
            " where ant_st.arrival_time < {destArrivalTimeHigh}  \n" +
            " AND ant_st.arrival_time > {destArrivalTimeLow}  \n" +
            " and ant_st.arrival_time > tu_st.departure_time  \n" +
            " with ant,ant_st,tu, tu_st  \n" +
            " match p = allshortestpaths((tu_st)-[*]->(ant_st))  \n" +
            " with nodes(p) as n, tu, ant\n" +
            " unwind n as nodes  \n" +
            " match (nodes)-[r]-()  \n" +
            " return nodes,r,tu,ant, tu.id as stopid")
    Page<Stoptime> specificTrip(
                        @Param("origStation") String origStation,
                        @Param("origArrivalTimeLow") String origArrivalTimeLow,
                        @Param("origArrivalTimeHigh") String origArrivalTimeHigh,
                        @Param("destStation") String destStation,
                        @Param("destArrivalTimeLow") String destArrivalTimeLow,
                        @Param("destArrivalTimeHigh") String destArrivalTimeHigh,
                        Pageable pageRequest);
}
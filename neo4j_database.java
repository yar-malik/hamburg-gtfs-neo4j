--------------------------------------------------------
 LOAD CSV WITH HEADERS FROM\n" +
            "'file:///hamburg/calendar_dates.txt' AS csv\n" +
            "MATCH (t:Trip {service_id: csv.service_id})\n" +
            "CREATE (t)-[:RUNS_DURING]->(cd:CalendarDate{service_id: csv.service_id, date: csv.date, exception_type: csv.exception_type })

----------------------------------------------------------
load csv with headers from  
 'file:///hamburg/stops.txt' as csv  
 with csv  
 where not (csv.parent_station is null)  
 match (ps:Stop {id: csv.parent_station}), (s:Stop {id: csv.stop_id})  
 create (ps)<-[:PART_OF]-(s);

----------------------------------------------------------

match (tu:Stop {name: {origStation} })--(tu_st:Stoptime)      
              where tu_st.departure_time > {origArrivalTimeLow}      
              AND tu_st.departure_time < {origArrivalTimeHigh}      
              with tu, tu_st      
              match (ant:Stop {name:{destStation}})--(ant_st:Stoptime)      
              where ant_st.arrival_time < {destArrivalTimeHigh}      
              AND ant_st.arrival_time > {destArrivalTimeLow}      
              and ant_st.arrival_time > tu_st.departure_time      
              with ant,ant_st,tu, tu_st      
              match p = allshortestpaths((tu_st)-[*]->(ant_st))      
              with nodes(p) as n, tu, ant    
              unwind n as nodes      
              match (nodes)-[r]-()      
              return nodes,r,tu,ant, tu.id as stopid
----------------------------------------------------------


match (s1:Stoptime)-[:PART_OF_TRIP]->(t:Trip),  
 (s2:Stoptime)-[:PART_OF_TRIP]->(t)  
 where s2.stop_sequence=s1.stop_sequence+1 
      and not (s1)-[:PRECEDES]->(s2)  
 create (s1)-[:PRECEDES]->(s2); match (s1:Stoptime)-[:PART_OF_TRIP]->(t:Trip),  
 (s2:Stoptime)-[:PART_OF_TRIP]->(t)  
 where s2.stop_sequence=s1.stop_sequence+1 
      and not (s1)-[:PRECEDES]->(s2)  
 create (s1)-[:PRECEDES]->(s2);


 ------------------------------------------------
 //add the stoptimes  
 using periodic commit  
 load csv with headers from  
 'file:///hamburg/stop_times.txt' as csv  
 match (t:Trip {id: csv.trip_id}), (s:Stop {id: csv.stop_id})  
 create (t)<-[:PART_OF_TRIP]-(st:Stoptime {arrival_time: csv.arrival_time, departure_time: csv.departure_time, stop_sequence: toInt(csv.stop_sequence)})-[:LOCATED_AT]->(s);  


 ---------------Adding Property To Relationship--------------------------

 match(s:Stoptime), (e:Stoptime)
match p = ((s)-[r:PRECEDES]->(e))
set r.duration = apoc.date.parse(e.arrival_time,'s','HH:mm:ss') - apoc.date.parse(s.departure_time, 's', 'HH:mm:ss')


---------------- Trips (Non Duplicated)---------------------
load csv with headers from  
'file:///hamburg/trips.txt' as csv    
merge (t:Trip {headsign: csv.trip_headsign})
set t.id =  csv.trip_id
set t.service_id = csv.service_id
set t.direction_id =  csv.direction_id
set t.short_name =  csv.trip_short_name
set t.route_id = csv.route_id

match (r:Route), (t:Trip)
where r.id = t.route_id
create (t)-[:USES]->(r)
------------------------------------------------------------
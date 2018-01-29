 LOAD CSV WITH HEADERS FROM\n" +
            "'file:///nmbs/calendar_dates.txt' AS csv\n" +
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
 create (s1)-[:PRECEDES]->(s2); 
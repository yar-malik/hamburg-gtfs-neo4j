 create constraint on (a:Agency) assert a.id is unique;  
 create constraint on (r:Route) assert r.id is unique;  
 create constraint on (t:Trip) assert t.id is unique;  
 create index on :Trip(service_id);  
 create constraint on (s:Stop) assert s.id is unique;  
 create index on :Stoptime(stop_sequence);  
 create index on :Stop(name); 
 -----------------
  load csv with headers from  
 'file:///hamburg/agency.txt' as csv  
 create (a:Agency {id: toInt(csv.agency_id), name: csv.agency_name});  
 ---------------------

 load csv with headers from  
 'file:///hamburg/routes.txt' as csv  
 match (a:Agency {id: toInt(csv.agency_id)})  
 create (a)-[:OPERATES]->(r:Route {id: csv.route_id, short_name: csv.route_short_name, long_name: csv.route_long_name, type: toInt(csv.route_type)});
 ---------------------
 load csv with headers from  
 'file:///hamburg/trips.txt' as csv  
 match (r:Route {id: csv.route_id})  
 create (r)<-[:USES]-(t:Trip {id: csv.trip_id, service_id: csv.service_id, headsign: csv.trip_headsign, direction_id: csv.direction_id, short_name: csv.trip_short_name});  

 ---------------------

 load csv with headers from  
 'file:///hamburg/stops.txt' as csv  
 create (s:Stop {id: csv.stop_id, name: csv.stop_name, latitude: toFloat(csv.stop_lat), longitude: toFloat(csv.stop_lon), platform_code: csv.platform_code, parent_station: csv.parent_station, location_type: csv.location_type, timezone: csv.stop_timezone, code: csv.stop_code});  
 ---------------------
 load csv with headers from  
 'file:///hamburg/stops.txt' as csv  
 with csv  
 where not (csv.parent_station is null)  
 match (ps:Stop {id: csv.parent_station}), (s:Stop {id: csv.stop_id})  
 create (ps)<-[:PART_OF]-(s)

 
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

----------------------------Add Stoptimes--------------------------------

using periodic commit  
 load csv with headers from  
 'file:///hamburg/stop_times.txt' as csv  
 create (st:Stoptime {arrival_time: csv.arrival_time, departure_time: csv.departure_time, stop_sequence: toInt(csv.stop_sequence), stop_id:csv.stop_id, trip_id:csv.trip_id });

-----------------------
match (t:Trip), (st:Stoptime)
where t.id = st.trip_id
create (t)<-[:PART_OF_TRIP]-(st)
-----------------------

match (st:Stoptime)
where not ()<-[:PART_OF_TRIP]-(st)
detach delete st

-----------------------

match (s:Stop), (st:Stoptime)
where s.id = st.stop_id
create (s)<-[:LOCATED_AT]-(st)


-----------------StopTime Connection-----------------

match (s1:Stoptime)-[:PART_OF_TRIP]->(t:Trip),  
 (s2:Stoptime)-[:PART_OF_TRIP]->(t)  
 where s2.stop_sequence=s1.stop_sequence+1  
 create (s1)-[:PRECEDES]->(s2);

-------------------------Transfers-----------------------------------------
load csv with headers from  
'file:///hamburg/transfers.txt' as csv   
 create (t:Transfers {from_stop_id: csv.from_stop_id, to_stop_id: csv.to_stop_id, transfer_type: csv.transfer_type});
------------
match(t:Transfers), (s1:Stop), (s2:Stop)
where t.from_stop_id = s1.id and t.to_stop_id = s2.id
create (s1)-[:TRANSFER]->(s2)
------------
match p = (n:Stop)-[r:TRANSFER]->(n)
return p
limit 25
------------
match p = (n:Stop)-[r:TRANSFER]->(n)
detach delete r


-------------------------Properties To Labels----------------------------
match(s:Stoptime), (e:Stoptime)
match p = ((s)-[r:PRECEDES]->(e))
set r.weight = 1

match(s:Stoptime), (e:Stoptime)
match p = ((s)-[r:PRECEDES]->(e))
set r.duration = apoc.date.parse(e.arrival_time,'s','HH:mm:ss') - apoc.date.parse(s.departure_time, 's', 'HH:mm:ss')

 ------------------------Spatial------------------------------

CALL spatial.addPointLayer('spatialGeometry');

match (n:Stop) 
call spatial.addNode('spatialGeometry', n) Yield node
Return node;

-------------------------Query-----------------------------------------

CALL spatial.bbox('spatialGeometry',{lon:9.946658,lat:53.561669}, {lon:10.072805, lat:53.584786}) 

CALL spatial.closest('spatialGeometry',{lon:9.946658,lat:53.561669}, 0.001) Yield node
return count(node)


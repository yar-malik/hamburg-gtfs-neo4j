---------------------------------------------
---------------Find All Nodes--------------
MATCH (n)
RETURN n
---------------------------------------------
-----Find All Relations---------
MATCH p=()-[r:LOCATED_AT]->() RETURN p LIMIT 100
---------------------------------------------
------Find Shortest Path Between Nodes--------
match(s:Stop), (e:Stop)
where s.name starts with "U Schlump" and e.name starts with "Sartoriusstraße"
match p = shortestpath((s)-[*]-(e))
return p
-------------------------------------------------
------Find Indirect Path Between Nodes-----------
MATCH(s:Stop), (e:Stop)
WHERE s.name starts with "U Schlump" and e.name = "U Eppendorfer Baum"
match p = allshortestpaths((s)-[*]-(e))
where NONE (x in relationships(p) where type(x)="OPERATES")
return p
limit 25
-------------------------------------------------------
------Find Specific Indirect Path Between Nodes--------
MATCH (tu:Stop {name:"U Schlump"})--(st_tu:Stoptime),
(ar:Stop {name:"Sartoriusstraße"})--(st_ar:Stoptime),
p1=((st_tu)-[:PRECEDES*]->(st_midway_arr:Stoptime)),
(st_midway_arr)--(midway:Stop),
(midway)--(st_midway_dep:Stoptime),
p2=((st_midway_dep)-[:PRECEDES*]->(st_ar))
WHERE
st_tu.departure_time > "08:00:00"
AND st_tu.departure_time < "11:00:00"
AND st_midway_arr.arrival_time > st_tu.departure_time
AND st_midway_dep.departure_time > st_midway_arr.arrival_time
AND st_ar.arrival_time > st_midway_dep.departure_time
RETURN
tu,st_tu,ar,st_ar,p1,p2,midway
order by (st_ar.arrival_time_int-st_tu.departure_time_int) ASC
limit 1
-------------------------------------------------------
------Find Specific Indirect Path Between Nodes--------
match(n:Route),(s:Stop),(e:Stop)
where n.short_name='U3' and s.name starts with "U Schlump" and e.name starts with "U Eppendorfer Baum"
match p = shortestpath((s)-[*]-(e))
return p
limit 25
-------------------------------------------------------
------Change Datatype Of A Node--------
match(a:Agency)
where a.id=toInt(a.id)
with a
set a.id = toString(a.id)
return count(a)
-------------------------------------------------------
------Direct Route--------
match (tu:Stop {name: "U Schlump"})--(tu_st:Stoptime)  
 where tu_st.departure_time > "20:00:00"  
 AND tu_st.departure_time < "22:00:00"  
 with tu, tu_st  
 match (ant:Stop {name:"Sartoriusstraße"})--(ant_st:Stoptime)  
 where ant_st.arrival_time < "22:00:00"  
 AND ant_st.arrival_time > "20:00:00"  
 and ant_st.arrival_time > tu_st.departure_time  
 with ant,ant_st,tu, tu_st  
 match p = allshortestpaths((tu_st)-[*]->(ant_st))  
 with nodes(p) as n  
 unwind n as nodes  
 match (nodes)-[r]-()  
 return nodes,r 

------------------------Route With Midway Station---------------------------------
 MATCH (tu:Stop {name:"U Schlump"})--(st_tu:Stoptime),  
 (ar:Stop {name:"Sartoriusstraße"})--(st_ar:Stoptime),  
 p1=((st_tu)-[:PRECEDES*]->(st_midway_arr:Stoptime)),  
 (st_midway_arr)--(midway:Stop),  
 (midway)--(st_midway_dep:Stoptime),  
 p2=((st_midway_dep)-[:PRECEDES*]->(st_ar))  
 WHERE  
 st_tu.departure_time > "21:00:00"  
 AND st_tu.departure_time < "22:00:00"  
 AND st_midway_arr.arrival_time > st_tu.departure_time  
 AND st_midway_dep.departure_time > st_midway_arr.arrival_time  
 AND st_ar.arrival_time > st_midway_dep.departure_time  
 RETURN  
 tu,st_tu,ar,st_ar,p1,p2,midway  
 order by (st_ar.arrival_time_int-st_tu.departure_time_int) ASC  
 limit 1  
---------------------Assigning Relationship Between Stops------------------------------------

 match (s1:Stoptime)-[:PART_OF_TRIP]->(t:Trip),  
 (s2:Stoptime)-[:PART_OF_TRIP]->(t)  
 where s2.stop_sequence=s1.stop_sequence+1 
 	and not (s1)-[:PRECEDES]->(s2)  
 create (s1)-[:PRECEDES]->(s2); 

----------------------Shortest Path Between Atations-----------------------------------
MATCH (s:Stop {name:"U Schlump"})--(sTime:Stoptime),
(e:Stop {name:"Sartoriusstraße"})--(eTime:Stoptime)
where sTime.departure_time > "20:00:00" AND sTime.departure_time < "22:00:00"  and  eTime.arrival_time < "22:00:00"  
AND eTime.arrival_time > "20:00:00" 
match path = allshortestpaths((sTime)-[*]-(eTime))
return length(path), path


---------------------Weights Calculation--------------------
match (tu:Stop {name: "U Schlump"})--(tu_st:Stoptime)  
where tu_st.departure_time > "20:00:00"  
AND tu_st.departure_time < "22:00:00"  
with tu, tu_st  
match (ant:Stop {name:"Sartoriusstraße"})--(ant_st:Stoptime)  
where ant_st.arrival_time < "22:00:00"  
AND ant_st.arrival_time > "20:00:00"  
and ant_st.arrival_time > tu_st.departure_time  
with ant,ant_st,tu, tu_st  
match p = allshortestpaths((tu_st)-[r:PRECEDES*]->(ant_st))
return p, reduce(EstimatedTime=0, r in relationships(p) | EstimatedTime+(r.weight)+2) AS TotalWeight
limit 10


--------------------Specific Relationships---------------
MATCH(s:Stop), (e:Stop)    
WHERE s.name = 'U Schlump' AND e.name = 'Sartoriusstraße'    
MATCH p = allshortestpaths((s)-[*]-(e)) 
WHERE ALL (r IN relationships(p) WHERE type(r) = 'PART_OF_TRIP' OR type(r) = 'LOCATED_AT')

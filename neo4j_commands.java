---------------------------------------------
-----Find all Nodes---------
MATCH (n)
RETURN n
---------------------------------------------
-----Find all Relations---------
MATCH p=()-[r:LOCATED_AT]->() RETURN p LIMIT 100
---------------------------------------------
------Find Shortest Path between Nodes--------
match(s:Stop), (e:Stop)
where s.name starts with "U Schlump" and e.name starts with "Sartoriusstraße"
match p = shortestpath((s)-[*]-(e))
return p
-------------------------------------------------
------Find Indirect path between Nodes-----------
MATCH(s:Stop), (e:Stop)
WHERE s.name starts with "U Schlump" and e.name = "U Eppendorfer Baum"
match p = allshortestpaths((s)-[*]-(e))
where NONE (x in relationships(p) where type(x)="OPERATES")
return p
limit 25
-------------------------------------------------------
------Find specific indirect path between Nodes--------
MATCH (tu:Stop {name:"U Schlump"})--(st_tu:Stoptime),
(ar:Stop {name:"U Eppendorfer Baum"})--(st_ar:Stoptime),
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
------Find specific indirect path between Nodes--------
match(n:Route),(s:Stop),(e:Stop)
where n.short_name='U3' and s.name starts with "U Schlump" and e.name starts with "U Eppendorfer Baum"
match p = shortestpath((s)-[*]-(e))
return p
limit 25
-------------------------------------------------------
------Change datatype of a node--------
match(a:Agency)
where a.id=toInt(a.id)
with a
set a.id = toString(a.id)
return count(a)
-------------------------------------------------------
------Direct Route--------
match (tu:Stop {name: "U Schlump"})--(tu_st:Stoptime)  
 where tu_st.departure_time > "07:00:00"  
 AND tu_st.departure_time < "09:00:00"  
 with tu, tu_st  
 match (ant:Stop {name:"Sartoriusstraße"})--(ant_st:Stoptime)  
 where ant_st.arrival_time < "09:00:00"  
 AND ant_st.arrival_time > "07:00:00"  
 and ant_st.arrival_time > tu_st.departure_time  
 with ant,ant_st,tu, tu_st  
 match p = allshortestpaths((tu_st)-[*]->(ant_st))  
 with nodes(p) as n  
 unwind n as nodes  
 match (nodes)-[r]-()  
 return nodes,r 
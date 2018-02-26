MATCH(s:Stop)--(st:Stoptime),
(e:Stop)--(et:Stoptime)
WHERE s.name = 'U Schlump' AND e.name = 'Sartoriusstraße'    
and et.arrival_time > st.departure_time 
MATCH p = shortestpath((st)-[*]-(et)) 
WHERE ALL (r IN relationships(p) WHERE type(r) = 'PRECEDES' OR type(r) = 'LOCATED_AT')
RETURN p, s, e
limit 1
----------------Dijkstra-------------

MATCH  (s:Stop)--(st:Stoptime),
(e:Stop)--(et:Stoptime)
where s.name IN [ 'Schlump', 'U Schlump'] 
	and e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ]
call apoc.algo.dijkstra(st, et, 'PRECEDES', 'weight') YIELD path, weight
return s, e, path, weight

-------------Shortest Path---------------
MATCH(s:Stop)--(st:Stoptime),
(e:Stop)--(et:Stoptime)
WHERE s.name = 'U Schlump' AND e.name = 'Sartoriusstraße'    
and et.arrival_time > st.departure_time 
MATCH p = shortestpath((st)-[:PRECEDES*]-(et)) 
RETURN p,s,e,
reduce(distance=0, r in relationships(p) | distance+ toInt(r.weight)) AS totalDistance
       ORDER BY totalDistance ASC

-------------Shortest Path With Weghts---------------
MATCH(s:Stop)--(st:Stoptime),
(e:Stop)--(et:Stoptime)
WHERE s.name = 'U Schlump' AND e.name = 'Sartoriusstraße'    
and et.arrival_time > st.departure_time 
MATCH p = shortestpath((st)-[:PRECEDES*]-(et)) 
RETURN p,s,e,
reduce(distance=0, r in relationships(p) | distance+ toInt(r.weight)) AS totalDistance
       ORDER BY totalDistance ASC

------------All Shortestpath----------------
match (s:Stop)--(st:Stoptime), (e:Stop)--(et:Stoptime)    
where s.name IN [ 'Schlump', 'U Schlump'] 
	and e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ]
    and st.arrival_time < et.departure_time 
match p = allshortestpaths((st)-[r:PRECEDES*]->(et))
return p

-------------All Path------------------------
match (s:Stop)--(st:Stoptime), (e:Stop)--(et:Stoptime)    
where s.name IN [ 'Schlump', 'U Schlump'] 
	and e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ]
match p = ((st)-[r:PRECEDES*]-(et))
return p

------------All Simple Path--------------------
match (s:Stop)--(st:Stoptime), (e:Stop)--(et:Stoptime) 
where s.name IN [ 'Schlump', 'U Schlump'] 
	and e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ] 
CALL apoc.algo.allSimplePaths(st, et, 'PRECEDES', 20) yield path as path
RETURN s, st, e, et path

-----------------------In Direct Routes--------------------------------
MATCH(s:Stop)--(st:Stoptime),
(e:Stop)--(et:Stoptime)
Where s.name = 'Sartoriusstraße' 
	and e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ] 
and et.arrival_time > st.departure_time 
MATCH p = shortestpath((st)-[*]-(et))
WHERE ALL (r IN relationships(p) WHERE type(r) = 'PRECEDES' OR type(r) = 'LOCATED_AT' or type(r) = 'PART_OF_TRIP')
RETURN p,s,e,
reduce(distance=0, r in relationships(p) | distance + toInt(r.weight)) AS totalDistance
       ORDER BY totalDistance ASC

--------------------------------------------

MATCH(s:Stop)--(st:Stoptime),
(e:Stop)--(et:Stoptime)
where s.name IN [ 'Schlump', 'U Schlump'] 
	and e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ]  
and et.arrival_time > st.departure_time 
MATCH p = shortestpath((st)-[*]-(et))
WHERE ALL (r IN relationships(p) WHERE type(r) = 'PRECEDES' OR type(r) = 'LOCATED_AT' or type(r) = 'PART_OF_TRIP')
RETURN p,s,e,
reduce(distance=0, r in relationships(p) | distance + toInt(r.weight)) AS totalDistance
       ORDER BY totalDistance ASC

-----------------------Weight Calculation For Indirect Routes--------------------------------


MATCH(s:Stop)--(st:Stoptime),
(e:Stop)--(et:Stoptime)
where s.name IN [ 'Dörpsweg'] 
	and e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ]  
MATCH p = shortestpath((st)-[*]-(et))
WHERE ALL (r IN relationships(p) WHERE type(r) = 'PRECEDES' OR type(r) = 'LOCATED_AT')
RETURN p,s,e,
reduce(distance=0, r in relationships(p) | distance+ toInt(r.weight)) AS totalDistance
       ORDER BY totalDistance ASC     


match (s:Stop)--(st:Stoptime), (e:Stop)--(et:Stoptime) 
where s.name IN [ 'Dörpsweg'] 
	and e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ] 
CALL apoc.algo.allSimplePaths(st, et, 'PRECEDES|TRANSFER|PART_OF|LOCATED_AT', 100) yield path as path
RETURN path



Dörpsweg
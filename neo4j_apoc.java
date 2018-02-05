----------------------------------------
----------------------------------------
match (cs:Stop {name: "U Schlump"})--(csTime:Stoptime) 
CALL apoc.path.expandConfig(cs,{relationshipFilter:"PRECEDES>",maxLevel:3,uniqueness:"NODE_GLOBAL"}) YIELD path
WITH cs, RELATIONSHIPS(path) as r, LAST(NODES(path)) as es
RETURN cs,es,r
----------------------------------------
 
MATCH  (startNode:Stop {name:"U Schlump"})--(st:Stoptime),
(endNode:Stop{name:"Sartoriusstraße"})--(et:Stoptime)
call apoc.algo.dijkstra(st, et, 'PRECEDES', 'duration') YIELD path, weight
return path;

----------------------------------------
MATCH p=(s:Stop)<-[rel2:LOCATED_AT]-(st:Stoptime)-[rel1:PART_OF_TRIP]-(t:Trip)-[rel:USES]->(r:Route)
where r.short_name = "U3" and s.name contains 'Schlump'
return r,p
limit 100

--------------------Weight Check--------------------
MATCH  (startNode:Stop {name:"U Schlump"})--(st:Stoptime),
(endNode:Stop{name:"Sartoriusstraße"})--(et:Stoptime)
call apoc.algo.dijkstra(st, et, 'PRECEDES', 'duration') YIELD path, weight
return path, weight/60
limit 20;
--------------------------------------------------------
MATCH  (startNode:Stop)--(st:Stoptime),
(endNode:Stop)--(et:Stoptime)
where startNode.name = 'Schlump' or startNode.name = 'U Schlump' 
	and endNode.name = 'Hauptbahnhof Süd' // or endNode.name = 'HBF/Steintorwall' or endNode.name = 'Hamburg Hbf'
call apoc.algo.dijkstra(st, et, 'PRECEDES', 'weight') YIELD path, weight
return startNode, endNode, path, weight
limit 20;
-------------------------------------------------------
CALL apoc.meta.graph
-------------------------------------------------------
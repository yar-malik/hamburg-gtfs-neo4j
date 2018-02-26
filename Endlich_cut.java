MATCH(s:Stop)--(st:Stoptime),
(e:Stop)--(et:Stoptime)
where s.name IN [ 'Dörpsweg'] 
	and e.name IN ['Hauptbahnhof Süd', 'HBF/Steintorwall' , 'Hamburg Hbf', 'Hauptbahnhof Nord', 'Hamburg Hbf (Kirchenallee)', 'Hauptbahnhof/ZOB', 'HBF/Mönckebergstraße' ]  
MATCH p = shortestpath((st)-[*]-(et))
WHERE ALL (r IN relationships(p) WHERE type(r) = 'PRECEDES' OR type(r) = 'LOCATED_AT')
RETURN p,s,e,
reduce(distance=0, r in relationships(p) | distance+ toInt(r.weight)) AS totalDistance
       ORDER BY totalDistance ASC


------------------------------------------------------------------------
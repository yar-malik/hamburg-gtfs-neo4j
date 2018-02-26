package com.aamalik.gtfs.neo4j;

import com.aamalik.gtfs.neo4j.dto.TripPlan;
import com.aamalik.gtfs.neo4j.entity.*;
import com.aamalik.gtfs.neo4j.entity.projection.stoptime.TripPlanResultProjection;
import com.aamalik.gtfs.neo4j.repository.*;
import org.neo4j.graphdb.Path;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.*;

@Controller
@RequestMapping("/customrest")
public class Neo4jWebServiceController {

    @Autowired
    AgencyRepository agencyRepository;

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    StopRepository stopRepository;

    @Autowired
    StoptimeRepository stoptimeRepository;

    @Autowired
    PathRepository pathRepository;

    @Autowired
    CalendarDateRepository calendarDateRepository;

    @Autowired
    TripRepository tripRepository;

    @Autowired
    ProjectionFactory projectionFactory;


    @GetMapping(path = "/agency/{agencyId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    //Example id: HVV
    public Agency getAgency(@PathVariable String agencyId, Model model) {
        return agencyRepository.findByAgencyId(agencyId,1);
    }

    @GetMapping(path = "/agency/{agencyId}/routes", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    //Example id: HVV
    public Set<Route> getAgencyRoutes(@PathVariable String agencyId, Model model) {
        Agency agency = agencyRepository.findByAgencyId(agencyId,1);
        return agency.routes;
    }

    @GetMapping(path = "/route/{routeId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    //Example id: 13
    public Route getRoute(@PathVariable String routeId, Model model) {
        return routeRepository.findByRouteId(routeId,1);
    }

    @GetMapping(path = "/getStuffDone/{routeId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Path getStuffDone(@PathVariable String routeId, Model model) {
        Route route = routeRepository.findByRouteId(routeId,1);

        Stop s1 = stopRepository.findByName("U Schlump",2);
        Stop s2 = stopRepository.findByName("Sartoriusstraße",2);

        return null;
    }


    @GetMapping(path = "/stop/{stopName}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    //Example name: U Schlump
    public Stop getStop(@PathVariable String stopName, Model model) {
        return stopRepository.findByName(stopName,1);
    }

    @GetMapping(path = "/stoptime/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    //Example id: 1270015
    public Stoptime getStopTime(@PathVariable Long id, Model model) {
        stoptimeRepository.findOne(id,1);
        return null;
    }

    @GetMapping(path = "/trip/{tripId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    //Example id: 22
    public Trip getTrip(@PathVariable String tripId, Model model) {
        return tripRepository.findByTripId(tripId, 1);
    }


    @RequestMapping(value = "/shortestPath", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayList <ArrayList <ArrayList<Stoptime>>> shortestPath(@RequestBody TripPlan plan){

        ArrayList <ArrayList <ArrayList<Stoptime>>> allPlansWithLegs = new ArrayList<>();

        Sort sort = new Sort(Sort.Direction.ASC, "stopid");
        Pageable pageable = new PageRequest(0, 1000, sort);

        Page<Stoptime> resultlist = stoptimeRepository.shortestPath(
                        plan.getOrigStation(),
                        plan.getDestStation(),
                        pageable);

        ArrayList <ArrayList<Stoptime>> finalResult = breakupTrips( resultlist);

        for (ArrayList<Stoptime> leg : finalResult) {
            ArrayList <ArrayList<Stoptime>> planWithLegs = new ArrayList<>();
            planWithLegs.add(leg);
            allPlansWithLegs.add((planWithLegs));
        }

        return allPlansWithLegs;
    }

    @RequestMapping(value = "/specificTripOneDirection", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayList <ArrayList <ArrayList<Stoptime>>> specificTripOneDirection(@RequestBody TripPlan plan){

        ArrayList <ArrayList <ArrayList<Stoptime>>> allPlansWithLegs = new ArrayList<>();

        Sort sort = new Sort(Sort.Direction.ASC, "tripId", "stopSequence");
        Pageable pageable = new PageRequest(0, 100000, sort);

        Page<Stoptime> imResult = stoptimeRepository.specificTripOneDirection(
                plan.getOrigStation(),
                plan.getDestStation(),
                pageable);

        ArrayList <ArrayList<Stoptime>> finalResult = breakupTrips( imResult);

        for (ArrayList<Stoptime> leg : finalResult) {
            ArrayList <ArrayList<Stoptime>> planWithLegs = new ArrayList<>();
            planWithLegs.add(leg);
            allPlansWithLegs.add((planWithLegs));
        }

        System.out.println("allPlansWithLegs: " + allPlansWithLegs);

        return allPlansWithLegs;
    }

    @RequestMapping(value = "/specificTripBothDirection", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayList <ArrayList <ArrayList<Stoptime>>> specificTripBothDirection(@RequestBody TripPlan plan){

        ArrayList <ArrayList <ArrayList<Stoptime>>> allPlansWithLegs = new ArrayList<>();

        Sort sort = new Sort(Sort.Direction.ASC, "tripId", "stopSequence");
        Pageable pageable = new PageRequest(0, 100000, sort);

        Page<Stoptime> imResult = stoptimeRepository.specificTripBothDirection(pageable);

        ArrayList <ArrayList<Stoptime>> finalResult = breakupTrips( imResult);

        for (ArrayList<Stoptime> leg : finalResult) {
            ArrayList <ArrayList<Stoptime>> planWithLegs = new ArrayList<>();
            planWithLegs.add(leg);
            allPlansWithLegs.add((planWithLegs));
        }

        System.out.println("allPlansWithLegs: " + allPlansWithLegs);
        System.out.println("allPlansWithimResultLegsX: " + allPlansWithLegs.get(0).get(0));

        return allPlansWithLegs;
    }

    //------------------------------TESTING------------------------------

    @RequestMapping(value = "/findPathMitUmSteigen", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayList <ArrayList <TripPlanResultProjection>> findPathMitUmSteigen(@RequestBody TripPlan plan){

        ArrayList <ArrayList <ArrayList<Stoptime>>> allPlansWithLegs = new ArrayList<>();

//        Sort sort = new Sort(Sort.Direction.ASC, "tripId", "stopSequence");
        Pageable pageable = new PageRequest(0, 100000, null);

        Iterable<Map<String,Object>> allResults = stoptimeRepository.findPathMitUmSteigen();
        ArrayList <ArrayList<Stoptime>> result = new ArrayList<>();

        ArrayList currentSet = null;

        JSONArray allGlobalFinalResults = new JSONArray();

        ArrayList<ArrayList<TripPlanResultProjection>> myXXListTotal = new ArrayList<ArrayList<TripPlanResultProjection>>();


        for (Map<String, Object> row : allResults) {
            JSONArray allLocalFinalResults = new JSONArray();
            JSONObject localJsonObject = new JSONObject();
            List<Object> nodes = (List) row.get("nodes");
            List<Object> rels = (List) row.get("rels");

            ArrayList<TripPlanResultProjection> myXXList = new ArrayList<>();

            for (Object obj : nodes) {

                if (obj instanceof Stoptime) {

                    Stoptime stoptime = (Stoptime) obj;

                    Set<Trip> newTrips = new HashSet<>();
                    Set<Stop> newStops = new HashSet<>();

                    if(stoptime.getTrips() == null){
                        Trip t = tripRepository.findByTripId(stoptime.getTrip_id(), 1);
                        newTrips.add(t);
                        stoptime.setTrips(newTrips);
                    }

                    if(stoptime.getStops() == null){
                        Stop s = stopRepository.findByStopId(stoptime.getStop_id(), 1);
                        newStops.add(s);
                        stoptime.setStops(newStops);
                    }

                    TripPlanResultProjection tripPlan = projectionFactory.createProjection(TripPlanResultProjection.class, stoptime);
                    myXXList.add(tripPlan);
                }
            }
            myXXListTotal.add(myXXList);

        }

        return myXXListTotal;
    }

    @RequestMapping(value = "/findPathMitWeights", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JSONArray findPathMitWeights(@RequestBody TripPlan plan){

        ArrayList <ArrayList <ArrayList<Stoptime>>> allPlansWithLegs = new ArrayList<>();
        Pageable pageable = new PageRequest(0, 100000, null);

        Iterable<Map<String,Object>> allResults = stoptimeRepository.findPathMitWeights();
        ArrayList <ArrayList<Stoptime>> result = new ArrayList<>();
        ArrayList<ArrayList<TripPlanResultProjection>> myXXListTotal = new ArrayList<ArrayList<TripPlanResultProjection>>();
        JSONArray totalFinalResults = new JSONArray();

        for (Map<String, Object> row : allResults) {

            JSONArray allLocalFinalResults = new JSONArray();
            JSONObject jsonObjekt = new JSONObject();

            List<Object> nodes = (List) row.get("nodes");
            List<Object> rels = (List) row.get("rels");
            Integer totalWeight = (Integer)row.get("totalWeight");

            ArrayList<TripPlanResultProjection> myXXList = new ArrayList<>();

            for (Object obj : nodes) {

                if (obj instanceof Stoptime) {

                    JSONObject oneStopTimeObject = new JSONObject();
                    Stoptime stoptime = (Stoptime) obj;

                    Set<Trip> newTrips = new HashSet<>();
                    Set<Stop> newStops = new HashSet<>();

                    if(stoptime.getTrips() == null){
                        Trip t = tripRepository.findByTripId(stoptime.getTrip_id(), 1);
                        newTrips.add(t);
                        stoptime.setTrips(newTrips);
                    }

                    if(stoptime.getStops() == null){
                        Stop s = stopRepository.findByStopId(stoptime.getStop_id(), 1);
                        newStops.add(s);
                        stoptime.setStops(newStops);
                    }

                    oneStopTimeObject.put("trip_id", stoptime.getTrips().iterator().next().getTripId());
                    oneStopTimeObject.put("stopSequence", stoptime.getStopSequence());
                    oneStopTimeObject.put("latitude", stoptime.getStops().iterator().next().getLatitude());
                    oneStopTimeObject.put("longitude", stoptime.getStops().iterator().next().getLatitude());
                    oneStopTimeObject.put("stopName", stoptime.getStops().iterator().next().getName());
                    oneStopTimeObject.put("stop_Id", stoptime.getStops().iterator().next().getStopId());
                    oneStopTimeObject.put("headsign", stoptime.getTrips().iterator().next().getHeadsign());
                    oneStopTimeObject.put("tripShortName", stoptime.getTrips().iterator().next().getShortName());
                    allLocalFinalResults.add(oneStopTimeObject);
                }
            }

            jsonObjekt.put("trip", allLocalFinalResults);
            jsonObjekt.put("totalWeight", totalWeight);

            totalFinalResults.add(jsonObjekt);

        }

        return totalFinalResults;
    }

    @RequestMapping(value = "/apocAlgoAllSimplePaths", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayList <ArrayList <ArrayList<Stoptime>>> apocAlgoAllSimplePaths(@RequestBody TripPlan plan) {

        System.out.println("======================");

        ArrayList <ArrayList <ArrayList<Stoptime>>> allPlansWithLegs = new ArrayList<>();

        Pageable pageable = new PageRequest(0, 100000, null);

        Page<PathObject> imResult = pathRepository.apocAlgoAllSimplePaths(pageable);
        ArrayList <ArrayList<Stoptime>> result = new ArrayList<>();

        String lastTripId = null;
        ArrayList currentSet = null;

        System.out.println("imResult: " + imResult);

        for (PathObject stoptime: imResult.getContent()) {
//            System.out.println("weight.getStoptimeList: " + weight.getStoptimeList());
            System.out.println("weight.getStoptimeList: ");

//            for(Stoptime stoptime: weight.getStoptimeList()){
//                if(stoptime.getTrips() != null) {
//                    System.out.println("Entering this loop ");
//                    Trip currentTrip = stoptime.getTrips().iterator().next();
//
//                    if (lastTripId == null || !lastTripId.equals(currentTrip.getTripId())) {
//                        currentSet = new ArrayList<Stoptime>();
//                        result.add(currentSet);
//                        lastTripId = currentTrip.getTripId();
//                    }
//
//                    TripPlanResultProjection tripPlan = projectionFactory.createProjection(TripPlanResultProjection.class, stoptime);
//                    currentSet.add(tripPlan);
//                }
//            }
        }

        ArrayList <ArrayList<Stoptime>> finalResult = result;

        for (ArrayList<Stoptime> leg : finalResult) {
            ArrayList <ArrayList<Stoptime>> planWithLegs = new ArrayList<>();
            planWithLegs.add(leg);
            allPlansWithLegs.add((planWithLegs));
        }
        System.out.println("allPlansWithLegs: " + allPlansWithLegs);
//        System.out.println("allPlansWithimResultLegsX: " + allPljansWithLegs.get(0).get(0));

        return allPlansWithLegs;

    }

    private  ArrayList <ArrayList<Stoptime>> breakupTrips(Page<Stoptime> test) {

        ArrayList <ArrayList<Stoptime>> result = new ArrayList<>();

        String lastTripId = null;
        ArrayList currentSet = null;

        for (Stoptime stoptime: test.getContent()) {

            if(stoptime.getTrips() != null) {
                Trip currentTrip = stoptime.getTrips().iterator().next();

                if (lastTripId == null || !lastTripId.equals(currentTrip.getTripId())) {
                    currentSet = new ArrayList<Stoptime>();
                    result.add(currentSet);
                    lastTripId = currentTrip.getTripId();
                }

                TripPlanResultProjection tripPlan = projectionFactory.createProjection(TripPlanResultProjection.class, stoptime);
                currentSet.add(tripPlan);
            }
        }
        System.out.println("result: " + result);

        return result;
    }
}
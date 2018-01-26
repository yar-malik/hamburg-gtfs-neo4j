package com.aamalik.gtfs.neo4j;

import com.aamalik.gtfs.neo4j.dto.TripPlan;
import com.aamalik.gtfs.neo4j.entity.*;
import com.aamalik.gtfs.neo4j.repository.*;
import com.aamalik.gtfs.neo4j.entity.*;
import com.aamalik.gtfs.neo4j.entity.projection.stoptime.TripPlanResultPjcn;
import com.aamalik.gtfs.neo4j.repository.*;
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
;
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

    @RequestMapping(value = "/planTrip", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayList <ArrayList <ArrayList<Stoptime>>>  planTrip( @RequestBody TripPlan plan){

        Sort sort = new Sort(Sort.Direction.ASC, "tripId").
                and( new Sort(Sort.Direction.ASC, "departureTimeInt"));
        Pageable pageable = new PageRequest(0, 1000000, sort);

        ArrayList <ArrayList <ArrayList<Stoptime>>> tripPlanNoTransfer =  planTripNoTransfer(plan);
        if (tripPlanNoTransfer.size() > 0) {
            return tripPlanNoTransfer;
        } else {
            tripPlanNoTransfer =  planTripOneTransfer(plan);
            return tripPlanNoTransfer;
        }
    }

    @RequestMapping(value = "/planTripNoTransfer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayList <ArrayList <ArrayList<Stoptime>>> planTripNoTransfer(@RequestBody TripPlan plan){

        ArrayList <ArrayList <ArrayList<Stoptime>>> allPlansWithLegs = new ArrayList<>();

        Sort sort = new Sort(Sort.Direction.ASC, "tripId").
                and( new Sort(Sort.Direction.ASC, "departureTimeInt"));
        Pageable pageable = new PageRequest(0, 1000000, sort);

//        System.out.println(plan.getTravelDate());
//        System.out.println(plan.getOrigStation());
//        System.out.println(plan.getOrigArrivalTimeLow());
//        System.out.println(plan.getOrigArrivalTimeHigh());
//        System.out.println(plan.getDestStation());
//        System.out.println(plan.getDestArrivalTimeLow());
//        System.out.println(plan.getDestArrivalTimeHigh());

        Page<Stoptime> imResult = stoptimeRepository.getMyTrips(
                plan.getTravelDate(),
                plan.getOrigStation(),
                plan.getOrigArrivalTimeLow(),
                plan.getOrigArrivalTimeHigh(),
                plan.getDestStation(),
                plan.getDestArrivalTimeLow(),
                plan.getDestArrivalTimeHigh(),
                pageable);

        ArrayList <ArrayList<Stoptime>> finalResult = breakupTrips( imResult);

        //Single leg trip
        for (ArrayList<Stoptime> leg : finalResult) {
            ArrayList <ArrayList<Stoptime>> planWithLegs = new ArrayList<>();
            planWithLegs.add(leg);
            allPlansWithLegs.add((planWithLegs));
        }

        return allPlansWithLegs;

    }

    @RequestMapping(value = "/planTripOneTransfer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayList <ArrayList <ArrayList<Stoptime>>>  planTripOneTransfer( @RequestBody TripPlan plan){
        ArrayList<ArrayList<ArrayList<Stoptime>>> allPlansWithLegs = new ArrayList<>();

//        System.out.println(plan.getTravelDate());
//        System.out.println(plan.getOrigStation());
//        System.out.println(plan.getOrigArrivalTimeLow());
//        System.out.println(plan.getOrigArrivalTimeHigh());
//        System.out.println("DestStation: " + plan.getDestStation());
//        System.out.println(plan.getDestArrivalTimeLow());
//        System.out.println(plan.getDestArrivalTimeHigh());

        long curRec = 0L;

        while(true) {

            Sort sort = new Sort(Sort.Direction.ASC, "tripId").
                    and(new Sort(Sort.Direction.ASC, "departureTimeInt"));
            Pageable pageable = new PageRequest(0, 1000000, sort);

            List<Stoptime> imResult = stoptimeRepository.getMyTripsOneStop(
                    plan.getTravelDate(),
                    plan.getOrigStation(),
                    plan.getOrigArrivalTimeLow(),
                    plan.getOrigArrivalTimeHigh(),
                    plan.getDestStation(),
                    plan.getDestArrivalTimeLow(),
                    plan.getDestArrivalTimeHigh(),
                    curRec,
                    Stoptime.class);

            System.out.println("Result: " + imResult.toString());

            ArrayList<ArrayList<Stoptime>> allLegs = breakupTrips(imResult);

            //Multi leg single plan
            ArrayList<ArrayList<Stoptime>> planWithLegs = new ArrayList<>();
            for (ArrayList<Stoptime> leg : allLegs) {
                planWithLegs.add(leg);
            }

            if (planWithLegs != null && planWithLegs.size() > 0) {
                allPlansWithLegs.add((planWithLegs));
            } else {
                return allPlansWithLegs;
            }

            curRec++;
        }

    }

    @RequestMapping(value = "/planTripAsfandyar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayList <ArrayList <ArrayList<Stoptime>>> planTripAsfandyar(@RequestBody TripPlan plan){

        ArrayList <ArrayList <ArrayList<Stoptime>>> allPlansWithLegs = new ArrayList<>();

        Sort sort = new Sort(Sort.Direction.ASC, "stopid");
        Pageable pageable = new PageRequest(0, 1000000, sort);

        System.out.println(plan.getTravelDate());
        System.out.println(plan.getOrigStation());
        System.out.println(plan.getOrigArrivalTimeLow());
        System.out.println(plan.getOrigArrivalTimeHigh());
        System.out.println(plan.getDestStation());
        System.out.println(plan.getDestArrivalTimeLow());
        System.out.println(plan.getDestArrivalTimeHigh());

        Page<Stoptime> imResult = stoptimeRepository.planTripAsfandyar(
                plan.getTravelDate(),
                plan.getOrigStation(),
                plan.getOrigArrivalTimeLow(),
                plan.getOrigArrivalTimeHigh(),
                plan.getDestStation(),
                plan.getDestArrivalTimeLow(),
                plan.getDestArrivalTimeHigh(),
                pageable,
                Stoptime.class);


        ArrayList <ArrayList<Stoptime>> finalResult = breakupTrips( imResult);

        for (ArrayList<Stoptime> leg : finalResult) {
            ArrayList <ArrayList<Stoptime>> planWithLegs = new ArrayList<>();
            planWithLegs.add(leg);
            allPlansWithLegs.add((planWithLegs));
        }

        return allPlansWithLegs;

    }


    @RequestMapping(value = "/oneStationAsfandyar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayList <ArrayList <ArrayList<Stoptime>>> basicRoutePost(@RequestBody TripPlan plan){

        ArrayList <ArrayList <ArrayList<Stoptime>>> allPlansWithLegs = new ArrayList<>();

        Sort sort = new Sort(Sort.Direction.ASC, "stopid");
        Pageable pageable = new PageRequest(0, 10000, sort);

        Page<Stoptime> resultlist = stoptimeRepository.oneStationAsfandyar(
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

    @RequestMapping(value = "/specificRouteAsfand", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayList <ArrayList <ArrayList<Stoptime>>> specificRouteAsfand(@RequestBody TripPlan plan){

        ArrayList <ArrayList <ArrayList<Stoptime>>> allPlansWithLegs = new ArrayList<>();

        Sort sort = new Sort(Sort.Direction.ASC, "stopid");
        Pageable pageable = new PageRequest(0, 1000000, sort);

        System.out.println(plan.getTravelDate());
        System.out.println(plan.getOrigStation());
        System.out.println(plan.getOrigArrivalTimeLow());
        System.out.println(plan.getOrigArrivalTimeHigh());
        System.out.println(plan.getDestStation());
        System.out.println(plan.getDestArrivalTimeLow());
        System.out.println(plan.getDestArrivalTimeHigh());

        Page<Stoptime> imResult = stoptimeRepository.specificRouteAsfand(
                plan.getOrigStation(),
                plan.getOrigArrivalTimeLow(),
                plan.getOrigArrivalTimeHigh(),
                plan.getDestStation(),
                plan.getDestArrivalTimeLow(),
                plan.getDestArrivalTimeHigh(),
                pageable);

        ArrayList <ArrayList<Stoptime>> finalResult = breakupTrips( imResult);

        for (ArrayList<Stoptime> leg : finalResult) {
            ArrayList <ArrayList<Stoptime>> planWithLegs = new ArrayList<>();
            planWithLegs.add(leg);
            allPlansWithLegs.add((planWithLegs));
        }

        return allPlansWithLegs;
    }

    private  ArrayList <ArrayList<Stoptime>> breakupTrips(Page<Stoptime> test) {
        return breakupTrips(test.getContent());
    }

    private  ArrayList <ArrayList<Stoptime>> breakupTrips(List<Stoptime> test) {
        ArrayList <ArrayList<Stoptime>> result = new ArrayList<>();

        String lastTripId = null;
        ArrayList currentSet = null;

        for (Stoptime stoptime: test) {

//            System.out.println("Stoptime: " + stoptime);
//            System.out.println("Stoptime.getTrips: " + stoptime.getTrips());

            if(stoptime.getTrips() != null) {
                Trip currentTrip = stoptime.getTrips().iterator().next();

                if (lastTripId == null || !lastTripId.equals(currentTrip.getTripId())) {
                    currentSet = new ArrayList<Stoptime>();
                    result.add(currentSet);
                    lastTripId = currentTrip.getTripId();
                }
                TripPlanResultPjcn tripPlan = projectionFactory.createProjection(TripPlanResultPjcn.class, stoptime);

                currentSet.add(tripPlan);
            }
        }
        return result;
    }

}

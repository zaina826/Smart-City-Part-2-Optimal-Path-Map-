import java.io.Serializable;
import java.util.ArrayList;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
class UrbanTransportationApp implements Serializable {
    static final long serialVersionUID = 99L;

    public HyperloopTrainNetwork readHyperloopTrainNetwork(String filename) {
        HyperloopTrainNetwork hyperloopTrainNetwork = new HyperloopTrainNetwork();
        hyperloopTrainNetwork.readInput(filename);
        return hyperloopTrainNetwork;
    }

    /**
     * Function calculate the fastest route from the user's desired starting point to 
     * the desired destination point, taking into consideration the hyperloop train
     * network. 
     * @return List of RouteDirection instances
     */
    public List<RouteDirection> getFastestRouteDirections(HyperloopTrainNetwork network) {
        List<RouteDirection> routeDirections = new ArrayList<>();

        //First we need to initialize all possible routeDirections
        List<RouteDirection> allPoss = allPossibilities(network);

        //Now we have a graph, we will implement Dijkstra's algorithm to find the shortest path

        Map<String, Double> shortestDistances = new HashMap<>();
        Map<String, RouteDirection> prev = new HashMap<>();
        PriorityQueue<RouteDirection> pq = new PriorityQueue<>(Comparator.comparingDouble(rd -> rd.duration));

        String startStation = network.startPoint.description;
        shortestDistances.put(startStation, 0.0);
        pq.add(new RouteDirection(startStation, startStation, 0.0, false));

        while (!pq.isEmpty()) {
            RouteDirection current = pq.poll();
            String currentStation = current.endStationName;

            for (RouteDirection adj : allPoss) {
                if (adj.startStationName.equals(currentStation)) {
                    //Shortcut for relaxing routes/edges
                    String nextStation = adj.endStationName;
                    double newDist = shortestDistances.get(currentStation) + adj.duration;

                    if (!shortestDistances.containsKey(nextStation) || newDist < shortestDistances.get(nextStation)) {
                        shortestDistances.put(nextStation, newDist);
                        prev.put(nextStation, adj);
                        pq.add(new RouteDirection(currentStation, nextStation, newDist, adj.trainRide));
                    }
                }
            }
        }

        String destination = network.destinationPoint.description;
        if (!prev.containsKey(destination)) {
            return routeDirections;
        }

        for (RouteDirection step = prev.get(destination); step != null; step = prev.get(step.startStationName)) {
            routeDirections.add(0,step);
        }
        return routeDirections;
    }

    /**
     * Function to print the route directions to STDOUT
     * */
    public void printRouteDirections(List<RouteDirection> directions) {
        double totalDuration = directions.stream().mapToDouble(d -> d.duration).sum();
        int roundedDuration = (int) Math.round(totalDuration);
        System.out.printf("The fastest route takes %d minute(s).\n", roundedDuration);

        System.out.println("Directions");
        System.out.println("----------");

        int step = 1;
        for (RouteDirection direction : directions) {
            if (direction.trainRide) {
                System.out.printf("%d. Get on the train from \"%s\" to \"%s\" for %.2f minutes.\n", step++, direction.startStationName, direction.endStationName, direction.duration);
            } else {
                System.out.printf("%d. Walk from \"%s\" to \"%s\" for %.2f minutes.\n", step++, direction.startStationName, direction.endStationName, direction.duration);
            }
        }

        // TODO: Your code goes here

    }

    public List<RouteDirection> allPossibilities(HyperloopTrainNetwork network) {
        List<RouteDirection> possibleRoutes = new ArrayList<>(); // Initialize the list
        List<Station> allStations = new ArrayList<>();
        for (TrainLine line : network.lines) {
            allStations.addAll(line.trainLineStations);
        }
        allStations.add(network.startPoint);
        allStations.add(network.destinationPoint);

        //Consecutive stops back and forth
        for (TrainLine line : network.lines) {
            for (int i = 0; i < line.trainLineStations.size() - 1; i++) {
                Station station1 = line.trainLineStations.get(i);
                Station station2 = line.trainLineStations.get(i + 1);
                double distance = calculateDistance(station1.coordinates, station2.coordinates);
                possibleRoutes.add(new RouteDirection(station1.description, station2.description, calculateTravelTime(distance, network.averageTrainSpeed, true), true));
                possibleRoutes.add(new RouteDirection(station2.description, station1.description, calculateTravelTime(distance, network.averageTrainSpeed, true), true));
            }
        }

        //Walking from one line to another
            for (int i = 0; i < allStations.size(); i++) {
                for (int j = i + 1; j < allStations.size(); j++) {
                    String lineName1 = getTrainLineName(allStations.get(i).description);
                    String lineName2 = getTrainLineName(allStations.get(j).description);
                    if (!lineName1.equals(lineName2)) {
                        double distance = calculateDistance(allStations.get(i).coordinates, allStations.get(j).coordinates);
                        possibleRoutes.add(new RouteDirection(allStations.get(i).description, allStations.get(j).description, calculateTravelTime(distance, network.averageWalkingSpeed, false), false));
                        possibleRoutes.add(new RouteDirection(allStations.get(j).description, allStations.get(i).description, calculateTravelTime(distance, network.averageWalkingSpeed, false), false));

                    }
                }

            }
            // Direct walking route from start to finish
            double totalDistance = calculateDistance(network.startPoint.coordinates, network.destinationPoint.coordinates);
            possibleRoutes.add(new RouteDirection(network.startPoint.description, network.destinationPoint.description, calculateTravelTime(totalDistance, network.averageWalkingSpeed,false), false));




        return possibleRoutes;
    }
    private String getTrainLineName(String description) {
        Pattern pattern = Pattern.compile("^(.*?) Line");
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    public double calculateTravelTime(double distance, double speed, boolean train) {
        //Never divide with zero please
        if (speed == 0) return Double.MAX_VALUE;

        //Otherwise we're cool:

        return distance / speed;
    }
    public double calculateDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }


}
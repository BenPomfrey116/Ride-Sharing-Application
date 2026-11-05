import java.util.HashMap;
import java.util.Map;

public class Routes {
    //Locations in the system
    private final Location canterbury = new Location("Canterbury");
    private final Location whitstable = new Location("Whitstable");
    private final Location dover = new Location("Dover");
    private final Location brighton = new Location("Brighton");
    private final Location gatwick = new Location("Gatwick");
    private final Location heathrow = new Location("Heathrow");
    private final Location luton = new Location("Luton");

    //HashMap of locations to store Location objects by their name
    private final HashMap<String, Location> locations = new HashMap<>(Map.of(
        "Canterbury", canterbury,
        "Whitstable", whitstable,
        "Dover", dover,
        "Brighton", brighton,
        "Gatwick", gatwick,
        "Heathrow", heathrow,
        "Luton", luton
    ));

    public Routes() {
        //Add routes from canterbury
        canterbury.addRoute("Gatwick", new RouteLeg(67, 0.7, 60));
        canterbury.addRoute("Heathrow", new RouteLeg(88, 0.5, 60));
        canterbury.addRoute("Luton", new RouteLeg(105, 0.8, 60));
        canterbury.addRoute("Whitstable", new RouteLeg(7, 0.9, 40));
        canterbury.addRoute("Dover", new RouteLeg(17, 0.4, 50));
        canterbury.addRoute("Brighton", new RouteLeg(75, 0.8, 60));
        canterbury.addRoute("Canterbury", new RouteLeg(0, 1.0, 60));
        //Add routes from whitstable
        whitstable.addRoute("Gatwick", new RouteLeg(60, 0.9, 60));
        whitstable.addRoute("Heathrow", new RouteLeg(82, 0.7, 60));
        whitstable.addRoute("Luton", new RouteLeg(100, 0.8, 60));
        whitstable.addRoute("Canterbury", new RouteLeg(7, 0.9, 40));
        whitstable.addRoute("Dover", new RouteLeg(25, 0.6, 50));
        whitstable.addRoute("Brighton", new RouteLeg(70, 0.7, 60));
        whitstable.addRoute("Whitstable", new RouteLeg(0, 1.0, 60));
        //Add routes from dover
        dover.addRoute("Gatwick", new RouteLeg(75, 0.4, 60));
        dover.addRoute("Heathrow", new RouteLeg(105, 0.3, 60));
        dover.addRoute("Luton", new RouteLeg(120, 0.6, 60));
        dover.addRoute("Canterbury", new RouteLeg(17, 0.5, 50));
        dover.addRoute("Whitstable", new RouteLeg(25, 0.7, 50));
        dover.addRoute("Brighton", new RouteLeg(90, 0.4, 60));
        dover.addRoute("Dover", new RouteLeg(0, 1.0, 60));
        //Add routes from brighton
        brighton.addRoute("Gatwick", new RouteLeg(28, 1.0, 60));
        brighton.addRoute("Heathrow", new RouteLeg(68, 0.8, 60));
        brighton.addRoute("Luton", new RouteLeg(95, 0.5, 60));
        brighton.addRoute("Canterbury", new RouteLeg(75, 0.8, 60));
        brighton.addRoute("Whitstable", new RouteLeg(70, 0.7, 60));
        brighton.addRoute("Dover", new RouteLeg(90, 0.4, 60));
        brighton.addRoute("Brighton", new RouteLeg(0, 1.0, 60));
    }

    /**
     * Returns the HashMap of locations in the system
     * @return HashMap of locations in the system
     */
    public HashMap<String, Location> getLocations() {
        return locations;
    }

    /**
     * Returns the Location object for the given location name
     * @param name of the location
     * @return Location
     */
    public Location getLocation(String name) {
        return locations.get(name);
    }

    /**
     * Returns the distance between the two given locations
     * @param locationName of the first location
     * @param nextLocationName of the second location
     * @return distance between the two locations
     */
    public double getDistance(String locationName, String nextLocationName) {
        Location location = getLocation(locationName);
        //Get the RouteLeg between the location and the next location
        RouteLeg routeLeg = location.getRouteTo(nextLocationName);
        return routeLeg.getDistance();
    }

    /**
     * Returns the average speed limit between the two given locations
     * @param locationName of the first location
     * @param nextLocationName of the second location
     * @return average speed limit between the two locations
     */
    public double getAverageSpeedLimit(String locationName, String nextLocationName) {
        Location location = getLocation(locationName);
        //Get the RouteLeg between the location and the next location
        RouteLeg routeLeg = location.getRouteTo(nextLocationName);
        return routeLeg.getAverageSpeedLimit();
    }

    /**
     * Returns the traffic multiplier between the two given locations
     * @param locationName of the first location
     * @param nextLocationName of the second location
     * @return
     */
    public double getTraffic(String locationName, String nextLocationName) {
        Location location = getLocation(locationName);
        //Get the RouteLeg between the location and the next location
        RouteLeg routeLeg = location.getRouteTo(nextLocationName);
        return routeLeg.getTraffic();
    }

    /**
     * Sets the traffic multiplier between the two given locations to the new traffic multiplier
     * @param locationName of the first location
     * @param nextLocationName of the second location
     * @param newTraffic new traffic multiplier to set between the two locations
     */
    public void setTraffic(String locationName, String nextLocationName, double newTraffic) {
        Location location = getLocation(locationName);
        //Get the RouteLeg between the location and the next location
        RouteLeg routeLeg = location.getRouteTo(nextLocationName);
        routeLeg.setTraffic(newTraffic);
    }

}

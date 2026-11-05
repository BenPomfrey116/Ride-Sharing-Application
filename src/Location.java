import java.util.HashMap;

public class Location {
    private final String name;
    //HashMap stores the outgoing routes from this location
    // --> <destination, RouteLeg>
    private final HashMap<String, RouteLeg> outgoingRoutes;

    /**
     * Constructor for Location objects
     * @param name of the location
     */
    public Location(String name) {
        this.name = name;
        outgoingRoutes = new HashMap<>();
    }

    /**
     * Returns the name of the location
     * @return name of the location
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the HashMap of outgoing RouteLegs from this location
     * @return HashMap<String,RouteLeg> where String is location name
     */
    public HashMap<String, RouteLeg> getOutgoingRoutes() {
        return outgoingRoutes;
    }

    /**
     * Returns the RouteLeg from this location to the given destination
     * @param destination location name of the destination
     * @return RouteLeg from this location to the destination, or null if route does not exist
     */
    public RouteLeg getRouteTo(String destination) {
        RouteLeg leg = null;
        try {
            leg = outgoingRoutes.get(destination);
        }
        catch (NullPointerException e) {
            System.out.println("No route from " + name + " to " + destination);
        }
        return leg;
    }
    /**
    * Adds a new outgoing route from this location to the destination with the given RouteLeg
    * @param destination of the route
    * @param leg of the route
    */
    public void addRoute(String destination, RouteLeg leg) {
        outgoingRoutes.put(destination, leg);
    }


}

public class RouteLeg {
    private final double distance;
    private final double averageSpeedLimit;
    //Traffic multiplier for the leg, 1.0 means no traffic, 0.5 means traffic slows down speed by 0.5x
    private double traffic;

    /**
     * Constructor for RouteLeg objects
     * @param distance of the leg
     * @param averageSpeedLimit of the leg
     * @param traffic on the leg
     */
    public RouteLeg(double distance, double traffic, double averageSpeedLimit) {
        this.distance = distance;
        this.traffic = traffic;
        this.averageSpeedLimit = averageSpeedLimit;
    }

    /**
     * Gets the distance of the leg
     * @return distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Gets the average speed limit of the leg
     * @return average speed limit
     */
    public double getAverageSpeedLimit() {
        return averageSpeedLimit;
    }

    /**
     * Gets the traffic on the leg
     * @return traffic
     */
    public double getTraffic() {
        return traffic;
    }

    /**
     * Sets the traffic on the leg
     * @param newTraffic to be set
     */
    public void setTraffic(double newTraffic) {
        traffic = newTraffic;
    }

}

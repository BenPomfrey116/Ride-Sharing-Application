import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class TripCalculator {
    private final Routes routesData;

    public TripCalculator(Routes routesData){
        this.routesData = routesData;
    }

    /**
     * This class is the same as the first part of calculatePriceAndFilter() but doesn't do anything with UI or
     * filtering. It is just used to work out the maths for testing.
     * @param bags luggage input from the customer
     * @param vehicleType vehicle option from the customer
     * @param day the day the customer is browsing for
     * @param hours the hour the customer is looking for a booking
     * @param pickup location the customer is getting picked up from
     * @param dropoff the airport the customer wants to go to
     * @return the estimated cost of the booking
     */
    public double calculateEstimatedPrice(int bags, String vehicleType, String day, int hours, String pickup, String dropoff){
        double distance = routesData.getDistance(pickup, dropoff);

        double defaultCost = 0.0;
        double distanceCost = distance * 1.5;
        double luggageCost = bags + 2;
        double weekendCost = (day.equals("Saturday") || day.equals("Sunday")) ? 5.00 : 0;
        double vehicleCost = vehicleType.equals("Premium") ? 1.5 : 1;
        double hoursCost = ((hours >= 7 && hours <= 9) || (hours >= 16 && hours <= 18)) ? 1.25 : 1;

        return (defaultCost + distanceCost + luggageCost + weekendCost) * (vehicleCost * hoursCost);

    }

    /**
     * Calculates the cost of a booking based on:
     * Sum of
     * - the direct route duration (distance/(averageSpeedLimit)) at £30.00 per hour (not including traffic as it is uncertain)
     * - the luggage cost at £1.00 per bag
     * - weekend tax of £5.00 if the booking is on Saturday or Sunday
     * - the booking fee (£1.00 if >30 days away, £3.50 if <7 days, £5.00 if <2 days, default £2.50)
     * Multiplied by
     * - the vehicle type multiplier (x1 for Standard, x1.5 for Premium)
     * - the hours multiplier (x1.25 if between 7-9:00 or 16-18:00)
     * @param booking to calculate the cost of
     * @param service the booking is part of
     * @return cost
     */
    public HashMap<String, Double> calculateCost(Booking booking, Service service) {

        //Calculate the direct route cost using the distance and route data
        // --> Duration/distance based cost is the direct route to avoid penalising for other booking stops
        // --> Traffic is not included as it is unfair and uncertain
        String pickup = booking.getPickupLocation();
        String airport = service.getAirport();
        double directDistance = routesData.getDistance(pickup, airport);
        double directAverageSpeedLimit = routesData.getAverageSpeedLimit(pickup, airport);
        double directRouteDuration = directDistance / (directAverageSpeedLimit);
        //£30.00 per hour
        double directDurationCost = directRouteDuration * 30.0;

        //£1.00 per bag
        double luggageCost = booking.getLuggage();

        //Vehicle type multiplier from the service's vehicle data (x1 for Standard, x1.5 for Premium)
        String vehicleType = service.getVehicleType();
        double vehicleTypeMultiplier = service.getVehiclePriceMultiplier();

        //Hour multiplier for peak times (x1.25 if between 7-9:00 or 16-18:00)
        LocalDateTime bookingTime = booking.getBookingTime();
        int hour = bookingTime.getHour();
        double hoursMultiplier = 1.0;
        if((hour >= 7 && hour <= 9) || (hour >= 16 && hour <= 18)) {
            hoursMultiplier = 1.25;
        }
        //Weekend additional cost of £5
        double weekendTax = 0.0;
        int dayOfWeek = bookingTime.getDayOfWeek().getValue();
        if(dayOfWeek == 6 || dayOfWeek == 7) {
            weekendTax = 5.0;
        }

        //Booking fee based on how much in advance the booking date is
        // --> Default £2.50, £5 if less than 2 days, £3.50 if less than 7 days, £1 if more than 30 days
        LocalDateTime now = LocalDateTime.now();
        int daysUntilBooking = (int) Duration.between(now, bookingTime).toDays();
        double bookingFee = 2.5;
        if(daysUntilBooking < 2) {
            bookingFee = 5.0;
        }
        else if(daysUntilBooking < 7) {
            bookingFee = 3.5;
        }
        else if(daysUntilBooking > 30) {
            bookingFee = 1.0;
        }

        System.out.println("Direct Duration Cost: " + directDurationCost);
        System.out.println("Luggage Cost: " + luggageCost);
        System.out.println("Vehicle Type Multiplier: " + vehicleTypeMultiplier);
        System.out.println("Hours Multiplier: " + hoursMultiplier);
        System.out.println("Weekend Tax: " + weekendTax);
        System.out.println("Booking Fee: " + bookingFee);
        System.out.println("Total Cost: " + (bookingFee + directDurationCost + luggageCost + weekendTax) * vehicleTypeMultiplier * hoursMultiplier);

        double totalCost = (bookingFee + directDurationCost + luggageCost + weekendTax) * vehicleTypeMultiplier * hoursMultiplier;

        HashMap<String, Double> costBreakdown = new HashMap<>();
        costBreakdown.put("durationCost", directDurationCost);
        costBreakdown.put("luggageCost", luggageCost);
        costBreakdown.put("vehicleTypeMultiplier", vehicleTypeMultiplier);
        costBreakdown.put("hoursMultiplier", hoursMultiplier);
        costBreakdown.put("weekendTax", weekendTax);
        costBreakdown.put("bookingFee", bookingFee);
        costBreakdown.put("totalCost", totalCost);


        return costBreakdown;
    }

    /**
     * Calculates the duration of a booking
     * Traverses through the booking's service from the pickup location onwards
     * For each leg:
     * - gets the distance (miles)
     * - gets the traffic multiplier (0.1->1)
     * - gets the average speed limit (mph)
     * - calculates the leg duration (distance/(averageSpeedLimit*traffic))
     * @param newBooking to be checked
     * @return total duration of the booking in decimal hours
     */
    public double calculateDuration(Booking newBooking) {
        Service service = newBooking.getService();
        ArrayList<Booking> serviceBookings = service.getBookings();
        int bookingIndex = serviceBookings.indexOf(newBooking);
        double totalDuration = 0.0;
        //Iterate over the new booking's service starting the new booking
        for(int i = bookingIndex; i < serviceBookings.size(); i++) {
            Booking booking = serviceBookings.get(i);
            String location = booking.getPickupLocation();
            String nextLocation = service.getAirport();
            //Booking is not last pickup, so change the next location to the next pickup location instead
            if(i < serviceBookings.size()-1) {
                Booking nextBooking = serviceBookings.get(i+1);
                nextLocation = nextBooking.getPickupLocation();
            }

            //Get the RouteLeg data for this leg
            double distance = routesData.getDistance(location, nextLocation);
            double traffic = routesData.getTraffic(location, nextLocation);
            double averageSpeedLimit = routesData.getAverageSpeedLimit(location, nextLocation);

            //Calculate duration for this leg
            double duration = distance / (averageSpeedLimit * traffic);

            //If this is not the last booking in the service
            if(i < serviceBookings.size()-1) {
                //Get the next booking and its time
                Booking nextBooking = serviceBookings.get(i + 1);
                LocalDateTime nextBookingTime = nextBooking.getBookingTime();

                //If the vehicle is set to arrive before the next booking's time, add on the extra waiting time
                LocalDateTime currentPickupTime = booking.getBookingTime();
                LocalDateTime nextLocationTime = currentPickupTime.plusMinutes(Math.round(duration * 60));
                long minuteDifference = Duration.between(nextLocationTime, nextBookingTime).toMinutes();
                if(minuteDifference > 0) {
                    duration += minuteDifference / 60.0;
                }
                else {
                    //If not the last booking in the service, updates future booking times if vehicle now arrives later
                    if(nextLocationTime.isAfter(nextBookingTime)) {
                        LocalDateTime newPickup = nextBookingTime.plus(Duration.between(nextBookingTime, nextLocationTime));
                        nextBooking.setBookingTime(newPickup);
                    }
                }
            }

            totalDuration += duration;
        }
        return totalDuration;
    }

    public double estimateDuration(Booking newBooking) {
        Service service = newBooking.getService();
        ArrayList<Booking> serviceBookings = service.getBookings();
        int bookingIndex = serviceBookings.indexOf(newBooking);
        double totalDuration = 0.0;
        //Iterate over the new booking's service starting the new booking
        for(int i = bookingIndex; i < serviceBookings.size(); i++) {
            Booking booking = serviceBookings.get(i);
            String location = booking.getPickupLocation();
            String nextLocation = service.getAirport();
            //Booking is not last pickup, so change the next location to the next pickup location instead
            if(i < serviceBookings.size()-1) {
                Booking nextBooking = serviceBookings.get(i+1);
                nextLocation = nextBooking.getPickupLocation();
            }

            //Get the RouteLeg data for this leg
            double distance = routesData.getDistance(location, nextLocation);
            double traffic = routesData.getTraffic(location, nextLocation);
            double averageSpeedLimit = routesData.getAverageSpeedLimit(location, nextLocation);

            //Calculate duration for this leg
            double duration = distance / (averageSpeedLimit * traffic);

            totalDuration += duration;
        }
        return totalDuration;
    }


    /**
     * Calculate the price for cancelling a booking depending on how close
     * the cancel request is to the booking date.
     * @param booking the booking being checked for cancellation
     * @return price of the cancellation for the booking
     */
    public double calculateCancellationFee(Booking booking){
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime bookingTime = booking.getBookingTime();

        long hoursNotice = Duration.between(currentTime, bookingTime).toHours();
        double originalCost = booking.getCost();

        //If notice is 7 days or more
        if(hoursNotice >= 168){
            return originalCost * 0.1;
        }

        //If notice is between 24hrs and 7 days
        else if(hoursNotice >= 24){
            return originalCost * 0.5;
        }

        //If notice is within 24hrs
        else{
            return originalCost;
        }
    }

    /**
     * Calculates the cost for amendments by working out the difference of cost before and
     * after amending and charging the difference plus a service fee.
     * @param originalCost the price of the booking before any amendments
     * @param newCost the new price of the booking after any amendments
     * @return the cost for amending the selected booking
     */
    public double calculateAmendmentFee(double originalCost, HashMap<String, Double> newCost){
        double newTotalCost = newCost.get("totalCost");
        double costDifference = newTotalCost - originalCost;
        double amendmentServiceFee = 2.5;

        //If it costs more
        if(costDifference > 0){
            return costDifference + amendmentServiceFee;
        }
        //If it costs the same or less
        else{
            return amendmentServiceFee;
        }
    }

}

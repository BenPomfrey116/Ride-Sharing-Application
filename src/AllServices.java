import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * Class to store All Services
 */
public class AllServices {
    //HashMap stores services by their ID
    private HashMap<Long, Service> services = new HashMap<>();
    //Stores route data to compare distances
    private final Routes routesData;

    public AllServices(Routes routesData) {
        this.routesData = routesData;
    }

    /**
     * Adds a service to the HashMap of services
     * @param service to be added
     */
    public void addService(Service service) {
        System.out.println("--> Adding service: " + service);
        services.put(service.getId(), service);
    }

    /**
     * Gets a service from the HashMap of services using the service ID
     * @param id to lookup
     * @return service with given id
     */
    public Service getService(long id) {
        return services.get(id);
    }

    /**
     * Gets the entire HashMap of services
     * @return services
     */
    public HashMap<Long, Service> getServiceMap() {
        return services;
    }


    /**
     * Returns a service for the booking
     * Matching servics have:
     * - same airport
     * - same date
     * - enough available luggage
     * - a stop within 20 miles of the pickup location
     * - a stop is within 30 minutes of the booking time AND before the next stop (airport OR next booking)
     * - the stop is in the correct direction (always goes towards the airport and doesn't backtrack)
     * If no existing service matches, make a new one
     * @param newBooking to find a service for
     * @param airport the booking is for (used to find/create a service)
     * @param vehicleType the type of vehicle
     * @return matchingService for the booking
     */
    public Service findService(Booking newBooking, String airport, String vehicleType) {
        System.out.println("--------------------- Starting service comparison ------------------------");
        Optional<Service> matchingService = Optional.empty();
        for(Service service : services.values()) {
            System.out.println(">>>>>>> Service comparison id=" + service.getId() +  " ----------------------------------------------------");
            //Compares each service to see if the booking is a valid addition
            if(compareService(service, newBooking, airport, vehicleType)) {
                System.out.println("Found matching service for booking:");
                matchingService = Optional.of(service);
                break;
            }
            System.out.println("<<<<<<< Service comparison failed id=" + service.getId() +  " ---------------------------------------------");

        }
        //If no matching service found
        if(matchingService.isEmpty()) {
            System.out.println("NO MATCHING SERVICE, Making a new service for the booking");
            //Make a new service
            Service newService = new Service(airport, vehicleType);
            addService(newService);
            matchingService = Optional.of(newService);
        }
        return matchingService.get();
    }

    /**
     * Compares a service with a booking to see if they match based on airport, date, luggage, distance and time
     * @param service to compare
     * @param booking to compare with
     * @param bookingAirport to compare with the service airport
     * @param vehicleType to compare
     * @return true if the service matches the booking based on all criteria, false otherwise
     */
    public boolean compareService(Service service, Booking booking, String bookingAirport, String vehicleType) {
        if(!compareAirport(service, bookingAirport)) {return false;}
        if(!compareVehicleType(service, vehicleType)) {return false;}
        if(!compareDate(service, booking.getBookingTime())) {return false;}
        if(!compareLuggage(service, booking.getLuggage())) {return false;}
        return compareDistanceTime(service, booking, 20);
    }

    /**
     * Compares the airport of the service with the booking airport
     * @param service to compare
     * @param airport of the booking to compare with
     * @return true if the airports are the same, false otherwise
     */
    public boolean compareAirport(Service service, String airport) {
        String serviceAirport = service.getAirport();
        System.out.println("Comparing service airport: " + serviceAirport + " with booking airport: "
                + airport + "--> " + serviceAirport.equals(airport));
        return serviceAirport.equals(airport);
    }

    /**
     * Compares the vehicle type of the service and booking type
     * @param service to compare with
     * @param vehicleType of the booking to compare
     * @return true if the vehicle types are the same, false otherwise
     */
    public boolean compareVehicleType(Service service , String vehicleType) {
        String serviceVehicleType = service.getVehicleType();
        System.out.println("Comparing service vehicle type: " + serviceVehicleType + " with booking vehicle type: "
                + vehicleType + "--> " + serviceVehicleType.equals(vehicleType));
        return serviceVehicleType.equals(vehicleType);
    }

    /**
     * Compares the date of the service with the date of the booking
     * @param service to compare
     * @param bookingDateTime of the booking to compare with
     * @return true if the service and booking are on the same day, false otherwise
     */
    public boolean compareDate(Service service, LocalDateTime bookingDateTime) {
        LocalDateTime serviceDateTime = service.getArrivalDateTime();
        //Check if the service is both on the same year AND the same day of the year
        boolean sameYear = serviceDateTime.getYear() == bookingDateTime.getYear();
        boolean sameDayOfYear = serviceDateTime.getDayOfYear() == bookingDateTime.getDayOfYear();
        System.out.println("Comparing service date: " + serviceDateTime.toLocalDate() + " with booking date: "
                + bookingDateTime.toLocalDate() + "--> same year: " + sameYear + ", same day of year: " + sameDayOfYear);
        return sameYear && sameDayOfYear;
    }

    /**
     * Compares the available luggage of the service with the luggage of the booking
     * @param service to compare
     * @param luggage of the booking to compare with
     * @return true if the service has enough available luggage for the booking, false otherwise
     */
    public boolean compareLuggage(Service service, int luggage) {
        int availableLuggage = service.getAvailableLuggage();
        System.out.println("Comparing service available luggage: " + availableLuggage + " with booking luggage: "
                + luggage + "--> " + (availableLuggage >= luggage));
        return availableLuggage >= luggage;
    }

    /**
     * Compares the pickup location and booking time of a new booking with:
     * - pickup location of existing bookings
     * - booking time of existing bookings
     * @param service to compare
     * @param newBooking to compare with
     * @param maxDistance two locations can be to match the service
     * @return true if the service has existing bookings within the max distance of the new booking, false otherwise
     */
    public boolean compareDistanceTime(Service service, Booking newBooking, double maxDistance) {
        boolean withinDistanceTime = false;
        ArrayList<Booking> bookings = service.getBookings();
        String newPickup = newBooking.getPickupLocation();
        String airport = service.getAirport();
        //Checks if the new booking is both within maxDistance AND within 30 minutes of any existing bookings in the service
        for(Booking b : bookings) {
            double distance = routesData.getDistance(newPickup, b.getPickupLocation());
            //Check direction between the two bookings (the next stop must always be towards the airport)
            boolean forwardRoute = (routesData.getDistance(newPickup, airport) <= routesData.getDistance(b.getPickupLocation(), airport));
            System.out.println("routeDirection " + b.getPickupLocation() + "->" + newPickup + "->" + airport + ": " + (forwardRoute ? "forward" : "backward"));

            //Get the next stop's time (airport arrival time if last booking, otherwise the next booking time)
            LocalDateTime nextDateTime = service.getArrivalDateTime();
            int bookingIndex = bookings.indexOf(b);
            if(bookingIndex < bookings.size()-1) {
                Booking nextBooking = bookings.get(bookingIndex + 1);
                nextDateTime = nextBooking.getBookingTime();
            }

            //Compare time with the next stop given the direction of the route
            boolean compareTime = compareBookingTimes(b, nextDateTime, newBooking.getBookingTime(), forwardRoute);

            //Check the distance is close enough and the time is compatible with the route direction
            if(distance <= maxDistance && compareTime) {
                withinDistanceTime = true;
                break;
            }

        }
        System.out.println("Comparing service distance and time with new booking: distance within " + maxDistance + " miles: " + withinDistanceTime);
        return withinDistanceTime;
    }

    /**
     * Compares the time of an existing booking in a service with the time of a new booking
     * Checks if the existing booking (given its time) would be compatible with the direction
     * --> if the new booking is before the existing one, the airport must be further away
     * --> if the new booking is after the existing one, the airport must be closer
     * --> if the new time is within 30 minutes of the booking AND before the next stop
     * @param booking to compare to
     * @param nextDateTime of the next stop (airport arrival or next booking)
     * @param newBookingDateTime of the new booking
     * @param forwardRoute true if the new->existing booking direction is towards the airport
     * @return true if the new booking is within 30 minutes and in the correct direction, false otherwise
     */
    public boolean compareBookingTimes(Booking booking, LocalDateTime nextDateTime, LocalDateTime newBookingDateTime, boolean forwardRoute) {
        boolean withinTimeframe = false;
        //Make sure the booking is before the next stop
        boolean beforeNextTime = newBookingDateTime.isBefore(nextDateTime);
        if(beforeNextTime) {
            LocalDateTime bookingDateTime = booking.getBookingTime();
            //Check if the new booking is within 30 minutes of the existing booking
            long minuteDifference = Duration.between(bookingDateTime, newBookingDateTime).toMinutes();
            if(Math.abs(minuteDifference) <= 30) {
                //Check the route direction with the time difference to avoid backtracking
                if(forwardRoute && minuteDifference >= 0) {
                    withinTimeframe = true;
                }
                else if(!forwardRoute && minuteDifference <= 0) {
                    withinTimeframe = true;
                }
            }
        }
        System.out.println("Comparing service booking time: " + booking.getBookingTime() + " with new booking time: " + newBookingDateTime
                + " and next stop time: " + nextDateTime + "--> within 30 minutes: " + (Math.abs(Duration.between(booking.getBookingTime(), newBookingDateTime).toMinutes()) <= 30)
                + ", before next stop: " + beforeNextTime
                + ", correct direction: " + ((forwardRoute && Duration.between(booking.getBookingTime(), newBookingDateTime).toMinutes() >= 0) || (!forwardRoute && Duration.between(booking.getBookingTime(), newBookingDateTime).toMinutes() <= 0)));
        return withinTimeframe;
    }

    /**
     * Prints all services for debugging
     */
    public void printServices() {
        System.out.println("--------------------");
        System.out.println("All Services:");
        for (Service service : services.values()) {
            System.out.println(service);
        }
        System.out.println("--------------------");
    }
}

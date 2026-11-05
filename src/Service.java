import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent a Service for a particular airport
 */
public class Service {
    private final long id;
    private String airport;
    private LocalDateTime arrivalDateTime;
    private String vehicleType;
    private int maxLuggage;
    //Luggage limits for each vehicle type
    private final HashMap<String, Integer> luggageLimits = new HashMap<>(Map.of(
        "Standard", 10,
        "Premium", 20
    ));
    //Vehicle price multipliers
    private final HashMap<String, Double> vehiclePriceMultipliers = new HashMap<>(Map.of(
        "Standard", 1.0,
        "Premium", 1.5
    ));

    //Booking which are part of this service - should be in order of travel time
    private ArrayList<Booking> bookings = new ArrayList<>();

    /**
     * Constructor for new Service objects
     * @param airport the service is for
     * @param vehicleType the type of vehicle
     */
    public Service(String airport, String vehicleType){
        id = System.currentTimeMillis();
        this.airport = airport;
        this.vehicleType = vehicleType;
        this.maxLuggage = luggageLimits.get(vehicleType);
    }

    /**
     * Constructor for existing Service objects with specified id
     * @param id of the service
     * @param airport the service is for
     * @param vehicleType the type of vehicle
     */
    public Service(long id, String airport, String vehicleType, LocalDateTime arrivalDateTime) {
        this.id = id;
        this.airport = airport;
        this.vehicleType = vehicleType;
        this.maxLuggage = luggageLimits.get(vehicleType);
        this.arrivalDateTime = arrivalDateTime;
    }

    /**
     * Returns the unique id of the service
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the airport the service is for
     * @return airport
     */
    public String getAirport() {
        return airport;
    }

    /**
     * Sets the airport the service is for to the new airport
     * @param newAirport to be set
     */
    public void setAirport(String newAirport) {
        airport = newAirport;
    }

    /**
     * Returns the vehicle type of the service
     * @return vehicleType
     */
    public String getVehicleType() {
        return vehicleType;
    }

    /**
     * Sets the vehicle type of the service to the new vehicle type
     * - Updates the max luggage accordingly
     * @param newVehicleType to be set
     */
    public void setVehicleType(String newVehicleType) {
        vehicleType = newVehicleType;
        maxLuggage = luggageLimits.get(newVehicleType);
    }

    /**
     * Returns the maximum luggage allowed for the service based on the vehicle type
     * @return maxLuggage
     */
    public int getMaxLuggage() {
        return maxLuggage;
    }

    public int getAvailableLuggage() {
        int usedLuggage = 0;
        for(Booking booking : bookings) {
            usedLuggage += booking.getLuggage();
        }
        return maxLuggage - usedLuggage;
    }

    /**
     * Adds a booking to the service's list of bookings
     * - adds to a position in the list using time order
     * @param newBooking to be added
     */
    public void addBooking(Booking newBooking) {
        //Needs to add them in based on order of travel!
        LocalDateTime newBookingTime = newBooking.getBookingTime();
        //If the service is new (and has no bookings) just add it
        if(bookings.isEmpty()) {
            bookings.add(newBooking);
        }
        else {
            //Service is not new, so insert via bookingDateTime order
            int lowerIndex = 0;
            int upperIndex = bookings.size()-1;
            while(lowerIndex <= upperIndex) {
                //Binary search to find the correct index to insert
                int midpoint = (lowerIndex + upperIndex)/2;
                LocalDateTime midpointTime = bookings.get(midpoint).getBookingTime();
                if(newBookingTime.isBefore(midpointTime)) {
                    upperIndex = midpoint - 1;
                }
                else {
                    lowerIndex = midpoint + 1;
                }
            }
            //Insert at the correct found index
            bookings.add(lowerIndex, newBooking);
        }
    }

    /**
     * Removes a booking from the service's list of bookings
     * @param booking to be removed
     */
    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }

    /**
     * Returns the list of bookings for the service
     * @return bookings
     */
    public ArrayList<Booking> getBookings() {
        return bookings;
    }

    /**
     * Gets the price multiplier for the vehicle type
     * @return vehicle type price multiplier
     */
    public double getVehiclePriceMultiplier() {
        return vehiclePriceMultipliers.get(vehicleType);
    }


    /**
     * Returns a string representation of the arrival date and time in the format "year:month:day:hour:minute"
     * @return "year:month:day:hour:minute"
     */
    public String getDateTimeString() {
        int year = arrivalDateTime.getYear();
        int month = arrivalDateTime.getMonthValue();
        int day = arrivalDateTime.getDayOfMonth();
        int hour = arrivalDateTime.getHour();
        int minute = arrivalDateTime.getMinute();
        return year + ":" + month + ":" + day + ":" + hour + ":" + minute;
    }

    /**
     * Returns a string representation of the service in the format "id,airport,vehicleType,arrivalDateTime"
     * @return "id,airport,vehicleType,arrivalDateTime"
     */
    public String toFileString() {
        String dateTimeString = getDateTimeString();
        return id + "," + airport + "," + vehicleType + "," + dateTimeString;
    }

    /**
     * Appends the service details to the "services_data.txt" file in the format "id,airport,vehicleType,arrivalDateTime"
     */
    public void appendServiceToFile() {
        //Get a formatted string of the service details
        String serviceFileString = toFileString();
        try {
            //Write to the end of the file
            FileWriter fw = new FileWriter("services_data.txt", true);
            fw.write(serviceFileString + "\n");
            fw.close();
        }
        catch (IOException e) {
            System.out.println("Error with file loading: " + e.getMessage());
        }
    }

    /**
     * Returns the arrival date and time of the service
     * @return arrivalDateTime
     */
    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    /**
     * Sets the arrival date and time of the service to the new arrival date and time
     * @param newArrivalDateTime to be set
     */
    public void setArrivalDateTime(LocalDateTime newArrivalDateTime) {
        arrivalDateTime = newArrivalDateTime;
    }

    /**
     * Returns a formatted string with the service's fields
     * @return formatted string with service details
     */
    @Override
    public String toString() {
        return "id: " + id + ", airport: " + airport + ", vehicleType: " + vehicleType + ", maxLuggage: " + maxLuggage + ", arrivalDateTime: " + arrivalDateTime;
    }


}

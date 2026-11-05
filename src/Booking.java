import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Class to represent a booking made by a customer
 */
public class Booking {
    private final long id;
    private String pickupLocation;
    private LocalDateTime bookingDateTime;
    private int luggage;
    private double cost;
    private Customer customer;
    private Service service;
    private double duration;
    private String paymentMethodType;
    private boolean paid = false;

    /**
     * Constructor for new Booking objects
     * @param pickupLocation of the booking
     * @param airport the booking is for (used to find/create a service)
     * @param luggage amount for the booking
     * @param bookingDateTime of the booking
     * @param customer linked to the booking
     * @param paymentMethodType of the booking
     */
    public Booking(String pickupLocation, String airport, int luggage, LocalDateTime bookingDateTime, Customer customer, String paymentMethodType) {
        id = System.currentTimeMillis();
        this.pickupLocation = pickupLocation;
        this.bookingDateTime = bookingDateTime;
        this.luggage = luggage;
        this.customer = customer;
        this.paymentMethodType = paymentMethodType;
    }

    /**
     * Constructor for existing Booking objects with specified id
     * @param id of the booking
     * @param pickupLocation of the booking
     * @param luggage amount
     * @param bookingDateTime of the booking
     * @param cost of the booking
     * @param customer linked to the booking
     * @param service booking is linked to
     * @param paymentMethodType of the booking
     */
    public Booking(long id, String pickupLocation, int luggage, LocalDateTime bookingDateTime, double cost, double duration, Customer customer, Service service, String paymentMethodType) {
        this.id = id;
        this.pickupLocation = pickupLocation;
        this.bookingDateTime = bookingDateTime;
        this.cost = cost;
        this.luggage = luggage;
        this.duration = duration;
        this.customer = customer;
        this.service = service;
        this.paymentMethodType = paymentMethodType;
    }

    /**
     * Returns the unique id of the booking
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the pickup location of the booking
     * @return pickupLocation
     */
    public String getPickupLocation() {
        return pickupLocation;
    }
    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    /**
     * Returns the cost of the booking
     * @return cost
     */
    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Returns the booking date and time of the booking
     * @return bookingDateTime
     */
    public LocalDateTime getBookingTime() {
        return bookingDateTime;
    }
    public void setBookingTime(LocalDateTime bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
    }

    /**
     * Returns the amount of luggage for the booking
     * @return luggage
     */
    public int getLuggage() {
        return luggage;
    }
    public void setLuggage(int luggage) {
        this.luggage = luggage;
    }

    /**
     * Returns the customer linked to the booking
     * @return customer
     */
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Returns the service linked to the booking
     * @return service
     */
    public Service getService() {
        return service;
    }
    public void setService(Service service) {
        this.service = service;
    }

    /**
     * Returns the duration of the booking
     * @return duration
     */
    public double getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the booking
     * @param duration to be set
     */
    public void setDuration(double duration) {
        this.duration = duration;
    }

    /**
     * Returns the payment method type of the booking
     * @return paymentMethodType
     */
    public String getPaymentMethodType() {
        return paymentMethodType;
    }

    /**
     * Sets the payment method type of the booking
     * @param paymentMethodType to be set
     */
    public void setPaymentMethodType(String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    /**
     * Checks if the booking has been paid for
     * @return paid
     */
    public boolean isPaid() {
        return paid;
    }

    /**
     * Sets whether the booking has been paid for
     * @param paid to be set
     */
    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    /**
     * Returns the booking date and time as a string in the format "year:month:day:hour:minute" for writing to file
     * @return "year:month:day:hour:minute"
     */
    public String getDateTimeString() {
        int year = bookingDateTime.getYear();
        int month = bookingDateTime.getMonthValue();
        int day = bookingDateTime.getDayOfMonth();
        int hour = bookingDateTime.getHour();
        int minute = bookingDateTime.getMinute();
        return year + ":" + month + ":" + day + ":" + hour + ":" + minute;
    }

    /**
     * Returns a string representation of the booking in the format:
     * "id,pickupLocation,luggage,bookingDateTime,cost,customerId,serviceId"
     * for writing to file
     * @return "id,pickupLocation,luggage,bookingDateTime,cost,customerId,serviceId"
     */
    public String toFileString() {
        String dateTimeString = getDateTimeString();
        long customerId = customer.getId();
        long serviceId = service.getId();
        return id + "," + pickupLocation + "," + luggage + "," + dateTimeString + "," + cost + "," + duration + "," + customerId + "," + serviceId + "," + paymentMethodType;
    }

    /**
     * Appends the booking details to the "bookings_data.txt" file in the format:
     * "id,pickupLocation,luggage,bookingDateTime,cost,customerId,serviceId"
     * throws IOException if there is an error with file writing
     */
    public void appendBookingToFile() {
        //Get the booking details in a formatted file string
        String bookingFileString = toFileString();
        try {
            //Write to the end of the file
            FileWriter fw = new FileWriter("bookings_data.txt", true);
            fw.write(bookingFileString + "\n");
            fw.close();
        }
        catch (IOException e) {
            System.out.println("Error with file loading: " + e.getMessage());
        }
    }

    /**
     * Returns the booking's details for display in the ManageBookingsForm form.
     * The booking is displayed as the start and end locations.
     * @return The start and end locations as a String.
     */
    public String showBookingDetails() {
        // needs to be changed to access the service field to get the airport once it's implemented
        String airport = service.getAirport();
        String dateString = bookingDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT));
        return pickupLocation + " => " + airport + " | " + dateString;
    }


    /**
     * Checks to see if the booking time is less than 24 hours away.
     * Bookings less than 24 hours away cannot be cancelled.
     * @return True if the booking can be cancelled, False if not.
     */
    public boolean canBeCancelled() {
        //get time 24 hours from now (as per spec)
        LocalDateTime cutoff = (LocalDateTime.now()).plusDays(1);
        //return true if it is outside the 24-hour cutoff period
        return bookingDateTime.isAfter(cutoff);
    }

    /**
     * Returns a string representation of a booking
     * @return string
     */
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", pickupLocation='" + pickupLocation + '\'' +
                ", bookingDateTime=" + bookingDateTime +
                ", luggage=" + luggage +
                ", cost=" + cost +
                ", customer=" + customer.getFirstName() + " " + customer.getLastName() +
                ", service=" + service.getAirport() + " " + service.getVehicleType() +
                ", duration=" + duration +
                '}';
    }
}

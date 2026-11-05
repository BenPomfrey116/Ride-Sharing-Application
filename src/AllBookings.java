import java.util.HashMap;

/**
 * Class to store All Bookings
 */
public class AllBookings {
    //HashMap stores bookings by their ID
    private HashMap<Long, Booking> bookings = new HashMap<>();

    /**
     * Adds a booking to the HashMap of bookings
     * @param booking to be added
     */
    public void addBooking(Booking booking) {
        System.out.println(booking.getId());
        System.out.println("--> Adding booking: " + booking);
        bookings.put(booking.getId(), booking);
    }

    /**
     * Gets a booking from the HashMap of bookings using the booking ID
     * @param id to lookup
     * @return booking with given id
     */
    public Booking getBooking(Long id) {
        return bookings.get(id);
    }

    /**
     * Gets the entire HashMap of bookings
     * @return bookings
     */
    public HashMap<Long, Booking> getBookingMap() {
        return bookings;
    }

    /**
     * Prints all bookings for debugging
     */
    public void printBookings() {
        System.out.println("--------------------");
        System.out.println("All Bookings:");
        for (Booking booking : bookings.values()) {
            System.out.println(booking);
        }
        System.out.println("--------------------");
    }
}

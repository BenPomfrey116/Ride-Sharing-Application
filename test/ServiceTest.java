import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {
    private Service standardService;
    private Service premiumService;
    private Booking booking;
    private Customer customer;

    @BeforeEach
    void setup() {
        standardService = new Service("Gatwick", "Standard");
        premiumService = new Service("Gatwick", "Premium");
        customer = new Customer("Gary","Jones","gary_jones@outlook.com");
        booking = new Booking("Dover", "Gatwick", 3, LocalDateTime.of(2026,5,30,10,45), customer, "card");
    }

    @Test
    void getMaxLuggageStandard() {
        assertEquals(10, standardService.getMaxLuggage());
    }

    @Test
    void getMaxLuggagePremium() {
        assertEquals(20, premiumService.getMaxLuggage());
    }

    @Test
    void maxLuggageUpdates() {
        standardService.setVehicleType("Premium");
        assertEquals(20, standardService.getMaxLuggage());
    }

    @Test
    void getVehiclePriceMultiplierStandard() {
        assertEquals(1.0, standardService.getVehiclePriceMultiplier());
    }

    @Test
    void getVehiclePriceMultiplierPremium() {
        assertEquals(1.5, premiumService.getVehiclePriceMultiplier());
    }

    @Test
    void addBooking() {
        ArrayList<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(booking);
        standardService.addBooking(booking);
        assertEquals(expectedBookings, standardService.getBookings());
    }

    @Test
    void addMultipleBookings() {
        Booking booking2 = new Booking("Canterbury", "Gatwick", 2, LocalDateTime.of(2026,5,30,11,10), customer, "card");
        Booking booking3 = new Booking("Whitstable", "Gatwick", 1, LocalDateTime.of(2026,5,30,11,30), customer, "card");
        Booking booking4 = new Booking("Canterbury", "Gatwick", 4, LocalDateTime.of(2026,5,30,11,0), customer, "card");

        //Bookings added manually to expected list in order of time
        ArrayList<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(booking);
        expectedBookings.add(booking4);
        expectedBookings.add(booking2);
        expectedBookings.add(booking3);

        //Bookings added in random time order to service
        standardService.addBooking(booking3);
        standardService.addBooking(booking2);
        standardService.addBooking(booking4);
        standardService.addBooking(booking);

        assertEquals(expectedBookings, standardService.getBookings());

    }

    @Test
    void removeBooking() {
        ArrayList<Booking> expectedBookings = new ArrayList<>();
        standardService.addBooking(booking);
        standardService.removeBooking(booking);
        assertEquals(expectedBookings, standardService.getBookings());
    }

    @Test
    void getAvailableLuggage() {
        standardService.addBooking(booking);
        assertEquals(7, standardService.getAvailableLuggage());
    }

    @Test
    void getDateTimeString() {
        standardService.setArrivalDateTime(LocalDateTime.of(2026,5,30,12,15));
        assertEquals("2026:5:30:12:15", standardService.getDateTimeString());
    }

    @Test
    void toFileString() {
        standardService.setArrivalDateTime(LocalDateTime.of(2026,5,30,12,15));
        String expectedString = standardService.getId() + ",Gatwick,Standard,2026:5:30:12:15";
        assertEquals(expectedString, standardService.toFileString());
    }
}
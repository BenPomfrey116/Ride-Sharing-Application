import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AllServicesTest {
    AllServices allServices;
    Routes routesData;

    Service service1;
    Service service2;

    Booking service1_booking1;
    Customer customer = new Customer("Gary","Jones","garyjones@outlook.com");

    Booking newBooking1;
    Booking newBooking2;

    @BeforeEach
    void setup() {
        routesData = new Routes();
        allServices = new AllServices(routesData);

        service1 = new Service("Gatwick", "Standard");
        service2 = new Service("Heathrow", "Premium");

        service1_booking1 = new Booking("Canterbury", "Gatwick", 3, LocalDateTime.of(2026,5,30,10,45),customer, "card");
        service1.addBooking(service1_booking1);

        newBooking1 = new Booking("Whitstable", "Gatwick", 3, LocalDateTime.of(2026,5,30,11,10),customer, "card");
        newBooking2 = new Booking("Brighton", "Luton", 3, LocalDateTime.of(2026,10,30,11,10),customer, "card");

        service1.setArrivalDateTime(LocalDateTime.of(2026,5,30,12,15));

        allServices.addService(service1);

    }

    @Test
    void addService() {
        allServices.addService(service2);
        long service2Id = service2.getId();
        assertEquals(service2, allServices.getService(service2Id));
    }

    @Test
    void findServiceExisting() {
        Service foundService = allServices.findService(newBooking1, "Gatwick", "Standard");
        assertEquals(service1, foundService);
    }

    @Test
    void findServiceNew() {
        Service foundService = allServices.findService(newBooking2, "Luton", "Premium");
        ArrayList<Booking> foundServiceBookings = foundService.getBookings();
        //if a new service is created when searching, it won't have any bookings yet
        assertEquals(0, foundServiceBookings.size());
    }

    @Test
    void compareAirportSame() {
        assertTrue(allServices.compareAirport(service1, "Gatwick"));
    }

    @Test
    void compareAirportDifferent() {
        assertFalse(allServices.compareAirport(service1, "Heathrow"));
    }

    @Test
    void compareVehicleTypeSame() {
        assertTrue(allServices.compareVehicleType(service1, "Standard"));
    }

    @Test
    void compareVehicleTypeDifferent() {
        assertFalse(allServices.compareVehicleType(service1, "Premium"));
    }

    @Test
    void compareDateSame() {
        LocalDateTime newBookingTime = LocalDateTime.of(2026,5,30,12,15);
        assertTrue(allServices.compareDate(service1, newBookingTime));
    }

    @Test
    void compareDateDifferent() {
        LocalDateTime newBookingTime = LocalDateTime.of(2026,10,22,12,15);
        assertFalse(allServices.compareDate(service1, newBookingTime));
    }

    @Test
    void compareLuggageEnough() {
        assertTrue(allServices.compareLuggage(service1, 2));
    }

    @Test
    void compareLuggageTooMuch() {
        assertFalse(allServices.compareLuggage(service1, 8));
    }

    @Test
    void compareDistanceTimeValid() {
        assertTrue(allServices.compareDistanceTime(service1, newBooking1, 20));
    }

    @Test
    void compareDistanceTimeTooFar() {
        Booking farBooking = new Booking("Brighton", "Gatwick", 3, LocalDateTime.of(2026,5,30,11,10),customer, "card");
        assertFalse(allServices.compareDistanceTime(service1, farBooking, 20));
    }

    @Test
    void compareDistanceTimeTooLate() {
        Booking lateBooking = new Booking("Whitstable", "Gatwick", 3, LocalDateTime.of(2026,5,30,11,16),customer, "card");
        assertFalse(allServices.compareDistanceTime(service1, lateBooking, 20));
    }

    @Test
    void compareDistanceTimeTooEarly() {
        Booking earlyBooking = new Booking("Dover", "Gatwick", 3, LocalDateTime.of(2026,5,30,10,14),customer, "card");
        assertFalse(allServices.compareDistanceTime(service1, earlyBooking, 20));
    }

    @Test
    void compareDistanceTimeBackwardsRoute() {
        Booking futherAwayBooking = new Booking("Dover", "Gatwick", 3, LocalDateTime.of(2026,5,30,11,10),customer, "card");
        assertFalse(allServices.compareDistanceTime(service1, futherAwayBooking, 20));
    }

}
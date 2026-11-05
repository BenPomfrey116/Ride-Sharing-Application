import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class TripCalculatorTest {

    private TripCalculator calculator;
    private Routes routes;

    AllCustomers allCustomers;
    AllBookings allBookings;
    AllServices allServices;
    Customer customer;
    Booking booking1;
    Booking booking2;
    Booking booking3;

    Service service1;

    @BeforeEach
    void setUp() {
        routes = new Routes();
        calculator = new TripCalculator(routes);

        //Set up services with bookings and customers
        allCustomers = new AllCustomers();
        allBookings = new AllBookings();
        allServices = new AllServices(routes);
        customer = new Customer("John", "Smith", "john_smith@gmail.com");
        allCustomers.addCustomer(customer);
        service1 = new Service(1, "Gatwick", "Standard", LocalDateTime.of(2026, 6, 17, 13, 00));
        allServices.addService(service1);
        booking1 = new Booking(1, "Dover", 2, LocalDateTime.of(2026, 6, 17, 11, 35), 0.0, 0.416666, customer, service1, "card");
        booking2 = new Booking(2, "Canterbury", 1, LocalDateTime.of(2026, 6, 17, 12, 0), 0.0, 0.25, customer, service1, "card");
        booking3 = new Booking(3, "Whitstable", 0, LocalDateTime.of(2026, 6, 17, 12, 15), 0.0, 0.75, customer, service1, "card");
        allBookings.addBooking(booking1);
        allBookings.addBooking(booking2);
        allBookings.addBooking(booking3);
        service1.addBooking(booking1);
        service1.addBooking(booking2);
        service1.addBooking(booking3);
    }

    @Test
    void testCalculateEstimatedPriceStandard(){
        double actualPrice = calculator.calculateEstimatedPrice(2, "Standard", "Monday", 12, "Canterbury", "Gatwick");
        assertEquals(104.50, actualPrice, 0.001);
    }

    @Test
    void testCalculateEstimatedPriceWeekendAndPremium(){
        double actualPrice = calculator.calculateEstimatedPrice(0, "Premium", "Sunday", 13, "Canterbury", "Whitstable");
        assertEquals(26.25, actualPrice, 0.001);
    }

    @Test
    void testCalculateEstimatedPriceMorningPeakHours(){
        double actualPrice = calculator.calculateEstimatedPrice(1, "Standard", "Wednesday", 8, "Canterbury", "Gatwick");
        assertEquals(129.375, actualPrice, 0.001);
    }

    @Test
    void calculateDuration() {
        double expectedDuration = 1.985;
        double calculatedDuration = calculator.calculateDuration(booking1);
        double error = Math.abs(expectedDuration - calculatedDuration);
        assertTrue(error < 0.001);
    }

    @Test
    //Calculates cost for a booking with the cheapest possible parameters (off-peak, Standard vehicle etc.)
    void calculateCostBasePrice() {
        //Note: 16/8/2028 is a Wednesday
        Service cheapService = new Service(2, "Gatwick", "Standard", LocalDateTime.of(2028, 8, 16, 12, 58));
        Booking cheapBooking = new Booking(4, "Brighton", 1, LocalDateTime.of(2028, 8, 16, 12, 30), 0.0, 0.416666, customer, cheapService, "card");

        HashMap<String, Double> costBreakdown = calculator.calculateCost(cheapBooking, cheapService);

        assertTrue(Math.abs(costBreakdown.get("totalCost") - 16.0) < 0.001);
        assertTrue(Math.abs(costBreakdown.get("durationCost") - 14.0) < 0.001);
        assertTrue(Math.abs(costBreakdown.get("luggageCost") - 1.0) < 0.001);
        assertTrue(Math.abs(costBreakdown.get("bookingFee") - 1.0) < 0.001);
        assertTrue(Math.abs(costBreakdown.get("vehicleTypeMultiplier") - 1.0) < 0.001);
        assertTrue(Math.abs(costBreakdown.get("hoursMultiplier") - 1.0) < 0.001);
        assertTrue(Math.abs(costBreakdown.get("weekendTax") - 0.0) < 0.001);
    }

    @Test
    void calculateCostPeakPremiumWeekend() {
        //Note: 19/8/2028 is a Saturday
        Service expensiveService = new Service(2, "Gatwick", "Premium", LocalDateTime.of(2028, 8, 19, 8, 0));
        Booking expensiveBooking = new Booking(4, "Brighton", 1, LocalDateTime.of(2028, 8, 19, 7, 32), 0.0, 0.416666, customer, expensiveService, "card");

        HashMap<String, Double> costBreakdown = calculator.calculateCost(expensiveBooking, expensiveService);

        assertTrue(Math.abs(costBreakdown.get("totalCost") - 39.375) < 0.001);
        assertTrue(Math.abs(costBreakdown.get("weekendTax") - 5.00) < 0.001);
        assertTrue(Math.abs(costBreakdown.get("vehicleTypeMultiplier") - 1.5) < 0.001);
        assertTrue(Math.abs(costBreakdown.get("hoursMultiplier") - 1.25 ) < 0.001);
    }

    @Test
    void calculateCostCloseBookingFee() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime oneDayFromNow = now.plusDays(1);
        Service closeService = new Service(2, "Gatwick", "Standard", oneDayFromNow);
        Booking closeBooking = new Booking(5, "Brighton", 1, oneDayFromNow, 0.0, 0.416666, customer, closeService, "card");

        HashMap<String, Double> closeCostBreakdown = calculator.calculateCost(closeBooking, closeService);

        assertTrue(Math.abs(closeCostBreakdown.get("bookingFee") - 5.0) < 0.001);
    }

    @Test
    void calculateCostWithinWeekBookingFee() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime sixDaysFromNow = now.plusDays(6);
        Service midCloseService = new Service(3, "Gatwick", "Standard", sixDaysFromNow);
        Booking midCloseBooking = new Booking(6, "Brighton", 1, sixDaysFromNow, 0.0, 0.416666, customer, midCloseService, "card");

        HashMap<String, Double> midCloseCostBreakdown = calculator.calculateCost(midCloseBooking, midCloseService);

        assertTrue(Math.abs(midCloseCostBreakdown.get("bookingFee") - 3.5) < 0.001);
    }

    @Test
    void calculateCostOverWeekBookingFee() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime eightDaysFromNow = now.plusDays(8);
        Service regularService = new Service(4, "Gatwick", "Standard", eightDaysFromNow);
        Booking regularBooking = new Booking(6, "Brighton", 1, eightDaysFromNow, 0.0, 0.416666, customer, regularService, "card");

        HashMap<String, Double> regularCostBreakdown = calculator.calculateCost(regularBooking, regularService);

        assertTrue(Math.abs(regularCostBreakdown.get("bookingFee") - 2.5) < 0.001);
    }

    @Test
    void calculateCostAdvanceBookingFee() {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime oneMonthFromNow = now.plusDays(32);
        Service advanceService = new Service(5, "Gatwick", "Standard", oneMonthFromNow);
        Booking advanceBooking = new Booking(7, "Brighton", 1, oneMonthFromNow, 0.0, 0.416666, customer, advanceService, "card");

        HashMap<String, Double> advanceCostBreakdown = calculator.calculateCost(advanceBooking, advanceService);

        assertTrue(Math.abs(advanceCostBreakdown.get("bookingFee") - 1.0) < 0.001);
    }

    @Test
    void cancellationFeeOverSevenDaysNotice(){
        LocalDateTime notice = LocalDateTime.now().plusDays(10);
        Booking booking = new Booking(101, "Dover", 2, notice, 100.00, 0.5, customer, service1, "card");

        double fee = calculator.calculateCancellationFee(booking);

        assertEquals(10.00, fee, 0.001);
    }

    @Test
    void cancellationFeeBetweenOneAndSevenDaysNotice(){
        LocalDateTime notice = LocalDateTime.now().plusDays(4);
        Booking booking = new Booking(102, "Dover", 2, notice, 50.00, 0.5, customer, service1, "card");

        double fee = calculator.calculateCancellationFee(booking);

        assertEquals(25.00, fee, 0.001);
    }

    @Test
    void cancellationFeeUnderTwenyFourHoursNotice(){
        LocalDateTime notice = LocalDateTime.now().plusHours(3);
        Booking booking = new Booking(103, "Dover", 2, notice, 80.00, 0.5, customer, service1, "card");

        double fee = calculator.calculateCancellationFee(booking);

        assertEquals(80.00, fee, 0.001);
    }

    @Test
    void amendmentFeeWhenNewTripIsMoreExpensive(){
        double farePaid = 40.00;

        HashMap<String, Double> map = new HashMap<>();
        map.put("totalCost", 65.00);

        double extraFee = calculator.calculateAmendmentFee(farePaid, map);

        assertEquals(27.50, extraFee, 0.001);
    }

    @Test
    void amendmentFeeWhenNewTripIsCheaperOrSamePrice(){
        double farePaid = 60.00;

        HashMap<String, Double> map = new HashMap<>();
        map.put("totalCost", 45.00);

        double extraFee = calculator.calculateAmendmentFee(farePaid, map);

        assertEquals(2.50, extraFee, 0.001);
    }
}
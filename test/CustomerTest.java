import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
class CustomerTest {
    private Customer customer;
    private Booking booking;

    private Customer otherCustomer;


    @BeforeEach
    void setUp() {
        customer = new Customer("John", "Smith", "john.smith@gmail.com");
        booking = new Booking("Canterbury", "Gatwick", 2, LocalDateTime.now(), customer, "card");

        otherCustomer = new Customer("Jane", "Doe", "jane.dow@gmail.com");

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addBooking() {
        ArrayList<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(booking);
        customer.addBooking(booking);
        assertEquals(expectedBookings, customer.getBookings());
    }

    @Test
    void removeBooking() {
        ArrayList<Booking> expectedBookings = new ArrayList<>();
        customer.addBooking(booking);
        customer.removeBooking(booking);
        assertEquals(expectedBookings, customer.getBookings());
    }

    @Test
    void setCreditCardData() {
        CreditCardData creditCardData = new CreditCardData(customer, "John Smith", "1234567890123456", "12/26", "123");
        customer.setCreditCardData(creditCardData);
        assertTrue(customer.hasCreditCardData());
    }

    @Test
    void setBankTransferData() {
        BankTransferData bankTransferData = new BankTransferData(customer, "John Smith", "12345678", "12-34-56");
        customer.setBankTransferData(bankTransferData);
        assertTrue(customer.hasBankTransferData());
    }

    @Test
    void chargeWithCard() {
        CreditCardData creditCardData = new CreditCardData(customer, "John Smith", "1234567890123456", "12/26", "123");
        customer.setCreditCardData(creditCardData);
        boolean paymentSuccessful = customer.chargeForBooking(50.0, creditCardData);
        assertTrue(paymentSuccessful);
    }

    @Test
    void chargeWithCardWithoutCardDetails() {
        CreditCardData otherCreditCardData = new CreditCardData(otherCustomer, "Jane Doe", "9876543210987654", "11/25", "321");
        boolean paymentSuccessful = customer.chargeForBooking(50.0, otherCreditCardData);
        assertFalse(paymentSuccessful);
    }

    @Test
    void chargeWithBankTransfer() {
        BankTransferData bankTransferData = new BankTransferData(customer, "John Smith", "12345678", "12-34-56");
        customer.setBankTransferData(bankTransferData);
        boolean paymentSuccessful = customer.chargeForBooking(50.0, bankTransferData);
        assertTrue(paymentSuccessful);
    }

    @Test
    void chargeWithBankTransferWithoutBankDetails() {
        BankTransferData otherBankTransferData = new BankTransferData(otherCustomer, "Jane Doe", "87654321", "65-43-21");
        boolean paymentSuccessful = customer.chargeForBooking(50.0, otherBankTransferData);
        assertFalse(paymentSuccessful);
    }

    @Test
    void toFileString() {
        String expectedString = customer.getId() + "," + customer.getFirstName() + "," + customer.getLastName() + "," + customer.getEmail();
        String fileString = customer.toFileString();
        assertEquals(expectedString, fileString);
    }



}
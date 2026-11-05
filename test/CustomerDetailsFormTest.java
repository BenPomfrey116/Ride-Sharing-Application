import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDetailsFormTest {
    Routes routesData;
    AllCustomers allCustomers;
    AllBookings allBookings;
    AllServices allServices;
    TripCalculator tripCalculator;
    CustomerDetailsForm form;

    Customer existingCustomer;

    @BeforeEach
    void setUp() {
        routesData = new Routes();
        allCustomers = new AllCustomers();
        allBookings = new AllBookings();
        allServices = new AllServices(routesData);
        tripCalculator = new TripCalculator(routesData);
        form = new CustomerDetailsForm(routesData, allCustomers, allBookings, allServices, tripCalculator);

        existingCustomer = new Customer("John", "Smith", "john_smith@gmail.com");
        allCustomers.addCustomer(existingCustomer);
    }



    @Test
    void getCustomerExistingValid() {
        Optional<Customer> maybeCustomer = form.getCustomer("Returning", "John", "Smith", "john_smith@gmail.com", allCustomers);
        assertEquals(existingCustomer, maybeCustomer.get());
    }

    @Test
    void getCustomerExistingInvalid() {
        Optional<Customer> maybeCustomer = form.getCustomer("Returning", "Gary", "Jones", "gary_jones@outlook.com", allCustomers);
        assertTrue(maybeCustomer.isEmpty());
    }

    @Test
    void getCustomerNewValid() {
        Optional<Customer> maybeCustomer = form.getCustomer("New", "Gary", "Jones", "gary_jones@outlook.com", allCustomers);
        assertEquals("Gary", maybeCustomer.get().getFirstName());
        assertEquals("Jones", maybeCustomer.get().getLastName());
        assertEquals("gary_jones@outlook.com", maybeCustomer.get().getEmail());
    }

    @Test
    void getCustomerNewInvalid() {
        Optional<Customer> maybeCustomer = form.getCustomer("New", "John", "Smith", "john_smith@gmail.com", allCustomers);
        assertTrue(maybeCustomer.isEmpty());
    }

}
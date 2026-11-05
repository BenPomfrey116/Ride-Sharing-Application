import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AllCustomersTest {
    private AllCustomers allCustomers;

    private Customer customer;

    @BeforeEach
    void setUp() {
        allCustomers = new AllCustomers();
        customer = new Customer("Gary", "Jones", "gary.jones@hotmail.com");
    }

    @Test
    void addCustomer() {
        allCustomers.addCustomer(customer);
        long customerId = customer.getId();
        assertEquals(customer, allCustomers.getCustomer(customerId));
    }

    @Test
    void getCustomerByEmailExisting() {
        allCustomers.addCustomer(customer);
        Optional<Customer> maybeCustomer = allCustomers.getCustomerByEmail("gary.jones@hotmail.com");
        assertTrue(maybeCustomer.isPresent());
    }

    @Test
    void getCustomerByEmailNonExisting() {
        Optional<Customer> maybeCustomer = allCustomers.getCustomerByEmail("john.smith@outlook.com");
        assertFalse(maybeCustomer.isPresent());
    }

}
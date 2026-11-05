import java.util.HashMap;
import java.util.Optional;

/**
 * Class to store All Customers
 */
public class AllCustomers {
    //HashMap stores customers by their ID
    private HashMap<Long, Customer> customers = new HashMap<>();

    /**
     * Adds a customer to the HashMap of customers
     * @param customer to be added
     */
    public void addCustomer(Customer customer) {
        System.out.println("--> Adding customer: " + customer);
        customers.put(customer.getId(), customer);
    }

    /**
     * Gets a customer from the HashMap of customers using the customer ID
     * @param id to lookup
     * @return customer with given id
     */
    public Customer getCustomer(Long id) {
        return customers.get(id);
    }

    /**
     * Gets the entire HashMap of customers
     * @return customers
     */
    public HashMap<Long, Customer> getCustomerMap() {
        return customers;
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        Optional<Customer> maybeCustomer = Optional.empty();
        for(Customer customer : customers.values()) {
            if(customer.getEmail().equalsIgnoreCase(email)) {
                maybeCustomer = Optional.of(customer);
                break;
            }
        }
        return maybeCustomer;
    }

    /**
     * Prints all customers for debugging
     */
    public void printCustomers() {
        System.out.println("--------------------");
        System.out.println("All Customers:");
        for (Customer customer : customers.values()) {
            System.out.println(customer);
        }
        System.out.println("--------------------");
    }
}

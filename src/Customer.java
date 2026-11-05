import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Customer class represents a customer with a unique id, first name, last name, and email.
 * @author bmp23
 */

public class Customer {
    private final long id;
    private String firstName;
    private String lastName;
    private String email;
    private ArrayList<Booking> bookings = new ArrayList<>();
    private Optional<BankTransferData> bankTransferData = Optional.empty();
    private Optional<CreditCardData> creditCardData = Optional.empty();

    /**
     * Constructor for Customer objects
     * Assigns an automatically incrementing id
     * @param firstName of the customer
     * @param lastName of the customer
     * @param email of the customer
     */
    public Customer(String firstName, String lastName, String email) {
        id = System.currentTimeMillis();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Constructor for existing Customer objects with specified id
     * @param id of the customer
     * @param firstName of the customer
     * @param lastName of the customer
     * @param email of the customer
     */
    public Customer(long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Returns the unique id of the customer
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the first name of the customer
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String newFirstName) {
        firstName = newFirstName;
    }

    /**
     * Returns the last name of the customer
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String newLastName) {
        lastName = newLastName;
    }

    /**
     * Returns the email of the customer
     * @return email
     */
    public String getEmail() {
        return email;
    }
    public void setEmail(String newEmail) {
        email = newEmail;
    }

    /**
     * Adds a new booking to the customers list of bookings
     * @param newBooking made by the customer
     */
    public void addBooking(Booking newBooking) {
        bookings.add(newBooking);
    }
    /**
     * Removes a booking from the customers list of bookings
     * @param booking to remove
     */
    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }
    /**
     * Returns the list of bookings made by the customer
     * @return bookings list
     */
    public ArrayList<Booking> getBookings() {
        return bookings;
    }

    /**
     * Returns the bank transfer data of the customer
     * Can be Optional.empty() if the customers hasn't entered payment details yet
     * @return bankTransferData
     */
    public Optional<BankTransferData> getBankTransferData() {
        return bankTransferData;
    }

    /**
     * Sets the bank transfer data of the customer
     * @param bankTransferData to be set
     */
    public void setBankTransferData(BankTransferData bankTransferData) {
        this.bankTransferData = Optional.of(bankTransferData);
    }

    /**
     * Checks if the customer has bank transfer data
     * @return true if bank transfer data is present, false otherwise
     */
    public boolean hasBankTransferData() {
        return bankTransferData.isPresent();
    }

    /**
     * Returns the credit card data of the customer
     * Can be Optional.empty() if the customers hasn't entered payment details yet
     * @return creditCardData
     */
    public Optional<CreditCardData> getCreditCardData() {
        return creditCardData;
    }

    /**
     * Sets the credit card data of the customer
     * @param creditCardData to be set
     */
    public void setCreditCardData(CreditCardData creditCardData) {
        this.creditCardData = Optional.of(creditCardData);
    }

    /**
     * Checks if the customer has credit card data
     * @return true if credit card data is present, false otherwise
     */
    public boolean hasCreditCardData() {
        return creditCardData.isPresent();
    }

    /**
     * Charges the customer for a booking using the specified payment method data, if they have it.
     * NOTE: This method would contain the actual payment charging when in a real application
     * @param amount to charge the customer for the booking
     * @param paymentMethodData to charge the customer with, either bank transfer data or credit card data
     * @return true if the payment was successful, false otherwise
     */
    public boolean chargeForBooking(double amount, PaymentMethodData paymentMethodData) {
        boolean paymentSuccessful = true;
        if(paymentMethodData instanceof BankTransferData && hasBankTransferData()) {
            System.out.println("Charging " + amount + " to customer " + id + " using bank transfer data.");
        }
        else if(paymentMethodData instanceof CreditCardData && hasCreditCardData()) {
            //Charge the customer using their credit card data
            System.out.println("Charging " + amount + " to customer " + id + " using credit card data.");
        }
        else {
            System.out.println("Customer " + id + " does not have credit card data to charge.");
            paymentSuccessful = false;
        }
        return paymentSuccessful;
    }

    /**
     * Returns a string representation of the customer's details in the format:
     * "id,firstName,lastName,email" for writing to file
     * @return "id,firstName,lastName,email"
     */
    public String toFileString() {
        return id + "," + firstName + "," + lastName + "," + email;
    }

    /**
     * Appends the customer's details to the customers_data.txt file in the format:
     * id,firstName,lastName,email
     */
    public void appendCustomerToFile() {
        //Get a formatted string with the customer details
        String customerFileString = toFileString();
        try {
            //Write to the end of the file
            FileWriter fw = new FileWriter("customers_data.txt", true);
            fw.write(customerFileString + "\n");
            fw.close();
        }
        catch (IOException e) {
            System.out.println("Error with file loading: " + e.getMessage());
        }
    }

    /**
     * Returns a string representation of the customer's details in the format:
     * @return "id: id, firstName: firstName, lastName: lastName, email: email"
     */
    @Override
    public String toString() {
        return "id: " + id + ", firstName: " + firstName + ", lastName: " + lastName + ", email: " + email;
    }


}

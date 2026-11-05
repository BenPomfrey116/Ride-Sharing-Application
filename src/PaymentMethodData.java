import java.io.FileWriter;
import java.io.IOException;

/**
 * Class to store common payment method data
 */
public abstract class PaymentMethodData {
    private long id;
    private Customer customer;

    /**
     * Constructor for new PaymentMethodData objects
     * @param customer to link to
     */
    public PaymentMethodData(Customer customer) {
        id = System.currentTimeMillis();
        this.customer = customer;
    }

    /**
     * Constructor for existing PaymentMethodData objects with specified id
     * @param id of the payment method
     * @param customer to link to
     */
    public PaymentMethodData(long id, Customer customer) {
        this.id = id;
        this.customer = customer;
    }

    /**
     * Returns the unique id of the payment method data
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the customer linked to the payment method data
     * @return customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer linked to the payment method data
     * @param customer to link
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Abstract method to get a formatted string for appending to the file
     * Subclass implementations return strings in the formats:
     * CreditCardData: id,customerId,"card",nameOnCard,cardNumber,expiryDate,cvv
     * BankTransferData: id,customerId,"bank",accountName,accountNumber,sortCode
     * @return formatted string
     */
    public abstract String toFileString();

    /**
     * Appends the payment details data to the payment_details_data.txt file
     * with the relevant string format for the payment type
     */
    public void appendPaymentDetailsToFile() {
        //Get a formatted string with the payment details
        String paymentDetailsFileString = toFileString();
        try {
            //Write to the end of the file
            FileWriter fw = new FileWriter("payment_details_data.txt", true);
            fw.write(paymentDetailsFileString + "\n");
            fw.close();
        }
        catch (IOException e) {
            System.out.println("Error with file loading: " + e.getMessage());
        }
    }

    public abstract String formatDetails();

    @Override
    public abstract String toString();


}

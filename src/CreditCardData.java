import java.io.FileWriter;
import java.io.IOException;

/**
 * Class to store credit card data for a payment method of a customer
 */
public class CreditCardData extends PaymentMethodData {
    private String nameOnCard;
    private String cardNumber;
    private String expiryDate;
    private String cvv;

    /**
     * Constructor for CreditCardData object
     * @param customer to link to
     * @param nameOnCard as it appears
     * @param cardNumber of the card
     * @param expiryDate of the card (yy/mm)
     * @param cvv of the card
     */
    public CreditCardData(Customer customer, String nameOnCard, String cardNumber, String expiryDate, String cvv) {
        super(customer);
        this.nameOnCard = nameOnCard;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    /**
     * Constructor for existing CreditCardData object with specified id
     * @param id of the credit card data
     * @param customer to link to
     * @param nameOnCard as it appears
     * @param cardNumber of the card
     * @param expiryDate of the card (yy/mm)
     * @param cvv of the card
     */
    public CreditCardData(long id, Customer customer, String nameOnCard, String cardNumber, String expiryDate, String cvv) {
        super(id, customer);
        this.nameOnCard = nameOnCard;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    /**
     * Returns the name on the card
     * @return nameOnCard
     */
    public String getNameOnCard() {
        return nameOnCard;
    }

    /**
     * Returns the card number
     * @return cardNumber
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Returns the expiry date of the card
     * @return expiryDate
     */
    public String getExpiryDate() {
        return expiryDate;
    }

    /**
     * Returns the cvv of the card
     * @return cvv
     */
    public String getCvv() {
        return cvv;
    }

    /**
     * Returns a string representation of the credit card data in the format:
     * id,customerId,"card",nameOnCard,cardNumber,expiryDate,cvv
     * @return formatted string
     */
    @Override
    public String toFileString() {
        return getId() + "," + getCustomer().getId() + ",card," + nameOnCard + "," + cardNumber + "," + expiryDate + "," + cvv;
    }

    @Override
    public String formatDetails() {
        return "Name on card: " + nameOnCard
                + "\nCard number:  " + cardNumber
                + "\nExpiry date:  " + expiryDate
                + "\nCVV:          " + cvv;
    }

    /**
     * Returns a string representation of the credit card data
     * @return formatted string
     */
    @Override
    public String toString() {
        return "CreditCardData{" +
                "id=" + getId() +
                ", customer=" + getCustomer().getId() + "|" + getCustomer().getFirstName() +
                ", nameOnCard='" + nameOnCard + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", cvv='" + cvv + '\'' +
                '}';
    }

}

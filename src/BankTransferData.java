
/**
 * Class to store bank transfer data for a payment method of a customer
 */
public class BankTransferData extends PaymentMethodData {
    private String accountName;
    private String accountNumber;
    private String sortCode;

    /**
     * Constructor for BankTransferData object
     * @param customer to link to
     * @param accountName as it appears
     * @param accountNumber of the account
     * @param sortCode of the account
     */
    public BankTransferData(Customer customer, String accountName, String accountNumber, String sortCode) {
        super(customer);
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
    }

    /**
     * Constructor for existing BankTransferData object with specified id
     * @param id of the bank transfer data
     * @param customer to link to
     * @param accountName as it appears
     * @param accountNumber of the account
     * @param sortCode of the account
     */
    public BankTransferData(long id, Customer customer, String accountName, String accountNumber, String sortCode) {
        super(id, customer);
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
    }

    /**
     * Returns the name on the account
     * @return accountName
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Returns the account number
     * @return accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Returns the sort code
     * @return sortCode
     */
    public String getSortCode() {
        return sortCode;
    }

    /**
     * Returns a string representation of the bank transfer data in the format:
     * id,customerId,"bank",accountName,accountNumber,sortCode
     * @return formatted string
     */
    @Override
    public String toFileString() {
        return getId() + "," + getCustomer().getId() + ",bank," + accountName + "," + accountNumber + "," + sortCode;
    }

    @Override
    public String formatDetails() {
        return "Account Name:  " + accountName
                + "\nAccount Number: " + accountNumber
                + "\nSort Code:      " + sortCode;
    }

    /**
     * Returns a string representation of the bank transfer data
     * @return formatted string
     */
    @Override
    public String toString() {
        return "BankTransferData{" +
                "id=" + getId() +
                ", customer=" + getCustomer().getId() + "|" + getCustomer().getFirstName() +
                ", accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", sortCode='" + sortCode + '\'' +
                '}';
    }


}

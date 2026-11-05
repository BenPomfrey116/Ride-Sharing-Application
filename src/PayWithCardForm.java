import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class PayWithCardForm extends JFrame {
    //Form components
    private JPanel cardPanel;
    private JButton closeButton;
    private JTextField nameOnCardInput;
    private JLabel nameOnCardLabel;
    private JLabel cardNumberLabel;
    private JTextField cardNumberInput;
    private JButton continueButton;
    private JLabel expiryDateLabel;
    private JTextField monthInput;
    private JTextField yearInput;
    private JLabel cvvLabel;
    private JTextField cvvInput;
    private JLabel invaildNameMsg;
    private JLabel invalidCardNumberMsg;
    private JLabel invalidExpiryDateMsg;
    private JLabel invalidCVVMsg;
    private JPanel datePanel;
    private JLabel dateSlash;
    private JLabel enterDetailsLabel;
    private JTextField card;

    //The booking form to pass the CreditCardData back to
    private MakeBookingForm bookingForm;

    //Constructor which takes in the booking form and the customer
    // --> uses the customer to create a CreditCardData object and pass it back to the booking form
    public PayWithCardForm(MakeBookingForm bookingForm, Customer customer) {
        //link to the .form xml file layout
        setContentPane(cardPanel);
        setTitle("Enter card details");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //Center the window on the screen
        setLocationRelativeTo(null);
        setVisible(true);

        this.bookingForm = bookingForm;

        //Action listener for close button to simply close the form and re-enable the booking form
        closeButton.addActionListener(e -> {
            bookingForm.setFormEnabled(true);
            dispose();
        });

        //Action listener for continue button to create a CreditCardData object and pass it back to the booking form
        continueButton.addActionListener(e -> {

            //Validate inputs
            if(validateInputs()) {
                //If valid, set payment details
                String nameOnCard = nameOnCardInput.getText();
                String cardNumber = cardNumberInput.getText();
                String month = monthInput.getText();
                String year = yearInput.getText();
                String expiryDate = formatExpiryDate(month, year);
                String cvv = cvvInput.getText();

                CreditCardData creditCardData = new CreditCardData(customer, nameOnCard, cardNumber, expiryDate, cvv);
                customer.setCreditCardData(creditCardData);
                creditCardData.appendPaymentDetailsToFile();

                bookingForm.setPaymentData(creditCardData);

                dispose();
            }
        });

    }

    /**
     * Returns a formatted string for the expiry date
     * @param month to be formatted
     * @param year to be formatted
     * @return "mm/yy"
     */
    public String formatExpiryDate(String month, String year) {
        return month + "/" + year;
    }

    /**
     * Validates all inputs in the form using InputValidator (which shows error messages for invalid inputs)
     * @return true if all inputs are valid, false otherwise
     */
    public boolean validateInputs() {
        String nameOnCard = nameOnCardInput.getText();
        boolean validName = InputValidator.validateName(invaildNameMsg, nameOnCard);

        String cardNumber = cardNumberInput.getText();
        boolean validCardNumber = InputValidator.validateCardNumber(invalidCardNumberMsg, cardNumber);

        String month = monthInput.getText();
        String year = yearInput.getText();
        boolean validExpiryDate = InputValidator.validateExpiryDate(invalidExpiryDateMsg, month, year);

        String cvv = cvvInput.getText();
        boolean validCVV = InputValidator.validateCVV(invalidCVVMsg, cvv);

        return validName && validCardNumber && validExpiryDate && validCVV;
    }

}

import javax.swing.*;

public class PayWithBankForm extends JFrame {
    //Form components
    private JButton closeButton;
    private JLabel enterDetailsLabel;
    private JLabel accountNameLabel;
    private JTextField accountNameInput;
    private JTextField accountNumberInput;
    private JLabel accountNumberLabel;
    private JLabel sortCodeLabel;
    private JPanel sortCodePanel;
    private JTextField sortCodeInput_B;
    private JTextField sortCodeInput_A;
    private JLabel sortCodeDash1;
    private JLabel sortCodeDash2;
    private JTextField sortCodeInput_C;
    private JButton continueButton;
    private JPanel bankPanel;
    private JLabel invaildNameMsg;
    private JLabel invalidAccountNumberMsg;
    private JLabel invalidSortCodeMsg;

    //The booking form to pass the BankTransferData back to
    private MakeBookingForm bookingForm;

    //Constructor which takes in the booking form and the customer
    // --> uses the customer to create a BankTransferData object and pass it back to the booking form
    public PayWithBankForm(MakeBookingForm bookingForm, Customer customer) {
        //link to the .form xml file layout
        setContentPane(bankPanel);
        setTitle("Enter Bank details");
        setSize(600, 400);
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

        //Action listener for continue button to create a BankTransferData object and pass it back to the booking form
        continueButton.addActionListener(e -> {

            //Validate inputs
            if(validateInputs()) {
                //If valid, set payment details
                String accountName = accountNameInput.getText();
                String accountNumber = accountNumberInput.getText();
                String sortCodePartA = sortCodeInput_A.getText();
                String sortCodePartB = sortCodeInput_B.getText();
                String sortCodePartC = sortCodeInput_C.getText();
                String sortCode = formatSortCode(sortCodePartA, sortCodePartB, sortCodePartC);

                BankTransferData bankTransferData = new BankTransferData(customer, accountName, accountNumber, sortCode);
                customer.setBankTransferData(bankTransferData);
                bankTransferData.appendPaymentDetailsToFile();

                bookingForm.setPaymentData(bankTransferData);

                dispose();

            }

        });


    }

    /**
     * Returns a formatted string for the sort code in the format:
     * "partA-partB-partC"
     * @param partA of the sort code
     * @param partB of the sort code
     * @param partC of the sort code
     * @return "partA-partB-partC"
     */
    public String formatSortCode(String partA, String partB, String partC) {
        return partA + "-" + partB + "-" + partC;
    }

    /**
     * Validates all inputs in the form using InputValidator (which shows error messages for invalid inputs)
     * @return true if all inputs are valid, false otherwise
     */
    public boolean validateInputs() {
        String accountName = accountNameInput.getText();
        boolean validName = InputValidator.validateName(invaildNameMsg, accountName);

        String accountNumber = accountNumberInput.getText();
        boolean validAccountNumber = InputValidator.validateBankAccountNumber(invalidAccountNumberMsg, accountNumber);

        String sortCodePartA = sortCodeInput_A.getText();
        String sortCodePartB = sortCodeInput_B.getText();
        String sortCodePartC = sortCodeInput_C.getText();
        boolean validSortCode = InputValidator.validateSortCode(invalidSortCodeMsg, sortCodePartA, sortCodePartB, sortCodePartC);

        return validName && validAccountNumber && validSortCode;
    }

}

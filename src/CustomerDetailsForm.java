import javax.swing.*;
import java.util.Optional;

public class CustomerDetailsForm extends JFrame{
    //Form components
    private JPanel customerDetailsPanel;
    private JTextField firstNameTextField;
    private JTextField lastNameTextField;
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel emailLabel;
    private JTextField emailTextField;
    private JButton backButton;
    private JButton continueButton;
    private JLabel invalidFirstNameMsg;
    private JLabel invalidEmailMsg;
    private JLabel invalidLastNameMsg;
    private JComboBox newCustomerDropdown;
    private JLabel newCustomerLabel;
    private JLabel invalidCustomerTypeMsg;

    //Routes data for distances and duration calc
    private final Routes routesData;

    //private final InputValidator inputValidator = new InputValidator();

    public CustomerDetailsForm(Routes routesData, AllCustomers allCustomers, AllBookings allBookings, AllServices allServices, TripCalculator tripCalculator) {
        //Set up JFrame
        setContentPane(customerDetailsPanel);
        setTitle("Your details");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //Set to center of the screen
        setLocationRelativeTo(null);
        setVisible(true);

        this.routesData = routesData;

        //Action listener for back button to return to the main menu
        backButton.addActionListener(event -> {
            dispose();
            new MainForm(routesData, allCustomers, allServices, allBookings, tripCalculator);
        });

        //Action listener for continue button
        continueButton.addActionListener(event -> {
            //Only allow the user to continue if their inputs are all valid
            // --> displays error messages otherwise
            System.out.println("Customer inputs valid? (" + validateInputs() + ")");
            if(validateInputs()) {
                //Get the details from the inputs
                String firstName = firstNameTextField.getText();
                String lastName = lastNameTextField.getText();
                String email = emailTextField.getText();
                String customerType = (String) newCustomerDropdown.getSelectedItem();
                /*
                * Check if the customer is new or existing based on their input and their email
                * --> maybeCustomer is empty if they either claim to be new but have an existing email,
                *     or claim to be existing but don't have an existing email
                * --> otherwise, maybeCustomer contains the existing customer, or the new customer that has just been made
                * */
                Optional<Customer> maybeCustomer = getCustomer(customerType, firstName, lastName, email, allCustomers);
                if(maybeCustomer.isPresent()) {
                    Customer customer = maybeCustomer.get();
                    //Dispose of the form and make new booking form, passing the new customer
                    dispose();
                    //Passes the customer to the booking form to link bookings and customers when made
                    new MakeBookingForm(customer, routesData, allCustomers, allBookings, allServices, tripCalculator);
                }


            }

        });

    }

    /**
     * Validates all inputs in the form using InputValidator (which shows error messages for invalid inputs)
     * @return true if all inputs are valid, false otherwise
     */
    public boolean validateInputs() {
        boolean firstNameValid = InputValidator.validateName(invalidFirstNameMsg, firstNameTextField.getText());
        boolean lastNameValid = InputValidator.validateName(invalidLastNameMsg, lastNameTextField.getText());
        boolean emailValid = InputValidator.validateEmail(invalidEmailMsg, emailTextField.getText());
        boolean customerTypeValid = InputValidator.validateComboBox(invalidCustomerTypeMsg, newCustomerDropdown.getSelectedItem().toString(), "new or returning");
        return firstNameValid && lastNameValid && emailValid && customerTypeValid;
    }

    /**
     * Checks if the customer is new or existing based on their input and their email
     * --> maybeCustomer is empty if they either claim to be new but have an existing email,
     *     or claim to be existing but don't have an existing email
     * --> otherwise, maybeCustomer contains the existing customer, or the new customer that has just been made
     * @param type (new or returning) the customer claims to be
     * @param firstName of the customer
     * @param lastName of the customer
     * @param email of the customer
     * @param allCustomers to check through
     * @return an Optional containing a Customer if their type matches the existing emails in the system
     */
    public Optional<Customer> getCustomer(String type, String firstName, String lastName,  String email, AllCustomers allCustomers) {
        Optional<Customer> maybeCustomer = allCustomers.getCustomerByEmail(email);
        if(type.equals("New")) {
            //Customer claims they're new, but email is already in the system, so show error message
            if(maybeCustomer.isPresent()) {
                invalidCustomerTypeMsg.setText("A customer with this email already exists");
                invalidCustomerTypeMsg.setVisible(true);
                maybeCustomer = Optional.empty();
            }
            //Customer claims they're new, and email is not in the system, so make new customer
            else {
                Customer newCustomer = new Customer(firstName, lastName, email);
                allCustomers.addCustomer(newCustomer);
                //Append the new customer to the file
                newCustomer.appendCustomerToFile();
                maybeCustomer = Optional.of(newCustomer);
            }
        }
        else {
            //Customer claims they're existing, but email is not in the system, so show error message
            if(maybeCustomer.isEmpty()) {
                invalidCustomerTypeMsg.setText("No customer with this email found");
                invalidCustomerTypeMsg.setVisible(true);
            }
        }
        return maybeCustomer;
    }




}

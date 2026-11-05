import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MakeBookingForm extends JFrame{
    //Form components
    private JPanel makeBookingPanel;
    private JComboBox airportComboBox;
    private JLabel header;
    private JLabel airportLabel;
    private JLabel pickupLocationLabel;
    private JLabel dateLabel;
    private JTextField monthTextField;
    private JTextField yearTextField;
    private JTextField dayTextField;
    private JLabel dateSlash1Txt;
    private JPanel datePanel;
    private JLabel timeLabel;
    private JTextField minutesTextField;
    private JTextField hoursTextField;
    private JLabel timeColonTxt;
    private JLabel dateSlashTxt2;
    private JPanel timePanel;
    private JLabel luggageLabel;
    private JTextField noBagsTextField;
    private JButton backButton;
    private JButton payButton;
    private JLabel invalidAirportMsg;
    private JLabel invalidPickupMsg;
    private JLabel invalidDateMsg;
    private JLabel invalidTimeMsg;
    private JLabel invalidBagsMsg;
    private JComboBox pickupComboBox;
    private JLabel durationLabel;
    private JTextPane durationText;
    private JButton createBookingButton;
    private JLabel vehicleTypeLabel;
    private JComboBox vehicleTypeComboBox;
    private JLabel invalidVehicleTypeMsg;
    private JButton paymentDetailsButton;
    private JTextPane paymentDetailsText;
    private JLabel paymentLabel;
    private JLabel priceLabel;
    private JTextPane priceText;
    private JLabel paymentTypeLabel;
    private JLabel invalidPaymentTypeMsg;
    private JComboBox paymentTypeComboBox;
    private JLabel invalidPaymentDetailsMsg;

    //Routes data
    private final Routes routesData;

    //Trip calculator
    private final TripCalculator tripCalculator;

    //Payment details from the payment forms
    private Optional<PaymentMethodData> paymentData = Optional.empty();
    //HashMap to link the payment form input to the string used to store payment type in each booking
    private final HashMap<String, String> paymentInputToType = new HashMap<>(Map.of(
        "Credit card", "card",
        "Bank transfer", "bank"
    ));
    //Card details
    private PayWithCardForm cardForm;
    private String nameOnCard;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    //Bank transfer details
    private PayWithBankForm bankForm;
    private String accountName;
    private String accountNumber;
    private String sortCode;
    //Cost
    private double cost;

    //Created booking
    private Booking createdBooking;

    //Constructor takes in the customer who is making this booking, plus objects to access existing data
    public MakeBookingForm(Customer customer, Routes routesData, AllCustomers allCustomers, AllBookings allBookings, AllServices allServices, TripCalculator tripCalculator) {
        //link to the .form xml file layout
        setContentPane(makeBookingPanel);
        setTitle("New Booking Details");
        setSize(700, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //Center the window on the screen
        setLocationRelativeTo(null);
        setVisible(true);

        this.routesData = routesData;
        this.tripCalculator = tripCalculator;

        //Action listener for the back button to return to the customer details form without creating a booking
        backButton.addActionListener(event -> {
            dispose();
            new CustomerDetailsForm(routesData, allCustomers, allBookings, allServices, tripCalculator);
        });

        //Action listener for the payment details button to check if relevent payment details exist, or show payment forms if not.
        paymentDetailsButton.addActionListener(event -> {
            String paymentType = (String) paymentTypeComboBox.getSelectedItem();
            boolean validPaymentType = InputValidator.validateComboBox(invalidPaymentTypeMsg, paymentType, "a payment type");
            //If a payment type is selected
            if(validPaymentType){
                System.out.println(">>> Payment details button clicked with payment type: " + paymentType);
                //If the payment type is card
                if(paymentType.equals("Credit card")) {
                    //Check if the customer has existing card details
                    if(customer.hasCreditCardData()) {
                        System.out.println("Customer has existing card details, showing details...");
                        paymentData = Optional.of(customer.getCreditCardData().get());
                        showPaymentDetails();
                    }
                    //No existing card details, show card form
                    else {
                        setFormEnabled(false);
                        cardForm = new PayWithCardForm(this, customer);
                    }
                }
                else if(paymentType.equals("Bank transfer")) {
                    //Check if the customer has existing bank details
                    if(customer.hasBankTransferData()) {
                        System.out.println("Customer has existing card details, showing details...");
                        paymentData = Optional.of(customer.getBankTransferData().get());
                        showPaymentDetails();
                    }
                    //No existing bank details, show bank transfer form
                    else {
                        setFormEnabled(false);
                        bankForm = new PayWithBankForm(this, customer);
                    }
                }
            }

        });

        //Action listener for the create booking button
        createBookingButton.addActionListener(event -> {
            //All inputs must be valid to create a booking and allow the user to continue
            System.out.println("Booking form inputs valid? (" + validateInputs() + ")");
            if(validateInputs()) {
                setFormEnabled(false);
                //Get all the details from the form
                String airport = (String) airportComboBox.getSelectedItem();
                String pickup = (String) pickupComboBox.getSelectedItem();
                int day = Integer.parseInt(dayTextField.getText());
                int month = Integer.parseInt(monthTextField.getText());
                int year = Integer.parseInt(yearTextField.getText());
                int hours = Integer.parseInt(hoursTextField.getText());
                int minutes = Integer.parseInt(minutesTextField.getText());
                //Make LocalDateTime from the date and time details
                LocalDateTime bookingDateTime = LocalDateTime.of(year, month, day, hours, minutes);
                int bags = Integer.parseInt(noBagsTextField.getText());
                String vehicleType = (String) vehicleTypeComboBox.getSelectedItem();

                String paymentMethodInput = (String) paymentTypeComboBox.getSelectedItem();
                String paymentMethodType = paymentInputToType.get(paymentMethodInput);

                //Make new booking
                Booking newBooking = new Booking(pickup, airport, bags, bookingDateTime, customer, paymentMethodType);
                this.createdBooking = newBooking;

                //Finds a service with matching details (or creates one if no existing services match)
                Service matchingService = allServices.findService(newBooking, airport, vehicleType);
                matchingService.addBooking(newBooking);
                newBooking.setService(matchingService);
                allBookings.addBooking(newBooking);

                //Calculate the booking duration and set text in the form
                double duration = tripCalculator.calculateDuration(newBooking);
                newBooking.setDuration(duration);
                durationText.setText(formatDuration(duration));

                //Set the service's arrival date and time
                long durationMinutes = Math.round(duration * 60);
                LocalDateTime newArrivalDateTime = bookingDateTime.plusMinutes(durationMinutes);
                System.out.println("New arrival time for service: " + newArrivalDateTime);

                //Calculate the booking cost and set text in the form
                HashMap<String, Double> costBreakdown = tripCalculator.calculateCost(newBooking, matchingService);
                double cost = costBreakdown.get("totalCost");
                newBooking.setCost(cost);
                this.cost = cost;
                priceText.setText(formatCost(costBreakdown, vehicleType));

                //If the service is newly created (no arrival time yet), set arrival time and append to file
                if(matchingService.getArrivalDateTime() == null) {
                    matchingService.setArrivalDateTime(newArrivalDateTime);
                    matchingService.appendServiceToFile();
                }
                else {
                    //The service is not new (added new booking to existing service)
                    //If the new booking makes the vehicle arrive at a different time, update the service arrival time and all booking durations
                    if(!newArrivalDateTime.isEqual(matchingService.getArrivalDateTime())) {
                        System.out.println("Updating service arrival time and booking durations...");
                        matchingService.setArrivalDateTime(newArrivalDateTime);
                        //Update earlier booking durations
                        updateEarlierBookings(matchingService.getBookings(), newBooking);
                        try {
                            //Update the bookings in the file with new durations
                            updateBookingsFile(matchingService.getId(), allBookings);
                        }
                        catch(IOException e) {
                            System.out.println("Error loading bookings file: " + e.getMessage());
                        }
                        try {
                            //Update the service in the file with the new arrival time
                            updateServicesFile(matchingService, allServices);
                        }
                        catch(IOException e) {
                            System.out.println("Error loading services file: " + e.getMessage());
                        }
                    }
                }

                //Booking is now fully created and all details are set, so append to file
                newBooking.appendBookingToFile();

                //debug print statements to show the new booking details and where it lies in its service
                System.out.println("--- New booking created ---");
                System.out.println("FOR: " + customer.getFirstName() + " " + customer.getLastName()
                        + " at " + bookingDateTime.toString()
                        + " from " + pickup + " to " + airport
                        + " with " + bags + " bags.");
                System.out.println("Matching service: " + matchingService);
                //Print out bookings of the service in order
                System.out.println("Bookings in this service:");
                for(Booking booking : matchingService.getBookings()) {
                    System.out.println("--> " + booking);
                }
                System.out.println("----------------------");

                //Disable form buttons after showing duration --> forces user to click "Done" and return to main form
                backButton.setEnabled(false);
                createBookingButton.setEnabled(false);
                payButton.setEnabled(true);
            }


        });

        //Action listener for the pay button to return to the main form after making a booking and charge the customer
        payButton.addActionListener(event -> {

            //Charge the customer using the payment details and cost
            boolean paid = customer.chargeForBooking(cost, paymentData.get());
            createdBooking.setPaid(paid);

            //If payment is successful, return to the main form
            if(paid) {
                dispose();
                new MainForm(routesData, allCustomers, allServices, allBookings, tripCalculator);
            }
        });
    }

    /**
     * Sets the payment details given from the payment forms
     * @param paymentData to be set and shown in the form
     */
    public void setPaymentData(PaymentMethodData paymentData) {
        this.paymentData = Optional.of(paymentData);
        showPaymentDetails();
        setFormEnabled(true);
    }

    /**
     * Shows the payment details in the form and disables the payment inputs/buttons
     */
    public void showPaymentDetails() {
        paymentDetailsButton.setEnabled(false);
        paymentTypeComboBox.setEnabled(false);
        invalidPaymentDetailsMsg.setVisible(false);
        System.out.println(">>> Showing payment details: " + paymentData.get());
        paymentDetailsText.setText(paymentData.get().formatDetails());
    }

    /**
     * Validates all inputs in the form using InputValidator (which shows error messages for invalid inputs)
     * @return true if all inputs are valid, false otherwise
     */
    public boolean validateInputs() {
        String airportSelected = (String) airportComboBox.getSelectedItem();
        boolean validAirport = InputValidator.validateComboBox(invalidAirportMsg, airportSelected, "an airport");

        String pickupSelected = (String) pickupComboBox.getSelectedItem();
        boolean validPickup = InputValidator.validateComboBox(invalidPickupMsg, pickupSelected, "a pickup location");

        String vehicleTypeSelected = (String) vehicleTypeComboBox.getSelectedItem();
        boolean validVehicleType = InputValidator.validateComboBox(invalidVehicleTypeMsg, vehicleTypeSelected, "a vehicle type");

        String dayInput = dayTextField.getText();
        String monthInput = monthTextField.getText();
        String yearInput = yearTextField.getText();
        String hoursInput = hoursTextField.getText();
        String minutesInput = minutesTextField.getText();
        boolean validDateTime = InputValidator.validateDateTime(invalidDateMsg, invalidTimeMsg, dayInput, monthInput, yearInput, hoursInput, minutesInput);

        String bagAmount = noBagsTextField.getText();
        boolean validBags = InputValidator.validateBags(invalidBagsMsg, bagAmount, vehicleTypeSelected);

        String paymentTypeSelected = (String) paymentTypeComboBox.getSelectedItem();
        boolean validPaymentType = InputValidator.validateComboBox(invalidPaymentTypeMsg, paymentTypeSelected, "a payment type");

        validatePaymentDetailsGiven();

        return validAirport && validPickup && validVehicleType && validDateTime && validBags && validPaymentType && paymentData.isPresent();
    }

    /**
     * Checks if payment details have been given
     */
    public void validatePaymentDetailsGiven() {
        invalidPaymentDetailsMsg.setVisible(false);
        if(paymentData.isEmpty()) {
            invalidPaymentDetailsMsg.setText("Please enter payment details");
            invalidPaymentDetailsMsg.setVisible(true);
        }
    }

    /**
     * Formats decimal hours into a string
     * @param duration (decimal hours)
     * @return formatted string: "_ hours, _ minutes"
     */
    public String formatDuration(double duration) {
        int durationHours = (int) duration;
        int durationMinutes = (int) ((duration - durationHours) * 60);
        return durationHours + " hours, " + durationMinutes + " minutes";
    }

    /**
     * Formats the cost into a string for displaying in the form
     * Format:
     *      Cost breakdown:
     *      Booking fee:       + £_.__
     *      Duration:          + £_.__
     *      Luggage:           + £_.__
     *      Weekend booking:   + £_.__ (if weekend booking)
     *      [type] Vehicle:    x  _.__
     *      Peak booking time: x  _.__ (if peak time booking)
     *      -----------------------------
     *      Total cost:          £_.__
     * @param costBreakdown hashmap containing the cost factor and their values
     * @param vehicleType to show in the breakdown
     * @return formatted string
     */
    public String formatCost(HashMap<String, Double> costBreakdown, String vehicleType) {
        double cost = costBreakdown.get("totalCost");
        double directDurationCost = costBreakdown.get("durationCost");
        double luggageCost = costBreakdown.get("luggageCost");
        double weekendTax = costBreakdown.get("weekendTax");
        double bookingFee = costBreakdown.get("bookingFee");
        double vehicleTypeMultiplier = costBreakdown.get("vehicleTypeMultiplier");
        double hoursMultiplier = costBreakdown.get("hoursMultiplier");

        String breakdownString = "Cost breakdown:\n";
        breakdownString += String.format("Booking fee:            + £%.2f\n", bookingFee);
        breakdownString += String.format("Duration:               + £%.2f\n", directDurationCost);
        breakdownString += String.format("Luggage:                + £%.2f\n", luggageCost);
        if(weekendTax > 0) {
            breakdownString += String.format("Weekend booking:        + £%.2f\n", weekendTax);
        }
        if(vehicleTypeMultiplier > 1) {
            breakdownString += String.format("%s vehicle:          x%.2f\n", vehicleType, vehicleTypeMultiplier);
        }
        if(hoursMultiplier > 1) {
            breakdownString += String.format("Peak booking time:        x%.2f\n", hoursMultiplier);
        }
        breakdownString += String.format("--------------------------------\n" + "Total cost:               £%.2f", cost);

        return breakdownString;
    }



    /**
     * Updates the duration of earlier bookings in a service (new bookings may make the vehicle arrive later)
     * @param bookings in the service to be updated
     * @param newBooking update until
     */
    public void updateEarlierBookings(ArrayList<Booking> bookings, Booking newBooking) {
        int bookingIndex = bookings.indexOf(newBooking);
        //Iterate over the service bookings which appear before the new booking and update their durations
        for(int i = 0; i < bookingIndex; i++) {
            Booking booking = bookings.get(i);
            System.out.println(">>> Updating earlier booking: " + booking);
            booking.setDuration(tripCalculator.calculateDuration(booking));
        }
    }

    /**
     * Updates the bookings file with new durations for all bookings in a service.
     * @param serviceId of the service
     * @param allBookings to access each booking
     * @throws IOException if there is an issue loading the file
     */
    public void updateBookingsFile(long serviceId, AllBookings allBookings) throws IOException {
        //Load the file into an ArrayList of lines
        Path filePath = Paths.get("bookings_data.txt");
        ArrayList<String> lines = new ArrayList<>(Files.readAllLines(filePath));
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] bookingValues = line.split(",");
            long bookingServiceId = Long.parseLong(bookingValues[7]);
            //If the booking from the file is for the service
            if(bookingServiceId == serviceId) {
                //Get the booking and update the line with the new duration
                long bookingId = Long.parseLong(bookingValues[0]);
                Booking booking = allBookings.getBooking(bookingId);
                System.out.println("NEW FILE STRING: " + booking.toFileString());
                String newFileString = booking.toFileString();
                lines.set(i, newFileString);
            }
        }
        //Write the bookings back to the file with the updated durations
        Files.write(filePath, lines);
    }

    /**
     * Updates the services file with a new arrival time for a service.
     * @param service to be updated
     * @param allServices to access the service's details
     * @throws IOException if there is an issue loading the file
     */
    public void updateServicesFile(Service service, AllServices allServices) throws IOException {
        //Load the file into an ArrayList of lines
        Path filePath = Paths.get("services_data.txt");
        ArrayList<String> lines = new ArrayList<>(Files.readAllLines(filePath));
        long serviceId = service.getId();
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            System.out.println("Checking service line: " + line);
            String[] serviceValues = line.split(",");
            long currentServiceId = Long.parseLong(serviceValues[0]);
            //If the service from the file is the service that needs updating
            if(currentServiceId == serviceId) {
                //Remake the file string and update the line
                String newFileString = service.toFileString();
                lines.set(i, newFileString);
            }
        }
        //Write the services back to the file with the updated arrival time
        Files.write(filePath, lines);
    }

    /**
     * Enables or disables the form inputs and buttons
     * Payment details stay disabled if they are already given
     * @param enabled to set the form
     */
    public void setFormEnabled(boolean enabled) {
        //Set input fields
        airportComboBox.setEnabled(enabled);
        pickupComboBox.setEnabled(enabled);
        monthTextField.setEnabled(enabled);
        yearTextField.setEnabled(enabled);
        dayTextField.setEnabled(enabled);
        hoursTextField.setEnabled(enabled);
        minutesTextField.setEnabled(enabled);
        noBagsTextField.setEnabled(enabled);
        vehicleTypeComboBox.setEnabled(enabled);
        //Set buttons
        backButton.setEnabled(enabled);
        createBookingButton.setEnabled(enabled);
        //Set payment details
        //if payment details given, don't allow changing the payment type or details
        if(paymentData.isEmpty()) {
            paymentTypeComboBox.setEnabled(enabled);
            paymentDetailsButton.setEnabled(enabled);
        }

    }


}

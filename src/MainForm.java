import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MainForm extends JFrame{
    //Form components
    private JPanel main;
    private JButton btnManageBookings;
    private JButton btnMakeBooking;
    private JButton btnBrowseBookings;

    public MainForm(Routes routesData, AllCustomers allCustomers, AllServices allServices, AllBookings allBookings, TripCalculator tripCalculator) {
        setContentPane(main); //links the .form xml file to this class
        setTitle("COMP5009 A2");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Centers form on screen
        setLocationRelativeTo(null);
        setVisible(true);

        //Action listener to open the EnterEmailForm to go on to manage bookings
        btnManageBookings.addActionListener(e -> {
            new EnterEmailForm(allBookings, allServices, allCustomers, tripCalculator);
        });

        //Action listener to open the CustomerDetailsForm before going on to make a booking
        btnMakeBooking.addActionListener(e -> {
            dispose();
            new CustomerDetailsForm(routesData, allCustomers, allBookings, allServices, tripCalculator);
        });

        //Action listener to open the ServiceBrowserForm to browse existing services
        btnBrowseBookings.addActionListener(e -> {
            new ServiceBrowserForm(allServices, routesData, tripCalculator);
        });
    }

    /**
     * Reads payment method data from a text file and adds it to the AllPaymentMethods object.
     * @param allPaymentMethods to add each payment method to
     * @param allCustomers to link each payment method to the relevant customer
     * @throws IOException if file is not found
     */
    public static void readPaymentDetailsFromFile(AllPaymentMethods allPaymentMethods, AllCustomers allCustomers) throws IOException {
        //Read from the file path
        Path filePath = Paths.get("payment_details_data.txt");
        if(!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        List<String> paymentDetailsData = Files.readAllLines(filePath);
        for(String paymentDetails : paymentDetailsData) {
            //Get values from each file line
            String[] paymentValues = paymentDetails.split(",");
            long paymentMethodId = Long.parseLong(paymentValues[0]);
            long customerId = Long.parseLong(paymentValues[1]);
            String paymentMethodType = paymentValues[2];
            Customer customer = allCustomers.getCustomer(customerId);
            if(paymentMethodType.equals("bank")) {
                String accountName = paymentValues[3];
                String accountNumber = paymentValues[4];
                String sortCode = paymentValues[5];
                BankTransferData bankTransferData = new BankTransferData(paymentMethodId, customer, accountName, accountNumber, sortCode);
                allPaymentMethods.addPaymentMethod(bankTransferData);
                customer.setBankTransferData(bankTransferData);
            }
            else if(paymentMethodType.equals("card")) {
                String nameOnCard = paymentValues[3];
                String cardNumber = paymentValues[4];
                String expiryDate = paymentValues[5];
                String cvv = paymentValues[6];
                CreditCardData creditCardData = new CreditCardData(paymentMethodId, customer, nameOnCard, cardNumber, expiryDate, cvv);
                allPaymentMethods.addPaymentMethod(creditCardData);
                customer.setCreditCardData(creditCardData);
            }
        }
    }

    /**
     * Reads customer data from a text file and adds it to the AllCustomers object.
     * @param allCustomers to add each customer to
     * @throws IOException if file is not found
     */
    public static void readCustomersFromFile(AllCustomers allCustomers) throws IOException {
        //Read from the file path
        Path filePath = Paths.get("customers_data.txt");
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        List<String> customersData = Files.readAllLines(filePath);
        for(String customerDetails : customersData) {
            //Get values from each file line
            String[] customerValues = customerDetails.split(",");
            long customerId = Long.parseLong(customerValues[0]);
            String firstName = customerValues[1];
            String lastName = customerValues[2];
            String email = customerValues[3];
            //Make new customer
            Customer customer = new Customer(customerId, firstName, lastName, email);
            allCustomers.addCustomer(customer);
        }
    }

    /**
     * Reads service data from a text file and adds it to the AllServices object.
     * @param allServices to add each service to
     * @throws IOException if file is not found
     */
    public static void readServicesFromFile(AllServices allServices) throws IOException {
        //Read from the file path
        Path filePath = Paths.get("services_data.txt");
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        List<String> servicesData = Files.readAllLines(filePath);
        for(String serviceDetails : servicesData) {
            //Get values from each file line
            String[] serviceValues = serviceDetails.split(",");
            long serviceId = Long.parseLong(serviceValues[0]);
            String airport = serviceValues[1];
            String vehicleType = serviceValues[2];
            String[] dateTimeValues = serviceValues[3].split(":");
            LocalDateTime arrivalDateTime = LocalDateTime.of(
                    Integer.parseInt(dateTimeValues[0]),
                    Integer.parseInt(dateTimeValues[1]),
                    Integer.parseInt(dateTimeValues[2]),
                    Integer.parseInt(dateTimeValues[3]),
                    Integer.parseInt(dateTimeValues[4])
            );
            //Make new service with the details
            Service service = new Service(serviceId, airport, vehicleType, arrivalDateTime);
            allServices.addService(service);
        }
    }

    /**
     * Reads booking data from a text file and adds it to the AllBookings object.
     * Links each booking to the relevant Service and Customer objects.
     * @param allBookings to add each booking to
     * @param allServices to link each booking to the relevant service
     * @param allCustomers to link each booking to the relevant customer
     * @throws IOException if file is not found
     */
    public static void readBookingsFromFile(AllBookings allBookings, AllServices allServices, AllCustomers allCustomers) throws IOException {
        //Read from the file path
        Path filePath = Paths.get("bookings_data.txt");
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        List<String> bookingsData = Files.readAllLines(filePath);
        for(String bookingDetails : bookingsData) {
            //Get values from each file line
            String[] bookingValues = bookingDetails.split(",");
            long bookingId = Long.parseLong(bookingValues[0]);
            String pickup = bookingValues[1];
            int luggage = Integer.parseInt(bookingValues[2]);
            //Get date and time values from file ("year:month:day:hour:minute") and make LocalDateTime object
            String[] dateTimeValues = bookingValues[3].split(":");
            LocalDateTime bookingTime = LocalDateTime.of(
                Integer.parseInt(dateTimeValues[0]),
                Integer.parseInt(dateTimeValues[1]),
                Integer.parseInt(dateTimeValues[2]),
                Integer.parseInt(dateTimeValues[3]),
                Integer.parseInt(dateTimeValues[4])
            );
            double cost = Double.parseDouble(bookingValues[4]);
            double duration = Double.parseDouble(bookingValues[5]);
            long customerId = Long.parseLong(bookingValues[6]);
            long serviceId = Long.parseLong(bookingValues[7]);
            String paymentMethodType = bookingValues[8];
            Customer customer = allCustomers.getCustomer(customerId);
            Service service = allServices.getService(serviceId);
            //Make new booking
            Booking booking = new Booking(bookingId, pickup, luggage, bookingTime, cost, duration, customer, service, paymentMethodType);
            allBookings.addBooking(booking);
            //Link booking to the relevant service and customer
            service.addBooking(booking);
            customer.addBooking(booking);
        }
    }




    public static void main(String[] args) throws IOException {
        //Create Routes object to hold data on routes between locations
        final Routes routesData = new Routes();
        final TripCalculator tripCalculator = new TripCalculator(routesData);

        //Create collection classes for customers, bookings and services
        AllCustomers allCustomers = new AllCustomers();
        AllServices allServices = new AllServices(routesData);
        AllBookings allBookings = new AllBookings();
        AllPaymentMethods allPaymentMethods = new AllPaymentMethods();

        //Read data from each file
        readCustomersFromFile(allCustomers);
        readPaymentDetailsFromFile(allPaymentMethods, allCustomers);
        readServicesFromFile(allServices);
        readBookingsFromFile(allBookings, allServices, allCustomers);

        //Print out the data to check it has been read in correctly
        allCustomers.printCustomers();
        allPaymentMethods.printPaymentMethods();
        allServices.printServices();
        allBookings.printBookings();

        //Make the main form and pass in the data objects
        MainForm form = new MainForm(routesData, allCustomers, allServices, allBookings, tripCalculator);

    }
}

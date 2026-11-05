import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class EnterEmailForm extends JFrame {
    private JPanel main;
    private JTextField txtEmail;
    private JButton btnContinue;
    private JLabel lblWarning;


    public EnterEmailForm(AllBookings allBookings, AllServices allServices, AllCustomers allCustomers, TripCalculator tripCalculator) {
        setContentPane(main); // links the .form layout to the frame
        setTitle("COMP5009 A2");
        setSize(400, 200);
        setVisible(true);
        lblWarning.setForeground(Color.RED);

        btnContinue.addActionListener(e -> {
            System.out.println(txtEmail.getText());

            String email = txtEmail.getText();

            if (isValidEmail(email)){
                Customer theCustomer = null;
                ArrayList<Customer> customers = new ArrayList<>(allCustomers.getCustomerMap().values());
                for (Customer c : customers){
                    if (Objects.equals(c.getEmail(), email)){
                        theCustomer = allCustomers.getCustomerMap().get(c.getId());
                        dispose();
                        new ManageBookingsForm(allBookings, allServices, theCustomer, tripCalculator);
                    }
                }
                lblWarning.setText("Customer not found");
            }
        });
    }

    private boolean isValidEmail(String email){
        if (email.isEmpty()){
            lblWarning.setText("Please enter an email");
            return false;
        }
        if (!email.contains("@") || !email.contains(".")){
            lblWarning.setText("Please enter a valid email");
            return false;
        }
        return true;
    }
}

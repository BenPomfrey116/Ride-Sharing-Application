import java.util.HashMap;

/**
 * Class to store All PaymentMethodData objects
 */
public class AllPaymentMethods {
    //HashMap stores paymentMethods by their ID
    private HashMap<Long, PaymentMethodData> paymentMethods = new HashMap<>();

    /**
     * Adds a paymentMethod to the HashMap of paymentMethods
     * @param paymentMethod to be added
     */
    public void addPaymentMethod(PaymentMethodData paymentMethod) {
        System.out.println("--> Adding paymentMethod: " + paymentMethod);
        paymentMethods.put(paymentMethod.getId(), paymentMethod);
    }

    /**
     * Gets a paymentMethod from the HashMap of paymentMethods using the paymentMethod ID
     * @param id to lookup
     * @return paymentMethod with given id
     */
    public PaymentMethodData getPaymentMethod(Long id) {
        return paymentMethods.get(id);
    }

    /**
     * Gets the entire HashMap of paymentMethods
     * @return paymentMethods
     */
    public HashMap<Long, PaymentMethodData> getPaymentMethodMap() {
        return paymentMethods;
    }

    /**
     * Prints all paymentMethods for debugging
     */
    public void printPaymentMethods() {
        System.out.println("--------------------");
        System.out.println("All PaymentMethods:");
        for (PaymentMethodData paymentMethod : paymentMethods.values()) {
            System.out.println(paymentMethod);
        }
        System.out.println("--------------------");
    }
}

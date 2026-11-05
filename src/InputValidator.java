import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Class for validating user inputs from forms
 */
public class InputValidator {

    /**
     * Checks if a given input is empty and sets the invalidMsg text accordingly
     * @param invalidMsg JLabel to set text in
     * @param input to be checked
     * @param fieldMsgAddition specific error message for the input (e.g. "a name" or "an airport")
     * @return true if the input is empty, false otherwise
     */
    public static boolean checkEmpty(JLabel invalidMsg, String input, String fieldMsgAddition) {
        boolean empty = input.isEmpty();
        if(empty) {
            invalidMsg.setText("Please enter " + fieldMsgAddition);
        }
        return empty;
    }

    /**
     * Checks if a given input is numeric and sets the invalidMsg text accordingly
     * @param invalidMsg JLabel to set text in
     * @param input to be checked
     * @param fieldMsgAddition specific error message for the input (e.g. "a name" or "an airport")
     * @return true if the input is numeric, false otherwise
     */
    public static boolean checkNumeric(JLabel invalidMsg, String input, String fieldMsgAddition) {
        boolean numeric = true;
        //Try parsing the input to a long
        try {
            Long.parseLong(input);
        }
        //Catches when the input is not numeric
        catch (NumberFormatException e) {
            invalidMsg.setText(fieldMsgAddition + " must be numeric");
            numeric = false;
        }
        return numeric;
    }

    /**
     * Checks if a given input is the expected length and sets the invalidMsg text accordingly
     * @param invalidMsg JLabel to set text in
     * @param input to be checked
     * @param expectedLength to compare to
     * @param fieldMsgAddition specific error message for the input (e.g. "a name" or "an airport")
     * @return true if the input is the expected length, false otherwise
     */
    public static boolean checkLength(JLabel invalidMsg, String input, int expectedLength, String fieldMsgAddition) {
        boolean correctLength = input.length() == expectedLength;
        if(!correctLength) {
            invalidMsg.setText(fieldMsgAddition + " must be " + expectedLength + " digits long");
        }
        return correctLength;
    }

    /**
     * Checks if a selection has been made in a combo box/dropdown
     * Valid for anything but "Select"
     * @param invalidMsg JLabel to set text in
     * @param selection to be checked
     * @param fieldMsgAddition specific error message for the input (e.g. "a name" or "an airport")
     * @return true if a valid selection has been made, false otherwise
     */
    public static boolean validateComboBox(JLabel invalidMsg, String selection, String fieldMsgAddition) {
        boolean valid = !(selection.equals("Select"));
        if(!valid) {
            invalidMsg.setText("Please select " + fieldMsgAddition);
        }
        invalidMsg.setVisible(!valid);
        return valid;
    }

    /**
     * Checks if a name input is valid
     * A valid name is:
     * - Not empty
     * - Does not contain any numbers
     * @param invalidNameMsg JLabel to set text in
     * @param name to be checked
     * @return true if the name is valid, false otherwise
     */
    public static boolean validateName(JLabel invalidNameMsg, String name) {
        boolean validName = true;
        //Check if the name field is empty
        if(checkEmpty(invalidNameMsg, name, "a name")) {
            validName = false;
        }
        else {
            //Check the name does not contain a number
            char[] charArray = name.toCharArray();
            int charIndex = 0;
            while(validName && charIndex < charArray.length) {
                char c = charArray[charIndex];
                if(Character.isDigit(c)) {
                    invalidNameMsg.setText("Name cannot contain a number");
                    invalidNameMsg.setVisible(true);
                    validName = false;
                }
                charIndex++;
            }
        }
        invalidNameMsg.setVisible(!validName);
        return validName;
    }

    /**
     * Checks if an email input is valid
     * A valid email is:
     * - Not empty
     * - Contains an "@" and a "."
     * @param invalidMsg JLabel to set text in
     * @param email to be checked
     * @return true if the email is valid, false otherwise
     */
    public static boolean validateEmail(JLabel invalidMsg, String email) {
        invalidMsg.setVisible(false);
        boolean validEmail = true;
        //Check email is not empty
        if(checkEmpty(invalidMsg, email, "an email")) {
            validEmail = false;
        }
        //Check email contains an "@" and a "."
        else if(!email.contains("@") || !email.contains(".")) {
            invalidMsg.setText("Invalid email format");
            validEmail = false;
        }
        invalidMsg.setVisible(!validEmail);
        return validEmail;
    }

    /**
     * Checks both the date and time inputs together to ensure validity and not in the past
     * Validity definitions detailed in individual methods
     * @param invalidDateMsg JLabel to set invalid date text in
     * @param invalidTimeMsg JLabel to set invalid time text in
     * @param dayInput of the date
     * @param monthInput of the date
     * @param yearInput of the date
     * @param hoursInput of the time
     * @param minutesInput of the time
     * @return true if both the date and time are valid, false otherwise
     */
    public static boolean validateDateTime(JLabel invalidDateMsg, JLabel invalidTimeMsg, String dayInput, String monthInput, String yearInput, String hoursInput, String minutesInput) {
        //Check the date is valid
        boolean dateValid = validateDate(invalidDateMsg, dayInput, monthInput, yearInput);
        boolean timeValid = false;
        LocalDate date = null;
        if(dateValid) {
            //Create a data if date is valid to compare with the time
            date = LocalDate.of(Integer.parseInt(yearInput), Integer.parseInt(monthInput), Integer.parseInt(dayInput));
        }
        //Check the time is valid given the date and its validity
        timeValid = validateTime(invalidTimeMsg, hoursInput, minutesInput, date, dateValid);
        return dateValid && timeValid;
    }

    /**
     * Checks if a date input is valid and not in the past
     * A valid date is when:
     * - All fields are filled in
     * - The date is possible (e.g. not 31st Feb)
     * - The date is not in the past
     * @param invalidMsg JLabel to set text in
     * @param dayInput of the date
     * @param monthInput of the date
     * @param yearInput of the date
     * @return true if the date is valid and not in the past, false otherwise
     */
    public static boolean validateDate(JLabel invalidMsg, String dayInput, String monthInput, String yearInput) {
        boolean dateValid = true;
        //Check all fields are filled in
        if(dayInput.isEmpty() || monthInput.isEmpty() || yearInput.isEmpty()) {
            invalidMsg.setText("Please enter a date");
            dateValid = false;
        }
        else {
            String combinedInput = dayInput + monthInput + yearInput;
            dateValid = checkNumeric(invalidMsg, combinedInput, "Date");
            if(dateValid) {
                int day = Integer.parseInt(dayInput);
                int month = Integer.parseInt(monthInput);
                int year = Integer.parseInt(yearInput);
                try {
                    //Try making a date with the inputs (catches when it's not possible e.g. 31st Feb)
                    LocalDate inputDate = LocalDate.of(year, month, day);

                    //Check date isn't in the past
                    if (inputDate.isBefore(LocalDate.now())) {
                        invalidMsg.setText("Date cannot be in the past");
                        dateValid = false;
                    }
                }
                //Catches when the user has entered an impossible date
                catch(Exception e) {
                    invalidMsg.setText("Invalid date");
                    dateValid = false;
                }
            }
        }
        invalidMsg.setVisible(!dateValid);
        return dateValid;
    }

    /**
     * Checks if a time input is valid and not in the past (given the date)
     * A valid time is when:
     * - All fields are filled in
     * - The time is possible
     * - The time is not in the past (compared to the current time if the date is today)
     * @param invalidMsg JLabel to set text in
     * @param hoursInput of the time
     * @param minutesInput of the time
     * @param date to compare for past time
     * @param dateValid boolean to ensure the date is valid
     * @return true if the time is valid, false otherwise
     */
    private static boolean validateTime(JLabel invalidMsg, String hoursInput, String minutesInput, LocalDate date, boolean dateValid) {
        boolean timeValid = true;
        //Check all fields are filled in
        if(hoursInput.isEmpty() || minutesInput.isEmpty()) {
            invalidMsg.setText("Please enter a time");
            timeValid = false;
        }
        else {
            String combinedInput = hoursInput + minutesInput;
            timeValid = checkNumeric(invalidMsg, combinedInput, "Time");
            if(timeValid) {
                int hours = Integer.parseInt(hoursInput);
                int minutes = Integer.parseInt(minutesInput);
                //Try making a time with the inputs (catches when it's not possible e.g. 26:60)
                try {
                    LocalTime inputTime = LocalTime.of(hours, minutes);
                    LocalDateTime inputDateTime = LocalDateTime.of(date, inputTime);
                    //Check time isn't in the past
                    if(inputDateTime.isBefore(LocalDateTime.now())) {
                        invalidMsg.setText("Time cannot be in the past");
                        timeValid = false;
                    }
                }
                //Catches when the time is not possible or the date is invalid
                catch (Exception e) {
                    invalidMsg.setText("Invalid time");
                    //Check if time cannot be valid due to an invalid date
                    if(!dateValid) {
                        invalidMsg.setText("Time invalid without valid date");
                    }
                    timeValid = false;
                }
            }
        }
        invalidMsg.setVisible(!timeValid);
        return timeValid;
    }

    /**
     * Checks if a bag amount input is valid
     * A valid bag amount is:
     * - Not empty
     * - Numeric
     * - Not negative
     * @param invalidMsg JLabel to set text in
     * @param bagAmount to be checked
     * @return true if the bag amount is valid, false otherwise
     */
    public static boolean validateBags(JLabel invalidMsg, String bagAmount, String vehicleType) {
        boolean bagsValid = true;
        //Check if field is filled in
        if(checkEmpty(invalidMsg, bagAmount, "the number of bags")) {
            bagsValid = false;
        }
        //Check the input is numeric
        else {
            bagsValid = checkNumeric(invalidMsg, bagAmount, "Number of bags");
            if(bagsValid) {
                int bags = Integer.parseInt(bagAmount);
                //Check the number is not negative
                if(bags < 0) {
                    invalidMsg.setText("Number of bags cannot be negative");
                    bagsValid = false;
                }
                else if(vehicleType.equals("Standard") && bags > 10) {
                    invalidMsg.setText("Standard vehicles have maximum 10 bags");
                    bagsValid = false;
                }
                else if(vehicleType.equals("Premium") && bags > 20) {
                    invalidMsg.setText("Premium vehicles have maximum 20 bags");
                    bagsValid = false;
                }
            }
        }
        invalidMsg.setVisible(!bagsValid);
        return bagsValid;
    }

    /**
     * Checks if a card number input is valid
     * A valid card number is:
     * - Not empty
     * - Numeric
     * - 16 digits long
     * @param invalidMsg JLabel to set text in
     * @param cardNumber to be checked
     * @return true if the card number is valid, false otherwise
     */
    public static boolean validateCardNumber(JLabel invalidMsg, String cardNumber) {
        //Check if the card number field is empty
        boolean validCardNumber = !checkEmpty(invalidMsg, cardNumber, "your card number");
        System.out.println("Card number empty? " + validCardNumber + "checking numeric...");
        if(validCardNumber) {
            //Check the card number is numeric
            validCardNumber = checkNumeric(invalidMsg, cardNumber, "Card number");
        }
        System.out.println("Card number numeric? " + validCardNumber + "checking length...");
        if(validCardNumber) {
            //Check the card number is 16 digits long
            validCardNumber = checkLength(invalidMsg, cardNumber, 16, "Card number");
        }
        System.out.println("Card number valid? " + validCardNumber);
        invalidMsg.setVisible(!validCardNumber);
        return validCardNumber;
    }

    /**
     * Checks if a card expiry date input is valid and not in the past
     * @param invalidMsg JLabel to set text in
     * @param month of the expiry date
     * @param year of the expiry date
     * @return true if the expiry date is valid, false otherwise
     */
    public static boolean validateExpiryDate(JLabel invalidMsg, String month, String year) {
        boolean validExpiryDate = true;
        //Check if the month or year fields are empty
        if(month.isEmpty() || year.isEmpty()) {
            invalidMsg.setText("Please enter your card's expiry date");
            validExpiryDate = false;
        }
        //Check the date is in the correct form (mm/yy)
        else if(month.length() > 2 || year.length() > 2) {
            invalidMsg.setText("Invalid date format");
            validExpiryDate = false;
        }
        else {
            //Check the date is numeric
            validExpiryDate = checkNumeric(invalidMsg, month + year, "Date");
            if(validExpiryDate) {
                int monthInt = Integer.parseInt(month);
                int yearInt = Integer.parseInt(year);
                //Check if month is not possible
                if(monthInt < 1 || monthInt > 12) {
                    invalidMsg.setText("Invalid month");
                    validExpiryDate = false;
                }
                else {
                    //Check the date (mm/yy) is not in the past
                    LocalDate date = LocalDate.now();
                    String currentYear = date.format(DateTimeFormatter.ofPattern("yy"));
                    int currentYearInt = Integer.parseInt(currentYear);
                    boolean pastYear = yearInt < currentYearInt;
                    boolean sameYear = yearInt == currentYearInt;
                    boolean pastMonth = monthInt < date.getMonthValue();
                    //Check the date is not in the past
                    if(pastYear || (sameYear && pastMonth)) {
                        invalidMsg.setText("Card has expired");
                        validExpiryDate = false;
                    }
                }
            }
        }
        invalidMsg.setVisible(!validExpiryDate);
        return validExpiryDate;
    }

    /**
     * Checks if a card CVV input is valid
     * A valid CVV is:
     * - Not empty
     * - Numeric
     * - 3 digits long
     * @param invalidMsg JLabel to set text in
     * @param cvv to be checked
     * @return true if the CVV is valid, false otherwise
     */
    public static boolean validateCVV(JLabel invalidMsg, String cvv) {
        //Check if the CVV field is empty
        boolean validCVV = !checkEmpty(invalidMsg, cvv, "your card's CVV");
        //Check the CVV is numeric
        if(validCVV) {
            validCVV = checkNumeric(invalidMsg, cvv, "CVV");
        }
        //Check the CVV is 3 digits long
        if(validCVV) {
            validCVV = checkLength(invalidMsg, cvv, 3, "CVV");
        }
        invalidMsg.setVisible(!validCVV);
        return validCVV;
    }

    /**
     * Checks if a bank account number input is valid
     * A valid bank account number is:
     * - Not empty
     * - Numeric
     * - 8 digits long
     * @param invalidMsg JLabel to set text in
     * @param accountNumber to be checked
     * @return true if the bank account number is valid, false otherwise
     */
    public static boolean validateBankAccountNumber(JLabel invalidMsg, String accountNumber) {
        //Check if the account number field is empty
        boolean validAccountNumber = !checkEmpty(invalidMsg, accountNumber, "your account number");
        if(validAccountNumber) {
            //Check the card number is numeric
            validAccountNumber = checkNumeric(invalidMsg, accountNumber, "Account number");
        }
        if(validAccountNumber) {
            //Check the card number is 16 digits long
            validAccountNumber = checkLength(invalidMsg, accountNumber, 8, "Account number");
        }
        invalidMsg.setVisible(!validAccountNumber);
        return validAccountNumber;
    }

    /**
     * Checks if a bank sort code part is valid
     * A valid bank sort code is when all 3 parts are:
     * - Not empty
     * - Numeric
     * - 2 digits long
     * @param invalidSortCodeMsg JLabel to set text in
     * @param sortCodePart to be checked
     * @return true if the sort code part is valid, false otherwise
     */
    private static boolean validateSortCodePart(JLabel invalidSortCodeMsg, String sortCodePart) {
        //Check if the field is empty
        boolean validPart = !checkEmpty(invalidSortCodeMsg, sortCodePart, "a sort code");
        if(validPart) {
            //Check the sort code part is numeric
            validPart = checkNumeric(invalidSortCodeMsg, sortCodePart, "Sort code");
        }
        if(validPart) {
            //Check the sort code part is 2 digits long
            validPart = checkLength(invalidSortCodeMsg, sortCodePart, 2, "Sort code parts");
        }
        invalidSortCodeMsg.setVisible(!validPart);
        return validPart;
    }

    /**
     * Checks if a bank sort code input is valid by checking each part
     * @param invalidMsg JLabel to set text in
     * @param partA of the sort code
     * @param partB of the sort code
     * @param partC of the sort code
     * @return true if all parts are valid, false otherwise
     */
    public static boolean validateSortCode(JLabel invalidMsg, String partA, String partB, String partC) {
        return validateSortCodePart(invalidMsg, partA) && validateSortCodePart(invalidMsg, partB) && validateSortCodePart(invalidMsg, partC);
    }





}

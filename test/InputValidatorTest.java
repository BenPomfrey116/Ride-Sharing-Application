import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    @Test
    void checkEmptyEmpty() {
        assertTrue(InputValidator.checkEmpty(new JLabel(), "", "a test message"));
    }

    @Test
    void checkEmptyNotEmpty() {
        assertFalse(InputValidator.checkEmpty(new JLabel(), "Not empty", "a test message"));
    }

    @Test
    void checkNumericValid() {
        assertTrue(InputValidator.checkNumeric(new JLabel(), "123", "a test message"));
    }

    @Test
    void checkNumericInvalid() {
        assertFalse(InputValidator.checkNumeric(new JLabel(), "123abc", "a test message"));
    }

    @Test
    void checkLengthValid() {
        assertTrue(InputValidator.checkLength(new JLabel(), "12345", 5, "a test message"));
    }

    @Test
    void checkLengthInvalid() {
        assertFalse(InputValidator.checkLength(new JLabel(), "1234", 5, "a test message"));
    }

    @Test
    void validateComboBoxValid() {
        assertTrue(InputValidator.validateComboBox(new JLabel(), "Gatwick", "a test message"));
    }

    @Test
    void validateComboBoxInvalid() {
        assertFalse(InputValidator.validateComboBox(new JLabel(), "Select", "a test message"));
    }

    @Test
    void validateNameValid() {
        assertTrue(InputValidator.validateName(new JLabel(), "John"));
    }

    @Test
    void validateNameEmpty() {
        assertFalse(InputValidator.validateName(new JLabel(), ""));
    }

    @Test
    void validateNameWithNumber() {
        assertFalse(InputValidator.validateName(new JLabel(), "John1"));
    }

    @Test
    void validateEmailValid() {
        assertTrue(InputValidator.validateEmail(new JLabel(), "john_smith@gmail.com"));
    }

    @Test
    void validateEmailEmpty() {
        assertFalse(InputValidator.validateEmail(new JLabel(), ""));
    }

    @Test
    void validateEmailBadFormat() {
        assertFalse(InputValidator.validateEmail(new JLabel(), "john_smithgmail.com"));
        assertFalse(InputValidator.validateEmail(new JLabel(), "john_smith@gmailcom"));
    }

    @Test
    void validateDateValid() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(10);
        String dayString = String.valueOf(futureDate.getDayOfMonth());
        String monthString = String.valueOf(futureDate.getMonthValue());
        String yearString = String.valueOf(futureDate.getYear());

        assertTrue(InputValidator.validateDate(new JLabel(), dayString, monthString, yearString));
    }

    @Test
    void validateDateEmpty() {
        assertFalse(InputValidator.validateDate(new JLabel(), "", "6", "2026"));
        assertFalse(InputValidator.validateDate(new JLabel(), "17", "", "2026"));
        assertFalse(InputValidator.validateDate(new JLabel(), "17", "6", ""));
    }

    @Test
    void validateDateBadFormat() {
        assertFalse(InputValidator.validateDate(new JLabel(), "17th", "6", "2026"));
        assertFalse(InputValidator.validateDate(new JLabel(), "17", "June", "2026"));
        assertFalse(InputValidator.validateDate(new JLabel(), "17", "6", "26"));
        assertFalse(InputValidator.validateDate(new JLabel(), "Seventeenth", "June", "2026"));
    }

    @Test
    void validateDateImpossible() {
        assertFalse(InputValidator.validateDate(new JLabel(), "30", "2", "2027"));
        assertFalse(InputValidator.validateDate(new JLabel(), "31", "11", "2026"));
        assertFalse(InputValidator.validateDate(new JLabel(), "0", "4", "2026"));
    }

    @Test
    void validateDatePast() {
        assertFalse(InputValidator.validateDate(new JLabel(), "17", "6", "2025"));
    }

    @Test
    void validateTimeValid() {
        int currentHour = LocalDateTime.now().getHour();
        int currentMinute = LocalDateTime.now().getMinute();
        String futureHourString = String.valueOf((currentHour + 1) % 24);
        String futureMinuteString = String.valueOf((currentMinute + 30) % 60);
        assertTrue(InputValidator.validateDateTime(new JLabel(), new JLabel(), "17", "6", "2030", futureHourString, futureMinuteString));
    }

    @Test
    void validateTimeEmpty() {
        assertFalse(InputValidator.validateDateTime(new JLabel(), new JLabel(), "17", "6", "2030", "", "00"));
        assertFalse(InputValidator.validateDateTime(new JLabel(), new JLabel(), "17", "6", "2030", "13", ""));
    }

    @Test
    void validateTimeBadFormat() {
        assertFalse(InputValidator.validateDateTime(new JLabel(), new JLabel(), "17", "6", "2030", "1", "00pm"));
        assertFalse(InputValidator.validateDateTime(new JLabel(), new JLabel(), "17", "6", "2030", "13hr", "0min"));
    }

    @Test
    void validateTimeImpossible() {
        assertFalse(InputValidator.validateDateTime(new JLabel(), new JLabel(), "17", "6", "2030", "24", "00"));
        assertFalse(InputValidator.validateDateTime(new JLabel(), new JLabel(), "17", "6", "2030", "12", "60"));
        assertFalse(InputValidator.validateDateTime(new JLabel(), new JLabel(), "17", "6", "2030", "-1", "00"));
        assertFalse(InputValidator.validateDateTime(new JLabel(), new JLabel(), "17", "6", "2030", "12", "-1"));
    }

    @Test
    void validateTimePast() {
        int currentHour = LocalDateTime.now().getHour();
        int currentMinute = LocalDateTime.now().getMinute();
        String currentHourString = String.valueOf(currentHour);
        String futureMinuteString = String.valueOf((currentMinute + 30) % 60);
        int pastHour = currentHour - 2;
        int pastMinute = currentMinute - 30;
        if(pastHour < 0) {
            pastHour += 24;
        }
        if(pastMinute < 0) {
            pastMinute += 60;
        }
        String pastHourString = String.valueOf(pastHour);
        String pastMinuteString = String.valueOf(pastMinute);

        String currentDay = String.valueOf(LocalDate.now().getDayOfMonth());
        String currentMonth = String.valueOf(LocalDate.now().getMonthValue());
        String currentYear = String.valueOf(LocalDate.now().getYear());

        assertFalse(InputValidator.validateDateTime(new JLabel(), new JLabel(), currentDay, currentMonth, currentYear, pastHourString, futureMinuteString));
    }

    @Test
    void validateTimeInvalidDate() {
        assertFalse(InputValidator.validateDateTime(new JLabel(), new JLabel(), "30", "2", "2020", "20", "00"));
    }

    @Test
    void validateBagsValid() {
        assertTrue(InputValidator.validateBags(new JLabel(), "0", "Standard"));
        assertTrue(InputValidator.validateBags(new JLabel(), "1", "Standard"));
    }

    @Test
    void validateBagsEmpty() {
        assertFalse(InputValidator.validateBags(new JLabel(), "", "Standard"));
    }

    @Test
    void validateBagsBadFormat() {
        assertFalse(InputValidator.validateBags(new JLabel(), "One", "Standard"));
        assertFalse(InputValidator.validateBags(new JLabel(), "-1", "Standard"));
        assertFalse(InputValidator.validateBags(new JLabel(), "11", "Standard"));
        assertFalse(InputValidator.validateBags(new JLabel(), "21", "Premium"));
    }

    @Test
    void validateCardNumberValid() {
        assertTrue(InputValidator.validateCardNumber(new JLabel(), "1234567812345678"));
    }

    @Test
    void validateCardNumberEmpty() {
        assertFalse(InputValidator.validateCardNumber(new JLabel(), ""));
    }

    @Test
    void validateCardNumberNonNumeric() {
        assertFalse(InputValidator.validateCardNumber(new JLabel(), "1234abcd12345678"));
    }

    @Test
    void validateCardNumberBadLength() {
        assertFalse(InputValidator.validateCardNumber(new JLabel(), "12345678123456789"));
        assertFalse(InputValidator.validateCardNumber(new JLabel(), "123456781234567"));
    }

    @Test
    void validateExpiryDateValid() {
        assertTrue(InputValidator.validateExpiryDate(new JLabel(), "6", "35"));
    }

    @Test
    void validateExpiryDateEmpty() {
        assertFalse(InputValidator.validateExpiryDate(new JLabel(), "", "26"));
        assertFalse(InputValidator.validateExpiryDate(new JLabel(), "6", ""));
    }

    @Test
    void validateExpiryDateBadFormat() {
        assertFalse(InputValidator.validateExpiryDate(new JLabel(), "June", "26"));
        assertFalse(InputValidator.validateExpiryDate(new JLabel(), "6", "2026"));
    }

    @Test
    void validateExpiryDatePast() {
        assertFalse(InputValidator.validateExpiryDate(new JLabel(), "6", "20"));
    }

    @Test
    void validateCVVValid() {
        assertTrue(InputValidator.validateCVV(new JLabel(), "123"));
    }

    @Test
    void validateCVVEmpty() {
        assertFalse(InputValidator.validateCVV(new JLabel(), ""));
    }

    @Test
    void validateCVVNonNumeric() {
        assertFalse(InputValidator.validateCVV(new JLabel(), "12a"));
    }

    @Test
    void validateBankAccountNumberValid() {
        assertTrue(InputValidator.validateBankAccountNumber(new JLabel(), "12345678"));
    }

    @Test
    void validateBankAccountNumberEmpty() {
        assertFalse(InputValidator.validateBankAccountNumber(new JLabel(), ""));
    }

    @Test
    void validateBankAccountNumberNonNumeric() {
        assertFalse(InputValidator.validateBankAccountNumber(new JLabel(), "1234567a"));
    }

    @Test
    void validateBankAccountNumberBadLength() {
        assertFalse(InputValidator.validateBankAccountNumber(new JLabel(), "123456789"));
        assertFalse(InputValidator.validateBankAccountNumber(new JLabel(), "1234567"));
    }

    @Test
    void validateSortCodeValid() {
        assertTrue(InputValidator.validateSortCode(new JLabel(), "12", "34", "56"));
    }

    @Test
    void validateSortCodeEmpty() {
        assertFalse(InputValidator.validateSortCode(new JLabel(), "", "34", "56"));
        assertFalse(InputValidator.validateSortCode(new JLabel(), "12", "", "56"));
        assertFalse(InputValidator.validateSortCode(new JLabel(), "12", "34", ""));
    }

    @Test
    void validateSortCodeNonNumeric() {
        assertFalse(InputValidator.validateSortCode(new JLabel(), "1a", "34", "56"));
        assertFalse(InputValidator.validateSortCode(new JLabel(), "12", "3b", "56"));
        assertFalse(InputValidator.validateSortCode(new JLabel(), "12", "34", "5c"));
    }

    @Test
    void validateSortCodeBadLength() {
        assertFalse(InputValidator.validateSortCode(new JLabel(), "1", "34", "56"));
        assertFalse(InputValidator.validateSortCode(new JLabel(), "12", "345", "56"));
        assertFalse(InputValidator.validateSortCode(new JLabel(), "12", "34", "1255"));
    }


}
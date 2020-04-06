package de.tblsoft.solr.logic.iban;

import java.util.Random;

public class RandomIbanGenerator {

    private boolean random = true;


    private String countryCode = "DE";


    // 2 digits
    private String checkDigits = "89";


    // 8 digits
    private String blz = "12345678";

    // 10 digits
    private String accountNumber = "1234567890";


    public String  getRandomIban() {
        StringBuilder iban = new StringBuilder();
        if(random) {
            checkDigits = generateRandomDigitsString(2);
            blz = generateRandomDigitsString(8);
            accountNumber = generateRandomDigitsString(10);
        }

        iban.append(countryCode);
        iban.append(checkDigits);
        iban.append(blz);
        iban.append(accountNumber);

        return iban.toString();
    }

    public static String generateRandomDigitsString(int n) {
        String value =  String.valueOf(generateRandomDigits(n));
        return value;
    }

    public static int generateRandomDigits(int n) {
        int m = (int) Math.pow(10, n - 1);
        return m + new Random().nextInt(9 * m);
    }

    /**
     * Getter for property 'random'.
     *
     * @return Value for property 'random'.
     */
    public boolean isRandom() {
        return random;
    }

    /**
     * Setter for property 'random'.
     *
     * @param random Value to set for property 'random'.
     */
    public void setRandom(boolean random) {
        this.random = random;
    }
}

package de.tblsoft.solr.logic.iban;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class RandomIbanGeneratorTest {

    @Test
    public void getRandomIban() {

        RandomIbanGenerator randomIbanGenerator = new RandomIbanGenerator();
        randomIbanGenerator.setRandom(false);
        Assert.assertEquals("DE89123456781234567890", randomIbanGenerator.getRandomIban());
    }

    @Test
    public void getRandomNumber10DigitsTest() {
        RandomIbanGenerator randomIbanGenerator = new RandomIbanGenerator();
        String tenDigits = randomIbanGenerator.generateRandomDigitsString(10);
        Assert.assertEquals(10, tenDigits.length());
    }


    @Test
    public void getRandomNumber8DigitsTest() {
        RandomIbanGenerator randomIbanGenerator = new RandomIbanGenerator();
        String tenDigits = randomIbanGenerator.generateRandomDigitsString(8);
        Assert.assertEquals(8, tenDigits.length());
    }

    @Test
    public void getRandomNumber2DigitsTest() {
        RandomIbanGenerator randomIbanGenerator = new RandomIbanGenerator();
        String tenDigits = randomIbanGenerator.generateRandomDigitsString(2);
        Assert.assertEquals(2, tenDigits.length());
    }

}
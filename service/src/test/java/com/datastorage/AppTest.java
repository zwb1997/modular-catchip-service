package com.datastorage;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.net.Inet4Address;

import com.google.common.net.InetAddresses;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        try {
            Inet4Address addresses = InetAddresses.fromIPv4BigInteger(BigInteger.valueOf(4294967295L));
            System.out.println(addresses.getHostAddress());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}

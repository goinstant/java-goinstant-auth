package com.goinstant.auth;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.goinstant.auth.Signer;

/**
 * Unit test for simple App.
 */
public class SignerTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SignerTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(SignerTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testSigner() {
        Signer signer = new Signer("HKYdFdnezle2yrI2_Ph3cHz144bISk-cvuAbeAAA999");
        Map<String,Object> userData = new HashMap<String,Object>();
        userData.put("id","bar");
        userData.put("domain","example.com");
        userData.put("displayName","bob");
        String token = signer.sign(userData);
        assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."+
                     "eyJhdWQiOlsiZ29pbnN0YW50Lm5ldCJdfQ."+
                     "Fcx_VYsOrNjJGHDYAM4ZApk-syrEpbpr4oW9xKRcGg8", token);
    }
}

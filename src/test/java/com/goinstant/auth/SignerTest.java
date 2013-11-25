package com.goinstant.auth;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.goinstant.auth.Group;
import com.goinstant.auth.PlainGroup;
import com.goinstant.auth.PlainUser;
import com.goinstant.auth.Signer;
import com.goinstant.auth.User;

/**
 * Unit tests for Goinstant-auth's Signer class.
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
     * Does a signature check for a user not belonging to any groups.
     */
    public void testHappyPath() {
        Signer signer = new Signer("HKYdFdnezle2yrI2_Ph3cHz144bISk-cvuAbeAAA999");
        PlainUser user = new PlainUser("bar", "example.com", "bob");
        String token = signer.sign(user);
        assertEquals(
            // {"alg":"HS256","typ":"JWT"}
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"+
            "." +
            // {"sub":"bar","aud":["goinstant.net"],"iss":"example.com","dn":"bob"}
            "eyJzdWIiOiJiYXIiLCJhdWQiOlsiZ29pbnN0YW50Lm5ldCJdLCJpc3MiOiJleGFtcGxlLm"+
            "NvbSIsImRuIjoiYm9iIn0"+
            "." +
            "9xPchxBqzfmOC46wINCMlTKoHIMIZ2UTVeGt79FMbFs",
            token
        );

        String token2 = signer.sign(user);
        assertEquals(token, token2);
    }

    /**
     * Does a signature check for a user belonging to a group
     */
    public void testHappyPathWithGroups() {
        Signer signer = new Signer("HKYdFdnezle2yrI2_Ph3cHz144bISk-cvuAbeAAA999");
        HashSet<Group> groups = new HashSet<Group>();
        groups.add(new PlainGroup("1234", "Group 1234"));
        groups.add(new PlainGroup("42", "Meaning Group"));

        PlainUser user = new PlainUser("bar", "example.com", "bob", groups);
        String token = signer.sign(user);
        assertEquals(
            // {"alg":"HS256","typ":"JWT"}
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"+
            "." +
            // Without newlines:
            // {"g":[{"id":"42","dn":"Meaning Group"},{"id":"1234","dn":"Group 1234"}],
            // "sub":"bar","aud":["goinstant.net"],"iss":"example.com","dn":"bob"}
            "eyJnIjpbeyJpZCI6IjQyIiwiZG4iOiJNZWFuaW5nIEdyb3VwIn0seyJpZCI6IjEyMzQiLCJkbi"+
            "I6Ikdyb3VwIDEyMzQifV0sInN1YiI6ImJhciIsImF1ZCI6WyJnb2luc3RhbnQubmV0Il0sImlz"+
            "cyI6ImV4YW1wbGUuY29tIiwiZG4iOiJib2IifQ"+
            "." +
            "5J2sITiJ9JH15ujElmTI_mloRAlQS6dDxjD3RRs_E9M",
            token
        );

        String token2 = signer.sign(user);
        assertEquals(token, token2);
    }

    public void testUserWithoutID() {
        Signer signer = new Signer("HKYdFdnezle2yrI2_Ph3cHz144bISk-cvuAbeAAA999");
        PlainUser user = new PlainUser(null, "example.com", "bob");

        try {
            signer.sign(user);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
        }
    }

    public void testUserWithEmptyId() {
        Signer signer = new Signer("HKYdFdnezle2yrI2_Ph3cHz144bISk-cvuAbeAAA999");
        PlainUser user = new PlainUser("", "example.com", "bob");

        try {
            signer.sign(user);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
        }
    }

    public void testUserWithoutDomain() {
        Signer signer = new Signer("HKYdFdnezle2yrI2_Ph3cHz144bISk-cvuAbeAAA999");
        PlainUser user = new PlainUser("bar", null, "bob");

        try {
            signer.sign(user);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
        }
    }

    public void testUserWithEmptyDomain() {
        Signer signer = new Signer("HKYdFdnezle2yrI2_Ph3cHz144bISk-cvuAbeAAA999");
        PlainUser user = new PlainUser("bar", "", "bob");

        try {
            signer.sign(user);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
        }
    }
}

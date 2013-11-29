package com.goinstant.auth;

import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.lang.System;
import java.text.ParseException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

import com.goinstant.auth.Group;
import com.goinstant.auth.PlainGroup;
import com.goinstant.auth.PlainUser;
import com.goinstant.auth.Signer;
import com.goinstant.auth.User;

import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.*;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONArray;

/**
 * Unit tests for Goinstant-auth's Signer class.
 */
public class SignerTest{

    static final String sharedKey = "HKYdFdnezle2yrI2_Ph3cHz144bISk-cvuAbeAAA999";

    static int countNonNullKeys(Map<String,Object> map) {
        int count = 0;
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                count++;
            }
        }
        return count;
    }

    private JWTClaimsSet verifyJWT(JWSObject parsed) throws Exception {
        MACVerifier verifier = new MACVerifier(Signer.parseKey(sharedKey));
        try {
            boolean valid = parsed.verify(verifier);
            assertTrue(parsed.verify(verifier));
        } catch (JOSEException e) {
            assertTrue(false);
        }

        ReadOnlyJWSHeader header = parsed.getHeader();
        assertEquals(2, header.getIncludedParameters().size());

        assertTrue(header.getAlgorithm() == JWSAlgorithm.HS256);
        assertEquals("JWT", header.getType().toString());

        Payload payload = parsed.getPayload();
        JWTClaimsSet claims = JWTClaimsSet.parse(payload.toString());
        return claims;
    }

    /**
     * Does a signature check for a user not belonging to any groups.
     */
    @Test
    public void testHappyPath() throws Exception {
        Signer signer = new Signer(sharedKey);
        PlainUser user = new PlainUser("bar", "example.com", "bob");
        String token = signer.sign(user);

        JWSObject parsed = JWSObject.parse(token);
        JWTClaimsSet claims = verifyJWT(parsed);

        // assert no extra claims than the four below:
        assertEquals(4, countNonNullKeys(claims.getAllClaims()));

        assertEquals("bar", claims.getSubject());
        assertEquals("example.com", claims.getIssuer());
        assertEquals("bob", claims.getCustomClaim("dn"));

        List<String> aud = claims.getAudience();
        assertEquals(1, aud.size());
        assertEquals("goinstant.net", aud.get(0));

        // re-sign
        String token2 = signer.sign(user);
        assertEquals(token, token2);
    }

    /**
     * Does a signature check for a user belonging to a group
     */
    @Test
    public void testHappyPathWithGroups() throws Exception {
        Signer signer = new Signer(sharedKey);
        TreeSet<Group> groups = new TreeSet<Group>(); // deterministic ordering
        groups.add(new PlainGroup("42", "Meaning Group"));
        groups.add(new PlainGroup("1234", "Group 1234"));

        PlainUser user = new PlainUser("bar", "example.com", "bob", groups);
        String token = signer.sign(user);

        JWSObject parsed = JWSObject.parse(token);
        JWTClaimsSet claims = verifyJWT(parsed);

        // assert no extra claims than the five below:
        assertEquals(5, countNonNullKeys(claims.getAllClaims()));

        assertEquals("bar", claims.getSubject());
        assertEquals("example.com", claims.getIssuer());
        assertEquals("bob", claims.getCustomClaim("dn"));

        List<String> aud = claims.getAudience();
        assertThat(aud.size(), is(1));
        assertThat(aud, hasItem("goinstant.net"));


        Object o = claims.getCustomClaim("g");
        assertThat(o, instanceOf(JSONArray.class));

        @SuppressWarnings("unchecked") // instanceOf above checks
        JSONArray g = (JSONArray)o;
        assertThat(g.size(), is(2));
        assertThat(g, everyItem(instanceOf(JSONObject.class)));

        @SuppressWarnings("unchecked") // everyItem(instanceOf) above checks
        JSONObject g0 = (JSONObject) g.get(0);
        assertThat(g0.get("id").toString(), is("1234"));
        assertThat(g0.get("dn").toString(), is("Group 1234"));

        @SuppressWarnings("unchecked") // everyItem(instanceOf) above checks
        JSONObject g1 = (JSONObject) g.get(1);
        assertThat(g1.get("id").toString(), is("42"));
        assertThat(g1.get("dn").toString(), is("Meaning Group"));

        // re-sign
        String token2 = signer.sign(user);
        assertEquals(token, token2);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testUserWithoutID() {
        Signer signer = new Signer(sharedKey);
        PlainUser user = new PlainUser(null, "example.com", "bob");
        signer.sign(user);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testUserWithEmptyId() {
        Signer signer = new Signer(sharedKey);
        PlainUser user = new PlainUser("", "example.com", "bob");
        signer.sign(user);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testUserWithoutDomain() {
        Signer signer = new Signer(sharedKey);
        PlainUser user = new PlainUser("bar", null, "bob");
        signer.sign(user);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testUserWithEmptyDomain() {
        Signer signer = new Signer(sharedKey);
        PlainUser user = new PlainUser("bar", "", "bob");
        signer.sign(user);
    }
}

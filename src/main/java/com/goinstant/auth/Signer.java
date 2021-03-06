package com.goinstant.auth;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * Generates JWT Tokens for your Users.
 *
 * Produces tokens that not only log-in your users to GoInstant, but provides
 * your GoInstant client-side application with valuable information about those
 * users!
 * <br>
 * Please read GoInstant guide on <a href="https://developer.goinstant.com/v1/security_and_auth/index.html">Security and Auth</a>.
 */
public class Signer {
    private JWSSigner hmac;

    /**
     * Define the JWT message type.
     * Nimbus JOSE+JWT doesn't define this... maybe it's removed in later spec drafts?
     */
    private final static JOSEObjectType TYP_JWT = new JOSEObjectType("JWT");

    /**
     * Claims that can't be custom for a User.
     */
    private final static Set<String> RESERVED_CLAIMS;
    static {
        Set<String> n = new HashSet<String>();
        n.add("aud");
        n.add("dn");
        n.add("g");
        n.add("iss");
        n.add("sub");
        RESERVED_CLAIMS = Collections.unmodifiableSet(n);
    };

    /**
     * Claims that can't be custom for a Group.
     */
    private final static Set<String> GROUP_RESERVED_CLAIMS;
    static {
        Set<String> n = new HashSet<String>();
        n.add("dn");
        n.add("id");
        GROUP_RESERVED_CLAIMS = Collections.unmodifiableSet(n);
    };

    /**
     * The "aud" claim is always goinstant.net
     */
    private final static List<String> AUDIENCE;
    static {
        ArrayList<String> n = new ArrayList<String>(1);
        n.add(0,"goinstant.net");
        AUDIENCE = Collections.unmodifiableList(n);
    };

    /**
     * Default HS256 JWS header.
     */
    private final static JWSHeader DEFAULT_HEADER;
    static {
        DEFAULT_HEADER = makeHeader();
    };


    /**
     * Creates a Signer with a base64 or base64url encoded secret key.
     *
     * A single Signer can be re-used to sign additional tokens.
     *
     * @param secretKey your GoInstant application secret key
     *
     * @throws java.lang.IllegalArgumentException if the base64 is invalid or too short
     */
    public Signer(String secretKey) {
        byte[] binaryKey = parseKey(secretKey);
        if (binaryKey.length < 32) {
            throw new IllegalArgumentException(
                "secretKey is too short (must be >= 32 bytes after decoding)"
            );
        }

        this.hmac = new MACSigner(binaryKey);
    }

    /**
     * Parse a Base64 or Base64Url key into a byte array.
     * @param key base64 or base64url formatted key
     * @return decoded bytes
     */
    public static byte[] parseKey(String key) {
        Base64 parsed = new Base64(
            key.replace('-','+').replace('_','/') // convert from base64url
        );
        return parsed.decode();
    }

    /**
     * Create a signed JWT token for your GoInstant App.
     *
     * @param user the user to create a token for.
     *   See {@link PlainUser} if you don't want to implement your own.
     *
     * @throws IllegalArgumentException if the user or extraHeaders contain bad
     *   values or reserved custom properties.
     *
     * @return signed token if successful, null otherwise.
     */
    public String sign(User user) {
        return this.sign(user, null);
    }

    /**
     * Create a signed JWT token for your GoInstant App, with custom headers.
     *
     * @param user the user to create a token for.
     *   See {@link PlainUser} if you don't want to implement your own.
     *
     * @param extraHeaders a map of additional properties to include in the JWS header.
     *
     * @throws IllegalArgumentException if the user or extraHeaders contain bad
     *   values or reserved custom properties.
     *
     * @throws ClassCastException if one of the extra headers' value is of the
     *  wrong type for that key.
     *
     * @return signed token if successful, null otherwise.
     */
    public String sign(User user, Map<String,Object> extraHeaders) {
        try {
            JWSHeader header = (extraHeaders == null || extraHeaders.isEmpty())
                ? DEFAULT_HEADER
                : convertHeaders(extraHeaders);
            JWTClaimsSet claims = userToClaims(user);

            SignedJWT jwt = new SignedJWT(header, claims);
            jwt.sign(this.hmac);
            return jwt.serialize();

        } catch (JOSEException e) { // some crypto problem
            return null;
        }
    }

    private static void checkIdAndDn(String id, String displayName) {
        if (id == null || id.length() == 0)
            throw new IllegalArgumentException(
                "id must be a non-empty String");

        // optional, but must be non-empty if provided
        if (displayName != null && displayName.length() == 0)
            throw new IllegalArgumentException(
                "displayName must be a non-empty String");
    }

    /**
     * Converts a User and its Groups into a JWTClaimsSet.
     *
     * Groups are converted into a List so the List will get serialized as a
     * JSON Array, which GoInstant requires.
     */
    private static JWTClaimsSet userToClaims(User user) {
        JWTClaimsSet claims = new JWTClaimsSet();

        String id = user.getID();
        String displayName = user.getDisplayName();
        checkIdAndDn(id, displayName);

        String domain = user.getDomain();
        if (domain == null || domain.length() == 0)
            throw new IllegalArgumentException(
                "domain must be a non-empty String");

        claims.setAudience(AUDIENCE);
        claims.setSubject(id);
        claims.setIssuer(domain);
        claims.setCustomClaim("dn", displayName != null ? displayName : id);

        Map<String,Object> custom = user.getCustomClaims();
        for (Map.Entry<String,Object> entry : custom.entrySet()) {
            String key = entry.getKey();
            if (RESERVED_CLAIMS.contains(key)) {
                throw new IllegalArgumentException("The '"+key+"' claim cannot be custom for a User");
            }
            claims.setClaim(key, entry.getValue());
        }

        Set<Group> groups = user.getGroups();
        if (groups.size() > 0) {
            // Lists will get serialized as JSON Arrays
            ArrayList<Object> g = new ArrayList<Object>(groups.size());
            for (Group group : groups) {
                g.add(groupToMap(group));
            }
            claims.setCustomClaim("g", g);
        }

        return claims;
    }

    /**
     * Converts a Group into a generic Map for serialization.
     * Maps are serialized as JSON Objects.
     */
    private static Map<String,Object> groupToMap(Group group) {
        Map<String,Object> groupMap = new TreeMap<String,Object>(); // deterministic ordering

        String id = group.getID();
        String displayName = group.getDisplayName();
        checkIdAndDn(id, displayName);
        groupMap.put("id", id);
        groupMap.put("dn", displayName);

        Map<String,Object> custom = group.getCustomClaims();
        for (Map.Entry<String,Object> entry : custom.entrySet()) {
            String key = entry.getKey();
            if (GROUP_RESERVED_CLAIMS.contains(key)) {
                throw new IllegalArgumentException("The '"+key+"' claim cannot be custom for a Group");
            }
            groupMap.put(key, entry.getValue());
        }

        return groupMap;
    }

    /**
     * Construct a fresh HS256 JWS header.
     */
    private static JWSHeader makeHeader() {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        header.setType(TYP_JWT);
        return header;
    }

    /**
     * Attempt to assign custom headers.
     */
    private static JWSHeader convertHeaders(Map<String,Object> extraHeaders) {
        JWSHeader header = makeHeader();

        for (Map.Entry<String,Object> entry : extraHeaders.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();

            // XXX TODO: port this to Java 7 style switch:
            if (key.equals("alg") || key.equals("typ")) {
                break;
            } else if (key.equals("kid")) {
                header.setKeyID((String)val);
            } else if (key.equals("cty")) {
                header.setContentType((String)val);
            } else if (key.equals("jku")) {
                header.setJWKURL((URL)val);
            } else if (key.equals("jwk")) {
                header.setJWK((JWK)val);
            } else if (key.equals("x5u")) {
                header.setX509CertURL((URL)val);
            } else if (key.equals("x5t")) {
                header.setX509CertThumbprint((Base64URL)val);
            } else if (key.equals("x5c")) {
                @SuppressWarnings("unchecked") List<Base64> x5c = (List<Base64>)val;
                header.setX509CertChain(x5c);
            } else if (key.equals("crit")) {
                @SuppressWarnings("unchecked") Set<String> crit = (Set<String>)val;
                header.setCriticalHeaders(crit);
            } else {
                header.setCustomParameter(key, val);
            }
        }

        return header;
    }
}

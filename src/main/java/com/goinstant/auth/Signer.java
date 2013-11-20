package com.goinstant.auth;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.JOSEObjectType;


/**
 * Hello world!
 *
 */
public class Signer {
    private JWSSigner hmac;

    // don't know why this isn't in nimbusds... it's part of the spec (maybe
    // got removed in later drafts?)
    private static JOSEObjectType typJWT = new JOSEObjectType("JWT");

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
        Base64 parsed = new Base64(
            secretKey.replace('-','+').replace('_','/') // convert from base64url
        );

        byte[] binaryKey = parsed.decode();
        if (binaryKey.length < 32) {
            throw new IllegalArgumentException(
                "secretKey is too short (must be >= 32 bytes after decoding)"
            );
        }

        this.hmac = new MACSigner(binaryKey);
    }

    /**
     * Create a signed JWT token for your GoInstant App.
     *
     * The userData param is a Map to various properties.  The following
     * properties are required, additional properties are optional.
     *
     * <dl>
     *   <dt>id (String)</dt>
     *   <dd>Permanent identifier for this user</dd>
     *
     *   <dt>domain (String)</dt>
     *   <dd>The domain name in which the user and group IDs are defined (e.g. example.com)</dd>
     *
     *   <dt>displayName (String)</dt>
     *   <dd>The full name to display for this user</dd>
     * </dl>
     *
     * Of the optional properties, groups must be a
     * List&lt;Map&lt;String,Object&gt;&gt; with the following required properties:
     *
     * <dl>
     *   <dt>id</dt>
     *   <dd>Permanent identifier for this group</dd>
     *
     *   <dt>displayName</dt>
     *   <dd>name to display for this group</dd>
     * </dl>
     *
     * @param userData defines the properties for this user that should be
     * encoded in the token.
     *
     * @return signed JWT
     */
    public String sign(Map<String,Object> userData) {
        return this.sign(userData, null);
    }

    /**
     * Create and sign a JWT token for this userData.
     *
     * @param userData same as in {@link sign(java.util.Map<java.lang.String,java.lang.Object>)}
     *
     * @param extraHeaders items to add to the JWS header.
     *   See the {@link http://nimbusds.com/files/jose-jwt/javadoc/com/nimbusds/jose/JWSHeader.html Nimbus JOSE JWT docs}
     *   for acceptable names and the corresponding
     *   methods used to set them.
     *
     * @throws java.lang.ClassCastException if any of the extra headers cannot
     * be cast to the requisite type.
     * @throws java.lang.IllegalArgumentException if any of the required
     * userData parameters are missing.
     *
     * @return JWT String if successful, null otherwise.
     */
    public String sign(Map<String,Object> userData, Map<String,Object> extraHeaders) {
        JWTClaimsSet claims = new JWTClaimsSet();
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        if (extraHeaders != null) {
            assignJWSHeaders(header, extraHeaders);
        }
        header.setType(typJWT);

        assignJWTClaims(claims, userData);

        SignedJWT jwt = new SignedJWT(header, claims);
        try {
            jwt.sign(this.hmac);
            return jwt.serialize();
        } catch (IllegalStateException e) { // didn't sign properly
            return null;
        } catch (JOSEException e) { // other crypto problems
            return null;
        }
    }

    private static void assignJWTClaims(JWTClaimsSet claims, Map<String,Object> userData) {
        List<String> aud = new ArrayList<String>(1);
        aud.add(0,"goinstant.net");
        claims.setAudience(aud);
    }

    private static void assignJWSHeaders(JWSHeader header, Map<String,Object> extraHeaders) {
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
                @SuppressWarnings("unchecked")
                List<Base64> x5c = (List<Base64>)val;
                header.setX509CertChain(x5c);
            } else if (key.equals("crit")) {
                @SuppressWarnings("unchecked")
                Set<String> crit = (Set<String>)val;
                header.setCriticalHeaders(crit);
            } else {
                header.setCustomParameter(key, val);
            }
        }
    }
}

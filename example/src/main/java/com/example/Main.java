package com.example;

import java.lang.System;
import java.util.HashMap;
import java.util.HashSet;

import com.goinstant.auth.Signer;

// User and a convenient concrete implementation:
import com.goinstant.auth.User;
import com.goinstant.auth.PlainUser;

// Group and a convenient concrete implementation:
import com.goinstant.auth.Group;
import com.goinstant.auth.PlainGroup;

/**
 * Example application for creating a user token for GoInstant.
 *
 * Usage: java SimpleSigner (userId) (displayName)
 */
class Main {
  static final String MY_DOMAIN = "example.com"; // TODO: set this to your domain name

  public static void main(String[] args) {
    String secretKey = System.getenv("SECRET_KEY");
    if (secretKey == null) {
      // default to key from the goinstant-auth unit tests
      secretKey = "HKYdFdnezle2yrI2_Ph3cHz144bISk-cvuAbeAAA999";
    }

    // construct this signer just once:
    Signer signer = new Signer(secretKey);

    if (args.length < 2) {
      System.err.println("Usage: (jar) userId displayName");
      System.exit(1);
    }

    String userId = args[0];
    String email = userId + "@example.com";
    String displayName = args[1];

    // Groups are optional
    PlainGroup group = new PlainGroup("awesome","Awesome Folk");
    HashSet<Group> groups = new HashSet<Group>();
    groups.add(group);

    PlainUser user = new PlainUser(
      userId,       // permanent, unique ID of this user
      MY_DOMAIN,    // your domain name
      displayName,  // name to display for the user
      groups        // (optional) groups the user belongs to
    );

    // (optional) specify additional claims
    HashMap<String,Object> customClaims = new HashMap<String,Object>();
    customClaims.put("email", email);
    user.setCustomClaims(customClaims);

    // sign the token; embed this into your web-app
    String token = signer.sign(user);
    System.out.println(token);
    System.exit(0);
  }
}

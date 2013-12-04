# java-goinstant-auth

GoInstant Authentication for Your Java Application. [![Build Status](https://travis-ci.org/goinstant/java-goinstant-auth.png?branch=master)](https://travis-ci.org/goinstant/java-goinstant-auth)

This is an implementation of JWT tokens consistent with what's specified in the
[GoInstant Users and Authentication
Guide](https://developers.goinstant.com/v1/security_and_auth/guides/users_and_authentication.html).

This library is not intended as a general-use JWT library.  The awesome [Nimbus
JOSE+JWT](https://bitbucket.org/nimbusds/nimbus-jose-jwt) project is what this
module builds upon.

# Installation

Requirements:

- [java SE Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html) >= 7
- [Maven](https://maven.apache.org) >= 3.1.1

Add this to your `<dependencies>` section of `pom.xml`, or add it with your IDE:

```xml
 <dependency>
   <groupId>com.goinstant</groupId>
   <artifactId>goinstant-auth</artifactId>
   <version>1.0.4</version>
 </dependency>
```

Alternatively, you can download build artifacts [from our Sonatype OSS staging
area](http://oss.sonatype.org/content/groups/staging/com/goinstant/goinstant-auth/).

# Usage

(See the [`example` directory](./example/) for the complete source code).

First Construct a Signer with your goinstant application key. The application key
should be in base64url or base64 string format. To get your key, go to [your
goinstant dashboard](https://goinstant.com/dashboard) and click on your App.

**Remember, the Secret Key needs to be treated like a password!**
Never share it with your users!

```java
  static final String MY_DOMAIN = "example.com";

  public static void main(String[] args) {
    String secretKey = System.getenv("SECRET_KEY");
    // construct this Signer just once:
    Signer signer = new Signer(secretKey);
```

Then, construct a `com.goinstant.auth.User` instance (concrete convenience
class is `com.goinstant.auth.PlainUser`).  The `groups` parameter is optional,
the others are required.

```java
    String userId = args[0];
    String email = userId + "@example.com";
    String displayName = args[1];

    // groups are optional
    PlainGroup group = new PlainGroup("awesome","Awesome Folk");
    HashSet<Group> groups = new HashSet<Group>();
    groups.add(group);

    PlainUser user = new PlainUser(
      userId,       // permanent, unique ID of this user
      MY_DOMAIN,    // your domain name
      displayName,  // name to display for the user
      groups        // (optional) groups the user belongs to
    );
```

Optionally, you can add custom claims to the token (which will be visible in
your GoInstant client-side code).

```java
    HashMap<String,Object> customClaims = new HashMap<String,Object>();
    customClaims.put("email", email);
    user.setCustomClaims(customClaims);
```

Then, sign the token

```java
    String token = signer.sign(user);
    System.out.println(token);
    System.exit(0);
  }
```

# API

Javadocs are published to http://goinstant.github.io/java-goinstant-auth/apidocs/

Quick links:

- [Signer](http://goinstant.github.io/java-goinstant-auth/apidocs/com/goinstant/auth/Signer.html) - used to create tokens
- [User](http://goinstant.github.io/java-goinstant-auth/apidocs/com/goinstant/auth/User.html) interface defining what properties Signer expects a user to have
  - [PlainUser](http://goinstant.github.io/java-goinstant-auth/apidocs/com/goinstant/auth/PlainUser.html) is a concrete POJO
- [Group](http://goinstant.github.io/java-goinstant-auth/apidocs/com/goinstant/auth/Group.html) interface defining what properties Signer expects a group to have
  - [PlainGroup](http://goinstant.github.io/java-goinstant-auth/apidocs/com/goinstant/auth/PlainUser.html) is a concrete POJO

# Contributing

If you'd like to contribute to or modify java-goinstant-auth, here's a quick
guide to get you started.

## Development Dependencies

- [java SE Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html) >= 7
- [Maven](https://maven.apache.org) >= 3.1.1

## Set-Up

Download via GitHub:

```sh
git clone git@github.com:goinstant/java-goinstant-auth.git
cd java-goinstant-auth
mvn compile
```

## Testing

Testing is with the venerable [JUnit](http://junit.org/) framework.  Tests are
located under the `src/test/` directory.

To run the tests:

```sh
mvn test
```

## Publishing

Publishing is automated via maven and happens in two steps.  You'll need to
make sure pom.xml has the correct `<keyname>` or that your settings.xml has GPG
credentials.

### Snapshots

To stage a snapshot release (kinda like a pre-release, versions `X.Y.Z-SNAPSHOT`) run:

```sh
mvn clean deploy
```

### Staging

Before running maven, update this README so that the [Installing](#installing)
section references the new version number.

Running the command below will do the following:

1. modify the version in pom.xml to the release version
2. commit that
3. create a git tag for the release version
4. modify pom.xml to have the next `-SNAPSHOT` version
5. commit that
6. push these changes up to github

```sh
mvn clean release:prepare
```

If that's all good, then run the following to publish!

```sh
# publish to maven:
mvn release:perform

# publish javadocs to github.io via gh-pages:
mvn site
```

Then follow the [Releasing a Staging Repository](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-ReleasingaStagingRepository) instructions on the Sonatype Wiki.

In a nutshell:
- click Staging Repositories (left pane)
- find the new release (a "repository", looks like `comgoinstant-1000`)
- click Close (top pane)
- download and verify the artifacts (bottom pane)
- click Release (top pane) - ok to "drop automatically"

# Support

Email [GoInstant Support](mailto:support@goinstant.com) or stop by [#goinstant
on freenode](irc://irc.freenode.net#goinstant).

For responsible disclosures, email [GoInstant Security](mailto:security@goinstant.com).

To [file a bug](https://github.com/goinstant/java-goinstant-auth/issues) or
[propose a patch](https://github.com/goinstant/java-goinstant-auth/pulls),
please use github directly.

# Legal

&copy; 2013 GoInstant Inc., a salesforce.com company.  All Rights Reserved.

Licensed under the 3-clause BSD license

# Example App

This is a simple example application using [goinstant-auth for
Java](https://github.com/goinstant/java-goinstant-auth).

# How to Run

### Requirements

- Java JDK >= 6 (7 recommended)
- Maven >= 3.1

### Set Up

Edit the `pom.xml` file to have the latest version of goinstant-auth.

```xml
    <dependency>
      <groupId>com.goinstant</groupId>
      <artifactId>goinstant-auth</artifactId>
      <version>1.X.Y</version>
    </dependency>
```

Then, install this and other dependencies with:

```sh
  mvn install
```

### Building

```
  mvn clean compile package
```

### Running

Pass in a user ID and display name.

```sh
  java -jar ./target/simple-signer-*.jar '12345' 'Display Name'
```

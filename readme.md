# Cassandra Tweets

Simple Twitter Application with Cassandra

### Members

- 13512014 Muhammad Yafi
- 13512066 Calvin Sadewa

### Prerequisites

1. Install Java JDK 1.8
2. Install Gradle, add it to your PATH environment variables.
3. Ensure gradle can works by typing `gradle` in your command prompt.

### How To Build

1. Type `gradle client`
2. Run at build/libs: `java -jar client-1.0.jar`


### Available Commands

`register [username] [password]` : register the username and password to database

`login [username] [password]` : login with the given username and password

`follow [username]` : current user will follow [username]

`userline [username]` : get userline of [username]

`timeline [username]` : get timeline of [username]

`tweet [body]` : do tweet

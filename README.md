# Spring-Web-JWT

Adding JWT-based authorization to a Spring Web project is painful. Spring-Web-JWT provides a library
to include in your existing projects.

This top-level project is broken into two modules:

 * Spring-Web-JWT -- the library
 * Demo -- a demo that uses the library

A starter project for any Spring REST app that wants to do database-based JWT user authentication.

To use this project, you can either just copy it and do some basic renaming, or you can copy certain files into your
project. I'll list them once I know them.

This project will be far easier if you use IntelliJ IDEA for your IDE. Otherwise, you'll have to figure out the
appropriate Gradle commands yourself. (Maybe someone will help document that part.)

# To Run the Demo
To run the demo application, within IDEA:

 * Expand Gradle -- the Gradle icon in the far right border area.
 * Expand the pulldown for Spring-Web-JWT
 * Expand the pulldown for publishing
 * Execute publishToMavenLocal

This will do a build and then copy the build artifacts into your ~/.m2/repository maven repository. Now the
library is installed locally.

Next, the Demo application requires a local database. It's configured to expect PostgreSQL. If you use a different
database provider, you're on your own setting it up. If you're using PostgreSQL:

    createuser --password --createdb demo

This will prompt for a password. As configured, the password is demo, but we're going to update the config shortly.

    createdb -U demo demo

You can, of course, use a role and dbname other than demo.

    psql -U demo demo
    CREATE TABLE member (
        id serial,
        username text,
        password text,
        role text,
        created_at timestamp without time zone default now()
    );

Now it's time to update the configuration. Within Demo, open `src/main/resources/application.yml`. If you're to the point
of wanting a Spring Boot application that uses JWT authentication, one presumes you know how to change this configuration.

Note that my database server as a `/etc/hosts` entry of `dbserver`. Feel free to change that to `localhost` or whatever is
appropriate for your system. Change the username and password if you didn't use `demo:demo`. Change the database from
`demo` as appropriate.

`show-sql` makes Hibernate really chatty. Feel free to set if to false or remove those three lines entirely.

At that point, you can execute the Demo application. You should get a running REST application (with no HTML support)
on port 8080 (or override yourself in the run config).

This call requires no authentication.

    curl http://localhost:8080/ping

This call will seed the database with one administrative user and return the token you can use in subsequent calls.

    curl -u user:password http://localhost:8080/seed

Use an appropriate username and password. Here's a better example:

curl -u 'someone@nowhere.com:ThisIsMyPassword' http://localhost:8080/seed

If you have `jq` on your system, you can do something like this:

        AUTH=`curl -s -X POST -H "Content-Type: application/json" \
            -d '{"username": "Your Username", "password": "Your Password" }' \
            "http://localhost:8080/auth/authenticate" \
            | jq -r .token`

Otherwise just make it a simple curl command and cut & paste the token yourself.
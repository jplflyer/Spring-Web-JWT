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

# License
This project is distributed under the MIT License. See the attached LICENSE file.

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

# Adding to Your Project
Summary:

* Add the dependency to your `build.gradle` or `pom.xml` file.
* Setup your Member and MemberRepository. Standard Spring Data things.
    * Your Member entity should implement `UserDetails`. See below.
* Define a few Beans.
    * You can probably copy and paste `ApplicationConfig.java` below.

## Updating Maven or Gradle

Gradle:

    dependencies {
        ...
        implementation 'org.showpage:spring-web-jwt:1.0.0-SNAPSHOT'
    }
    repositories {
        mavenLocal()
        mavenCentral()
    }

Someone send me the maven equivalent, please.

This will change a tiny bit once we're publishing to Maven Central.

## Implement User Details Somehow
You will need some way of doing user lookups. I'm storing mine in PostgreSQL. You then need to create a bean. I have
a MemberRepository as follows:

    public interface MemberRepository extends JpaRepository<Member, Integer> {
        Optional<Member> findByUsername(String value);
    }

This is my Member entity:


    /**
     * One Member in the database.
     */
    @Entity
    @Data
    @Builder
    @AllArgsConstructor
    public class Member implements UserDetails {
        /**
         * Default constructor.
         */
        public Member() {
        }

        /** Primary Key */
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_jpa_sequence_generator")
        @SequenceGenerator(name = "member_jpa_sequence_generator", sequenceName = "member_id_seq", allocationSize = 1)
        Integer id;

        /** Username. We prefer email addresses. */
        String username;

        /** Encrypted password. */
        String password;

        /** User Role -- Admin, Member, et cetera. */
        @Enumerated(EnumType.STRING)
        UserRole role;

        /** When this user was first created. */
        Timestamp createdAt;

        //======================================================================
        // Methods from UserDetails. These are largely boilerplate. You can
        // make them complicated if there's a reason to do so.
        //======================================================================

        /**
         * Return the list of user roles for UserDetails. These are just a List
         * of strings (effectively) and can be used to secure portions of your
         * URL space based on role.
         *
         * @return The list of roles. We only support one role per user.
         */
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority(role.name()));
        }

        /**
         * Is this account non-expired?
         *
         * @return True normally. False if the account is marked expired.
         */
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        /**
         * Is this account locked?
         *
         * @return True normally. false if the account is marked locked.
         */
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        /**
         * Are the creds non-expired?
         *
         * @return True normally. False if the creds are expired.
         */
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        /**
         * Is the account enabled?
         *
         * @return True normally.
         */
        @Override
        public boolean isEnabled() {
            return true;
        }

You can use any class that implements `UserDetails`. Lombok provides my getter and setter with the `@Data` annotation.
Note the method `getAuthorities()`. I have an enum named `UserRole` that provides the `Member.role` field, but you can
just use strings or even hardcode something, if all your users are the same, or if you're going to handle user management
in another fashion.

## Define Some Beans

And then add something like this:

    @Configuration
    @RequiredArgsConstructor
    public class ApplicationConfig {
        /** We use a Member table. */
        private final MemberRepository memberRepository;

        private static final String[] WHITE_LIST_URL = {
            "/ping",
            "/seed",
            "/register",
            "/login"
        };

        /**
         * UserDetailsService is used by DaoAuthenticationProvider for retrieving a username,
         * a password, and other attributes for authenticating with a username and password.
         * Spring Security provides in-memory and JDBC implementations of UserDetailsService.
         *
         * @return  A simple lambda
         */
        @Bean
        public UserDetailsService userDetailsService() {
            return username -> memberRepository
                .findByUsername(username)
                .orElseThrow( () -> new UsernameNotFoundException("User not found") );
        }

        /**
         * This tells the default URL filter chain what URLs to whitelist.
         * @return The provider that hands it over.
         */
        @Bean
        public URLWhiteListProvider urlWhiteListProvider() {
            return () -> WHITE_LIST_URL;
        }
    }

The class can be called anything but must be marked with the `@Configuration` annotation or included in other
classes you have that do `@Configuration` of your beans. I use Project Lombok,
and the `@RequiredArgsConstructor` annotation gives me a constructor that handles any `private final` fields
(instead of using @Autowired).

So, what is all this. Well, MemberRepository is, of course, my repository. Do what makes sense for you.
This is just a Spring Data-JPA thing. If you have a different way of loading users, go for it.

I'll explain the String array shortly.

`userDetailsService()` returns a lambda that is used to get a user baseed on the username. It doesn't try to
authenticate the password. That happens later.

So, let's talk about the White List stuff. I may be going too far, but I've set up a security filter chain for me that
fits my needs. It has this line:

        private final URLWhiteListProvider urlWhiteListProvider;

And it expects to be able to get a string array from it. This is the list of URLs that should require no JWT token.
In this case, you can ping, seed, register, or login without a token, and all other calls will require a proper
JWT token passed like in the Authorization header as `Bearer YourToken`.

## Still To Do: The Signing Key
Right now, the signing key is hard-coded. In JWT, signing keys are any bytes. You can use anything. I provided
a bunch of text in my example. I'm going to update my demo to instead use a proper key. For now, what we've got
here works, but it's insecure if anyone knows you're using this project. Take a look at the last method of
JwtService.java if you want to understand why.

I'll fix it shortly.

# Contributing
I am fairly recently returned to Java. I may have gotten a few things wrong. Please feel free to tell me better ways
to do things.

The best thing you can do is to fork the project, make fixes and enhancements, and submit Pull Requests. I ask that
you honor my coding style. Please don't reformat my code just because you like it differently.

Note that I use a fair amount of white space. Code should be readable even to my old eyes.

Feel free to email my. My email is openly available via GitHub.


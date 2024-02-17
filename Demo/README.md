# Spring-Web-JWT

Adding JWT-based authorization to a Spring Web project is painful. Spring-Web-JWT provides a library
to include in your existing projects.

This top-level project is broken into two modules:

 * Spring-Web-JWT -- the library
 * Demo -- a demo that uses the library

A starter project for any Spring REST app that wants to do database-based JWT user authentication.

To use this project, you can either just copy it and do some basic renaming, or you can copy certain files into your
project. I'll list them once I know them.

# Spring Initializer
When creating this project, I used the Spring Initializer. I selected these additional packages:

* Project Lombok
* Spring Web
* Spring Data JPA
* Spring Security
* PostgreSQL

# How it Works
We're going to use a Security Filter Chain. We'll enable a Login REST call with no authentication and do it manually
in order to return a JWT to the caller.

All other calls will require the token itself, and we'll store the current user record in thread local storage to be
available as needed. The individual calls will not have to do anything about authentication but may need to code
authorization themselves. (Authentication is knowing who this is. Authorization is know what that person is
allowed to do.)

Hmm. I'll also add some role-based authorization, mostly as a demo.

# Setup
If you want to run this demo locally, one assumes you cloned it. Edit application.properties files to configure
your database info, right at the top.

For PostgreSQL, assuming it's running locally, you can do this:

    createuser --createdb --password demo

When prompted, give a password.

Then you can do this:

    psql -U demo demo
    create table member(id serial, username text, password text, role text, created_at timestamp default now());
    quit

After that, run this application and do something like this:

    curl -s "http://localhost:8080/seed?username=foo&password=bar"

This will seed the member table with one user with this username and password. It will only do so if there are no
members at all.

At this point, you can do this:

    curl -s -u foo:bar http://localhost:8080/login

This will return a JSON structure with your JWT inside. On my machine, I have a program called `jq` installed, and
I might do something like this:

    AUTH=`curl -s -u foo:bar http://localhost:8080/login | jq -r .Authentication`

After that, you can do something like:

    curl -s -H "Authentication: Bearer ${AUTH}" -H http://localhost:8080/test

This will also return JSON.

# References
It was tricky to find good info setting this up. This guy does a great job, although his
videos are long:

https://www.youtube.com/watch?v=KxqlJblhzfI

It's easy to find lots and lots of help. It's more difficult sorting through and finding modern
help. As of February, 2024, Spring Data version is 6.0, and so many tutorials are based on older
versions. The video linked I found by specifying Spring Security 6.
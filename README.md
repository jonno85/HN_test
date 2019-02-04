Spring boot service that uses some api endpoints from "https://github.com/HackerNews/API"
and serves the following 3 use cases;
1. Top 10 most occurring words in the titles of the last 25 stories
2. Top 10 most occurring words in the titles of the post of exactly the last week
3. Top 10 most occurring words in titles of the last 600 stories of users with at least 10.000 karma 


To build it and run, you must have maven installed on your path.
Go into the main folder and do:

mvn clean && mvn package && java -jar target/gs-spring-boot-0.1.0.jar

It will run a webserver responding on localhost:8080.
A simple web page let you reach the 3 endpoints directly from the browser;

 

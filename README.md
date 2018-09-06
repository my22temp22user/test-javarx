# test-javarx

Usage

Installation:
mvn clean install

Run the server:
java -jar target/test-javarx-1.0-SNAPSHOT.jar

Check number of event types (for example bar)
curl http://localhost:8080/service/test/count/events/type/bar

Check number of words in data (for example dolor)
curl http://localhost:8080/service/test/count/data/word/dolor

Things to improve:
Implement backpressure
Add more unit tests - test with finit predefined stream, failure recovery, etc
Use more constants and configuration
Test on other environments (currently tested only on Mac)
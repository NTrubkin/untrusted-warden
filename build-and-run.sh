mvn clean install -f demo-server/pom.xml
mvn clean install -f demo-client/pom.xml
mvn clean install -f demo/pom.xml
java -jar demo/target/demo-1.0-SNAPSHOT-jar-with-dependencies.jar

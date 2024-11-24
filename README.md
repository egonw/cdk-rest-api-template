CDK REST API template
---

An example of project that uses Chemistry Development Kit (CDK) and provides a REST API.

To start it in IDE, run [Starter](./src/main/java/cdkapi/Starter.java) and open your browser at http://localhost:8080/api/mw/CCC

To compile and start from the command line, run `mvn clean package` followed by `java -jar target/cdk-rest-template-1.0-SNAPSHOT-shaded.jar`.

Project structure:

- [CdkApi](./src/main/java/cdkapi/CdkApi.java) is where the actual API is
- [web-app-context.xml](./src/main/resources/web-app-context.xml) is the main app context where the beans can be defined
- [CdkApiTest](./src/test/java/cdkapi/CdkApiTest.java) to test your API

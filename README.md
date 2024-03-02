# Getting Started

## About Fraud Checker Service
### Address Verification Service (AVS)
AVS is an effective security measure to detect online fraud.
When customers purchase items, they need to provide their billing
address and ZIP code. An AVS will check if this address matches with
what the card issuing bank has on file.
Part of a card-not-present (CNP) transaction, the payment gateway
can send a request for user verification to the issuing bank.
The AVS responds with a code that would help the merchant understand
if the transaction is has a full AVS match.
If they don’t match, more investigation should be carried out by
checking the CVV (Card Verification Value), email address, IP address
on the transaction or allow your payment gateway to decline the
transaction.

### Card Verification Value (CVV)
The CVV (or Card Verification Code ) is the 3 or 4-digit code that
is on every credit card. The code should never be stored on the
merchant’s database. A CVV filter acts as an added security measure,
allowing only the cardholder to use the card since it is available
only on the printed card. If an order is placed on your website and
the CVV does not match, you should allow your payment gateway to
decline the transaction.  While making a card-not-present
transaction (online, email or telephone orders), merchant gets the
required card information from the customer to verify the transaction.
Friendly fraud, is a risk associated with CNP transactions, that can
lead to a chargeback. Enabling a CVV filter helps merchants fight
fraud and reduce chargebacks.

### Device Identification
Device identification analysis the computer rather than the person
who is visiting your website. It profiles the operating system,
internet connection and browser to gauge if the online transaction
has to be approved, flagged or declined. All devices (phones,
computers, tablets, etc) have a unique device fingerprint, similar
to the fingerprints of people, that helps identify fraudulent
patterns and assess risk if any.

## Develop/Debug 
### To Start Dev Loop 
1. In one Terminal ==> ```gradle bootRun -Dspring.profiles.active=development``` or to run on another port ```gradle bootRun -PjvmArgs="-Dserver.port=10001"```
    * To run a different profile at start-up, use ```gradle bootRun -Dspring.profiles.active=jenkins```.  If nothing is given, then the default, ```development``` profile is selected.
2. In the second Terminal ==> ```gradle -t test``` to run tests continuously.
3. In the IDE Terminal ==> 
    * To reload the latest classes in the JVM, use ```gradle compileJava```  
    * To reload the latest changes in static HTML files, use ```gradle reload```  

### To Run Specific Tests
1. Running a Test ==> ```gradle test --tests com.tsys.fraud_checker.FraudCheckerTest```.
2. Running Tagged Tests ==> 
    * Run tests having a tag and excluding another ```gradle taggedTest -DincludeTags='UnitTest' -DexcludeTags='End-To-End-Test'```.
    * Run tests that have both the tags StandAlone and UnitTest, and exclude ComponentTest - ```gradle taggedTest -DincludeTags='StandAlone & UnitTest' -DexcludeTags='ComponentTest'```.
    * Run tests that have either the StandAlone, or the ComponentTest tags and exclude End-To-End-Test - ```gradle taggedTest -DincludeTags='StandAlone | ComponentTest' -DexcludeTags='End-To-End-Test'```.
Note: you can put ```-i``` at the end of each to get further information while running the tests
    
### To Debug
* Using only Intellij IDE
    * Debugging is as simple as navigating to the class with the main method, right-clicking the triangle icon, and choosing Debug.
* Using another JVM process and Intellij IDE
    1. In one Terminal ==> ```gradle bootRun -Dserver.port=10001 -Dagentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000```
        * Understanding the Java Debug Args - By default, the JVM does not enable debugging. This is because:
          * It is an additional overhead inside the JVM. 
          * It can potentially be a security concern for apps that are in public.
          Hence debugging is only done during development and never on production systems.
          
          Before attaching a debugger, we first configure the JVM to allow debugging. 
          We do this by setting a command line argument for the JVM:
          ```-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000```
          
          * ```-agentlib:jdwp``` - Enable the Java Debug Wire Protocol (JDWP) agent inside the JVM. This is the main command line argument that enables debugging.
          * ```transport=dt_socket``` - Use a network socket for debug connections. Other options include Unix sockets and shared memory.
          * ```server=y``` - Listen for incoming debugger connections. When set to n, the process will try to connect to a debugger instead of waiting for incoming connections. Additional arguments are required when this is set to n.
          * ```suspend=n``` - Do not wait for a debug connection at startup. The application will start and run normally until a debugger is attached. When set to y, the process will not start until a debugger is attached.
          * ```address=8000``` - The network port that the JVM will listen for debug connections. Remember, this should be a port that is not already in use.
    2. On Intellij IDE ==> 
        * Open menu Run | Edit Configurations...
        * Click the + button and Select 'Remote' from Templates

## Building
### To Check Gradle Properties and tasks
1. Properties, use ```gradle :properties```
2. All Tasks, use ```gradle tasks --all```
3. Build, use ```gradle clean build```

### To Build Docker Images, Running and Stopping them using Gradle
1. Build Docker Image, use ```gradle docker```
2. To Push Docker Image to Docker Hub, use ```gradle dockerPush```
3. To Run Docker Image, use ```gradle dockerRun```
4. To Stop a running Docker Image, in another terminal, use ```gradle dockerStop```

### To Build Docker Images, Running and Stopping them using Command Line
1. Build Docker Image, use ```docker build -t dhavaldalal/fraud-checker-service:1.0.0 --build-arg BUILD_VERSION=1.0.0 .``` from within the directory where ```Dockerfile``` is present.
2. To Push Docker Image to Docker Hub, use ```gradle dockerPush```
3. To Run Docker Image by creating a named container ```fraud_checker```, use ```docker run --name fraud_checker --expose=8000 -p 8000:9001 dhavaldalal/fraud-checker-service:1.0.0``` and point the browser to ```http://localhost:8000```
4. To Stop a running named container, in another terminal, use ```docker stop fraud_checker``` and to start it again, use ```docker start fraud_checker```

### Getting inside Docker Image and Containers using Command Line
1. To get inside the image for inspecting, use ```docker run -it --rm --entrypoint /bin/bash dhavaldalal/fraud-checker-service:1.0.0```
2. To get inside the running container for inspecting, use ```docker exec -it <container_id> bash```.

NOTE: You may find out the container id using ```docker ps```.  Once inside the shell (either for image or container), do a ```ps -aux``` to see the running processes.  In the container listing you should see an additional ```java``` process running the ```.jar``` file.

### Removing Images and Containers using Command Line
1. To remove the image, use ```docker rmi dhavaldalal/fraud-checker-service:1.0.0```
2. To remove the named container, use ```docker rm fraud_checker```

### Pushing Images to Docker Hub using Command Line
1. First login using ```docker login```
2. To push images, use ```docker push dhavaldalal/fraud-checker-service:1.0.0```
NOTE: If you don't have an account on Docker Hub, please create one by visiting [https://hub.docker.com/](https://hub.docker.com/)

## Postman Collections
1. In your workspace, click ```Environments``` and import the development environment from [Fraud_Checker_Mervice_Development.postman_environment.json](src%2Ftest%2Fresources%2FFraud_Checker_Mervice_Development.postman_environment.json)
2. In your workspace, click ```Collections``` and import the url collections from [Fraud_Checker_Service.postman_collection.json](src%2Ftest%2Fresources%2FFraud_Checker_Service.postman_collection.json)

## Swagger Documentation
1. For Swagger UI point the browser to: [http://localhost:9001/swagger-ui/index.html](http://localhost:9001/swagger-ui/index.html) and 
2. To get the JSON version of the API docs, click on [http://localhost:9001/api-docs](http://localhost:9001/api-docs) 
3. To download the YAML version of the API docs, click on [http://localhost:9001/api-docs.yaml](http://localhost:9001/api-docs.yaml) 

### For Grouped Documentation
1. For Fraud Checker Specific API JSON docs, click [http://localhost:9001/api-docs/fraud-check](http://localhost:9001/api-docs/fraud-check)
2. For Validation API JSON docs on Fraud Checker, click [http://localhost:9001/api-docs/validation-apis](http://localhost:9001/api-docs/validation-apis)
3. For Setting up Fraud Checker for Testing JSON docs(In Development mode only), click [http://localhost:9001/api-docs/test-setup](http://localhost:9001/api-docs/test-setup) 

## Security Testing
### Getting 42 crunch token
1. Using Springfox, navigate to [http://localhost:9001/swagger-ui/index.html](http://localhost:9001/swagger-ui/index.html) and click on [http://localhost:9001/api-docs](http://localhost:9001/api-docs) to get the JSON version of the API docs 
2. In order to generate the YAML version, go to [https://editor.swagger.io](https://editor.swagger.io) and paste the above JSON file.  Go to Edit | Convert to YAML and save the file in YAML format.
3. Go to [https://platform.42crunch.com](https://platform.42crunch.com) and sign-up/sign-in
4. Click Settings | Api Tokens | Create New Token - <<your token>>
5.  

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.4.0-M3/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.4.0-M3/gradle-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.3.4.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)


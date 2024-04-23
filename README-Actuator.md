# Spring Boot Actuator

We know that monitoring a running application, gathering operational information like - its health, metrics, info, dump,
environment, and understanding traffic or the state of our database are needed to manage our application.  Traditionally
this was done using JMX beans.  But now, with Spring Boot Actuator we get these production-grade tools without actually
having to implement these features ourselves. Actuator is a sub-project of Spring Boot.  It uses HTTP endpoints or JMX
beans to enable us to interact with it.

## Add Actuator Dependency

```
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```

After adding the dependency, run the application and check the endpoint: ```http://localhost:9001/actuator``` or run
```curl localhost:9001/actuator```, you will see the following response.

```json
{
   "_links": {
      "self": {
         "href": "http://localhost:9001/actuator",
         "templated": false
      },
      "health-path": {
         "href": "http://localhost:9001/actuator/health/{*path}",
         "templated": true
      },
      "health": {
         "href": "http://localhost:9001/actuator/health",
         "templated": false
      }
   }
}
```
Spring Boot returns end-points in HATEOS (Hypermedia as the engine of application state) style i.e. adds a discovery endpoint that returns links to all available
actuator endpoints. This helps to discover other actuator endpoints and their corresponding URLs.


Now, you can check the health of the actuator, using ```http://localhost:9001/actuator/health``` or run
```curl localhost:9001/actuator/health```, you will see the following response:

```json
{
   "status": "UP"
}
```

## Add a corresponding Actuator test

Create ```com.tsys.fraud_checker.FraudCheckerActuatorTest``` and add a corresponding test within that ensures that the
Actuator is working:

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tags({
        @Tag("In-Process"),
        @Tag("ComponentTest")
})
public class FraudCheckerActuatorTest {

   @Autowired
   private TestRestTemplate client;

   @Test
   public void actuatorManagementEndpointWorks() {
      // Given-When
      final ResponseEntity<Map> response = client.getForEntity("/actuator", Map.class);

      // Then
      assertThat(response.getStatusCode(), is(HttpStatus.OK));
      assertTrue(response.getBody().containsKey("_links"));
   }
}
```

## Enable Other End-points or Exclusively select or Disable End-points
1. Now, the Actuator comes with most endpoints disabled.  In order to enable all the endpoints,
   in the ```application-development.properties``` file, add

   ```properties
   management.endpoints.web.exposure.include = *
   ```

2. To expose all enabled endpoints except one (e.g., /loggers), we use:

   ```properties
   management.endpoints.web.exposure.include = *
   management.endpoints.web.exposure.exclude = loggers
   ```

3. If you want selected features, provide a list:

   ```properties
   management.endpoints.web.exposure.include = health,env,info,beans,metrics,loggers
   ```

   Some of the URLs you see, for example in metrics section:
   ```json
       "metrics-requiredMetricName": {
           "href": "http://localhost:9001/actuator/metrics/{requiredMetricName}",
           "templated": true
       },
       "metrics": {
           "href": "http://localhost:9001/actuator/metrics",
           "templated": false
       }
   ```

   Go to Metrics and select a particular one and get the details on that metric.  For example, to get
   metric - JVM info, point the browser to:

   ```
   http://localhost:9001/actuator/metrics/jvm.info
   ```

   and You should see:

   ```json
   {
     "name": "jvm.info",
     "description": "JVM version info",
     "measurements": [
       {
         "statistic": "VALUE",
         "value": 1.0
       }
     ],
     "availableTags": [
       {
         "tag": "vendor",
         "values": [
           "Oracle Corporation"
         ]
       },
       {
         "tag": "runtime",
         "values": [
           "Java(TM) SE Runtime Environment"
         ]
       },
       {
         "tag": "version",
         "values": [
           "17.0.10+11-LTS-240"
         ]
       }
     ]
   }
   ```

4. Make sure for each of the Actuator features add a corresponding test in the
   ```com.tsys.fraud_checker.FraudCheckerActuatorTest``` class.  We have used the following Actuator features:

   1. env
   2. info
   3. beans
   4. metrics
   5. loggers
   6. health

   So we need 6 tests:

   ```java
     @Test
     public void actuatorHealthEndpointWorks() {
       // Given-When
       final ResponseEntity<Map> response = client.getForEntity("/actuator/health", Map.class);
   
       // Then
       assertThat(response.getStatusCode(), is(HttpStatus.OK));
     }
   
     @Test
     public void actuatorEnvironmentEndpointWorks() {
       // Given-When
       final ResponseEntity<Map> response = client.getForEntity("/actuator/env", Map.class);
   
       // Then
       assertThat(response.getStatusCode(), is(HttpStatus.OK));
     }
   
     @Test
     public void actuatorInfoEndpointWorks() {
       // Given-When
       final ResponseEntity<Map> response = client.getForEntity("/actuator/info", Map.class);
   
       // Then
       assertThat(response.getStatusCode(), is(HttpStatus.OK));
     }
     @Test
     public void actuatorBeansEndpointWorks() {
       // Given-When
       final ResponseEntity<Map> response = client.getForEntity("/actuator/beans", Map.class);
   
       // Then
       assertThat(response.getStatusCode(), is(HttpStatus.OK));
     }
   
     @Test
     public void actuatorMetricsEndpointWorks() {
       // Given-When
       final ResponseEntity<Map> response = client.getForEntity("/actuator/metrics", Map.class);
   
       // Then
       assertThat(response.getStatusCode(), is(HttpStatus.OK));
     }
   
     @Test
     public void actuatorLoggersEndpointWorks() {
       // Given-When
       final ResponseEntity<Map> response = client.getForEntity("/actuator/loggers", Map.class);
   
       // Then
       assertThat(response.getStatusCode(), is(HttpStatus.OK));
     }
   
   ```
## Enabling/Disabling Specific Endpoints
We can get a more fine-grained control for enabling or disabling the above configured endpoints.
1. Let us disable the health endpoint by adding:

   ```properties
   management.endpoint.health.enabled = false
   ```

   There is no need to touch the below property to exclude the
   web exposure.

   ```properties
   management.endpoints.web.exposure.include = health
   ```

   After restarting the application you should not see the health end-point in the HATEOS actuator GET call.  
   You may verify this by running the test and the corresponding health test should fail

2. Similar to above you may try the ```/info``` endpoint by adding:

   ```properties
   management.endpoint.info.enabled = false
   ```
   With the ```/info``` endpoint, you can additionally control the sub-information like
   build, git, java like this:

   ```properties
   # Does not show 'app' node
   management.info.env.enabled = false
   
   # Does not show 'java' node
   management.info.java.enabled = false  
   ```

3. Let's now add a ```/shutdown``` end-point and enable it.  For this add:

   ```properties
   management.endpoint.shutdown.enabled = true
   management.endpoints.web.exposure.include = shutdown
   ```

   As shutdown was not added earlier, we need to add the ```management.endpoints.web.exposure.include``` to add the
   shutdown feature.  Restart the application and you should see the HATEOS response containing the shutdown link:

   ```json
   "shutdown": {
     "href": "http://localhost:9001/actuator/shutdown",
     "templated": false
   }
   ```
   If you click use this as a GET request it won't work because this end-point accepts only POST request.  So, use
   Postman or use:

   ```shell
   curl -X POST http://localhost:9001/actuator/shutdown
   ```

   This will shut the application down!  In reality, this is not kept open in production like this.

## Change Management Port
Actuator defaults to running on the same port as the application. By adding in ```application-development.properties```
file, you can override that setting:

```properties
management.server.port: 10001
management.server.address: 127.0.0.1
```

## The ```/env``` Endpoint
If you point the browser to the ```/env``` endpoint on http://localhost:9001/actuator/env, you will see all the
enviromental details like:
* System Properties like - ```java.class.path```, ```file.separator```, ```user.dir```, ```PID``` etc...
* System Environment Variables like ```HOME```, ```PATH```, ```USER```, ```SHELL```, ```JAVA_HOME``` etc...
* All application properties
* Servlet Context Init Params
* Active Profiles
* If you have Spring devtools install, then all devtools properties

But, by default all the values are hidden.  In order to see these values, add the following property
in ```application.properties``` file, restart the app and point the browser to ```/env``` endpoint again.

```properties
# Allowed values are never, when-authorized, always
management.endpoint.env.show-values = ALWAYS
```


## The ```/loggers``` Endpoint
Access the ```/loggers``` endpoint at ```http://localhost:9001/actuator/loggers```.
You will see that it displays a list of all the configured loggers in the application
with their corresponding log levels. The details of an individual logger by passing the
logger name in the URL like this - ```http://localhost:9001/actuator/loggers/{name}```

For example, to get the details of the root logger, use the URL
http://localhost:9001/actuator/loggers/ROOT.

### Changing Log levels at runtime
Now, so far we are only able to view, the log-levels, But what if, the application is
facing some issue in production and we need to enable DEBUG logging for some time to
get more details about the issue.  Here is what we do -

For example, let's change the log level of the root logger to DEBUG at runtime,
make a POST request to the URL http://localhost:9001/actuator/loggers/ROOT with the payload:

```shell
curl -X 'POST' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
 'http://localhost:9001/actuator/loggers/ROOT' \
 -d '{ 
    "configuredLevel" : "DEBUG" 
  }' 
```

To check whether DEBUG is enabled or not, go to: http://localhost:9001/actuator/loggers/ROOT and you should see:

```json
{
  "configuredLevel": "DEBUG",
  "effectiveLevel": "DEBUG"
}
```

Likewise, you can change other loggers that are only specific to your package(s) by simply changing the name in
the above URL.

## The ```/flyway``` Endpoint
If you have Flyway in your application, then all you need to do do is simply add the following
to ```application.properties```:

```properties
management.endpoint.flyway.enabled = true
management.endpoints.web.exposure.include = ..., ..., flyway
```
Make sure that Flyway is also enabled in the ```application.properties```:
```properties
spring.flyway.enabled = true
```

Once you start the application and hit the http://localhost:9001/actuator/flyway, you will see
all the migrations.

**NOTE:** In case you get an error while starting the application that says -
```No Enum Constant found for UNDO_SQL```, then go to the backend ```schema_version``` table and
issue the following query:

```mysql-sql
DELETE FROM schema_version WHERE type = 'UNDO_SQL';
```
and then restart the application and hit the flyway actuator end-point.

## Configuring Endpoints
```management.endpoint.<name>``` prefix uniquely identifies the endpoint
that is being configured.

Each endpoint can be customized with properties using the format
```management.endpoint.<name>.<property to customize>```.

For Example:
```properties
management.endpoint.env.enabled = true
management.endpoint.env.show-values = WHEN_AUTHORIZED
management.endpoint.env.roles = admin
```

And finally to expose it over web, use the prefix
```management.endpoints.web.exposure.include = <name1>, <name2>, ...```

## Customizing the ```/info``` Endpoint
If you go to the ```http://localhost:9001/actuator/info```, you will find that it is empty.  This is where we can
customize this endpoint.  Let's include build and git details information of the project here:

NOTE: Below excerpt from: [https://reflectoring.io/spring-boot-info-endpoint/](https://reflectoring.io/spring-boot-info-endpoint/)

Spring collects useful application information from various ```InfoContributor``` beans defined in the application
context. Below is a summary of the default ```InfoContributor``` beans:

| ID	 | Bean Name	                 | Usage                                                                  |
|-------|----------------------------|------------------------------------------------------------------------|
| build | BuildInfoContributor	      | Exposes build information.                                             |
| env	 | EnvironmentInfoContributor | Exposes any property from the Environment whose name starts with info. |
| git	 | GitInfoContributor	      | Exposes Git related information.                                       |
| java	 | JavaInfoContributor	      | Exposes Java runtime information.                                      |
| os	 | OsInfoContributor	      | Exposes OS information.                                                |

By default, the ```env``` and ```java``` contributors are disabled.

1. Let us enable the java contributor by adding the following key-value pair
   in ```application-development.properties```:

   ```properties
   management.info.java.enabled = true
   ```

   And you should see:
   ```json
   "java": {
     "version": "17.0.10",
     "vendor": {
       "name": "Oracle Corporation"
     },
     "runtime": {
       "name": "Java(TM) SE Runtime Environment",
       "version": "17.0.10+11-LTS-240"
     },
     "jvm": {
       "name": "Java HotSpot(TM) 64-Bit Server VM",
       "vendor": "Oracle Corporation",
       "version": "17.0.10+11-LTS-240"
     }
   }
   ```

2. To display app info, we say ```management.info.env.enabled = true```.
   Also, Spring can pick up any app variable with a property name starting with info. To see this in action,
   let’s add the following properties in the ```application-development.properties``` file:

   ```properties
   # shows app info
   management.info.env.enabled = true
   # shows app info 
   info.app.website = https://tsys.co.in
   info.app.author = Dhaval Dalal
   info.app.microservice.name = Fraud Checker
   info.app.microservice.version = v1.0
   ```
   Hit the info url: ```http://localhost:9001/actuator/info``` and
   you should see something similar:

   ```json
   "app": {
     "author": "Dhaval Dalal",
       "microservice": {
         "name": "Fraud Checker",
         "version": "1.0"
       },
       "website": "https://tsys.co.in"
   },
   ```

3. Let us now add build info to this. Adding useful build information helps to identify the build artifact
   name, version, time created, etc... esp. useful for blue-green deployments.  Spring Boot allows to add this
   information using Maven or Gradle build plugins.  For this, we add the following build info block using the
   plugin DSL to ```build.gradle```:

   ```groovy
   springBoot {
       buildInfo {
           properties {
               // Custom properties
               additional = [
                       'jar': "$project.name-$project.version" + '.jar'
               ]
           }
       }
   }
   ```
   Running the ```./gradlew clean build``` task will generate ```build/resources/main/META-INF/build-info.properties```
   file with build info (derived from the project). Using the DSL we can customize existing values or add new
   properties as shown above.

   ```json
   "build": {
     "version": "1.0.0",
     "artifact": "fraud_checker_solution",
     "name": "fraud_checker_solution",
     "jar": "fraud_checker_solution-1.0.0.jar",
     "time": "2024-03-29T15:03:43.610Z",
     "group": "com.tsys"
   }
   ```

4. Let us now add Git info to this.  This is very useful to check if the relevant code present in production or the
   distributed deployments in different pods are in sync with expectations. Spring Boot can easily include Git
   properties in the Actuator endpoint using the Maven and Gradle plugins.  Using this plugin we can generate a
   ```git.properties``` file. The presence of this file automatically configures the ```GitProperties``` bean that
   is used by the ```GitInfoContributor``` bean to collate relevant information. By default, the following information
   will be exposed:

   ```properties
   git.branch
   git.commit.id
   git.commit.time
   ```

   In the ```build.gradle``` we  add the gradle-git-properties plugin:

   ```groovy
   plugins {
     id 'com.gorylenko.gradle-git-properties' version '2.4.1'
   }
   ```

   Let’s build the project ```./gradlew clean build```. We can see ```build/resources/main/git.properties``` file is
   created. And, the actuator info endpoint will display the same data:

   ```json
   "git": {
     "branch": "main",
     "commit": {
       "id": "3f1dc27",
       "time": "2024-03-27T12:32:08Z"
     }
   }
   ```

   The following management application properties control the Git related information:
   Let's add them to ```application-development.properties```

   | Application Property	                     | Purpose                                                      |
      |---------------------------------------------|--------------------------------------------------------------|
   | ```management.info.git.enabled = false```   | Disables the Git information entirely from the info endpoint |
   | ```management.info.git.mode = full```	     | Displays all the properties from the git.properties file     |

   Now, point the browser to the ```/info``` endpoint and it will show you full Git information:

   ```json
   "git": {
       "branch": "main",
       "commit": {
         "time": "2024-04-05T14:33:35Z",
         "message": { ... },
         "id": { ... },
         "user": { ... }
       },
       "build": {
         "version": "0.0.1",
         "user": {...},
       },
       "dirty": "true",
       "tags": "",
       "total": { ... },
       "closest": { ... },
       "remote": {...}
   }
   ```

   This plugin too provides multiple ways to configure the output using the attribute ```gitProperties```. For example,
   let’s limit the keys to be present by adding below to ```build.gradle```:

   ```groovy
   gitProperties {
       keys = [
               'git.branch',
               'git.commit.id.full',
               'git.commit.time',
               'git.commit.message.short',
               'git.commit.user.name',
               'git.total.commit.count',
       ]
   }
   ```
   We add the following tests for the above parts added to the ```/info``` endpoint:

   ```java
   @ExtendWith(SpringExtension.class)
   @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
   @Tags({
           @Tag("In-Process"),
           @Tag("ComponentTest")
   })
   public class FraudCheckerActuatorTest {
   
     ...
     ...
     ...
      
     @Test
     public void actuatorInfoEndpointHasAppInfo() {
       // Given-When
       final ResponseEntity<Map> response = client.getForEntity("/actuator/info", Map.class);
   
       // Then
       assertTrue(response.getBody().containsKey("app"));
     }
   
     @Test
     public void actuatorInfoEndpointHasBuildInfo() {
       // Given-When
       final ResponseEntity<Map> response = client.getForEntity("/actuator/info", Map.class);
   
       // Then
       assertThat(response.getStatusCode(), is(HttpStatus.OK));
       assertTrue(response.getBody().containsKey("build"));
     }
   
   
     @Test
     public void actuatorInfoEndpointHasGitInfo() {
       // Given-When
       final ResponseEntity<Map> response = client.getForEntity("/actuator/info", Map.class);
   
       // Then
       assertTrue(response.getBody().containsKey("git"));
     }
   
   }
   ```
5. Add an 'OS' contribution to this ```/info``` endpoint and write a corresponding test for it.

### Problem Statement
Write a ```DockerInfoContributor``` that shows the version of the engine on which Docker is running and the
port, volume mapped information.


## Customizing the ```/metrics``` Endpoint
We know that ```/metrics``` endpoint publishes information about OS and JVM as well as application-level metrics.

Spring Boot 2.0 uses Micrometer library for collecting metrics from JVM-based applications like Spring-based
application. Micrometer is now a part of the Actuator’s dependencies, so we should be good to go as long as the Actuator dependency
is in the classpath.

![SpringBoot-Actuator+Micrometer.png](images%2FSpringBoot-Actuator%2BMicrometer.png)

It converts these metrics in a format acceptable by the monitoring tools.  Micrometer is a facade between
application metrics and the metrics infrastructure developed by different monitoring/observability systems like Prometheus,
New Relic, Amazon Cloud Watch, Elastic etc... Think it to be like SLF4J, but for observability.

**Micrometer Concepts**

Let us look at the abstractions Micrometer provides:
* ```Meter``` - is the interface for collecting a set of measurements or metrics about the application.
* ```MeterRegistry``` Meters in Micrometer are created from and held in a MeterRegistry.
  Each supported monitoring system has an implementation of ```MeterRegistry```.
* ```SimpleMeterRegistry``` - it holds the latest value of each meter in memory and does not export the data anywhere.
  It is autowired in a Spring Boot application.
* ```@Timed``` - An annotation that frameworks can use to add timing support to either specific types
  of end-point methods that serve web request or to all methods.


In order to gather custom metrics, we have support for:
* ```Counter``` - a counter is a cumulative metric that represents a single value that can only be
  incremented/decremented or be reset to zero on restart, e.g. how many times feature A has been used.
  They are most often queried using the ```rate()``` function to view how often an event occurs over
  a given time period.

* ```Gauge``` - a gauge is a metric that represents a single numerical snapshot of data that can arbitrarily
  go up and down, e.g. how many users are logged-in currently or CPU utilization.

* ```Timer``` - to measure time. There are also histograms and summaries e.g. how much time does request to an
  endpoint take on average.  Histograms show the distribution of observations and putting those observations
  into pre-defined buckets. They are highly performant, and values can be accurately aggregated across both
  windows of time and across numerous time series. Note that both quantile and percentile calculations are
  done on the server side at query time.  Summaries measure latencies and are best used where an accurate
  latency value is desired without configuration of histogram buckets. They are limited as they cannot
  accurately perform aggregations or averages across quantiles and can be costly in terms of required resources.
  Calculations are done on the application or service client side at metric collection time.


We now implement custom metrics into the ```/metrics``` end-point.

Let's say we want to count how many times ```/ping``` was called.  For that matter
of fact you can choose any endpoint available in the application like the
login to log failed and successful attempts, count the number of downloads etc...

For us the ```FraudCheckerController``` serves the ```/ping``` end-point and to this we need to add the ```Counter```
such that we increment it each time this endpoint is called.

```java
@RestController
@RequestMapping("/")
public class FraudCheckerController {

   public String index() {
      return "index.html";
   }

   @GetMapping(value = "ping", produces = "application/json")
   public ResponseEntity<String> pong() {
      return ResponseEntity.ok(String.format("{ \"PONG\" : \"%s is running fine!\" }", getClass().getSimpleName()));
   }
}

```

But before we add the Counter to this code, we need to make sure that the Counter is created. For this let us create
```MetricsConfig```

```java
package com.tsys.springflyway.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

  @Bean
  public Counter makePingCounter(MeterRegistry meterRegistry) {
    return Counter.builder("api.ping.get")
        .description("a number of requests to /ping endpoint")
        .register(meterRegistry);
  }
}
```

Now, we need to modify the Controller class to inject this Counter and then
modify the ```pong()``` method to increment the counter value.

```java
@RestController
@RequestMapping("/")
public class FraudCheckerController {

  ...
  ...
   
  private Counter pingCounter;

  @Autowired
  public FraudCheckerController(VerificationService verificationService, Counter pingCounter) {
    this.verificationService = verificationService;
    this.pingCounter = pingCounter;
  }

   ...
   ...

   @GetMapping(value = "ping", produces = "application/json")
   public ResponseEntity<String> pong() {
      pingCounter.increment();
      return ResponseEntity.ok(String.format("{ \"PONG\" : \"%s is running fine!\" }", getClass().getSimpleName()));
   }
   
   ...
   ...
}
```

and we modify the ControllerTest to:

```java
public class FraudCheckerTest {

   ...
   ...
   ...

   @Test
   @Order(1)
   public void health() {
      // Given-When
      final ResponseEntity<String> response = client.getForEntity("/ping", String.class);

      // Then
      assertThat(response.getStatusCode(), is(HttpStatus.OK));
      assertThat(response.getBody(), is("{ \"PONG\" : \"FraudCheckerController is running fine!\" }"));
   }

   @Test
   @Order(2)
   public void incrementsPingCountByOneWhenAPingRequestIsMade() {
      // When
      client.getForEntity("/ping", String.class);

      // Then
      final ResponseEntity<Map> response = client.getForEntity("/actuator/metrics/api.ping.get", Map.class);
      final List<Map<String, ?>> measurements = (List<Map<String, ?>>) response.getBody().get("measurements");
      final double api_ping_get = (double) measurements.get(0).get("value");
      assertThat(api_ping_get, is(2.0d));
   }
}
```

Note that we have Ordered the tests as the ```/ping``` request is invoked twice from this test and hence we make the
results deterministic.

Now, restart the application and go to the http://localhost:9001/actuator/metrics/api.ping.get
end-point and you should see this:

```json

  "name": "api.ping.get",
  "description": "a number of requests to /ping endpoint",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 0.0
    }
  ],
  "availableTags": [
    
  ]
}
```

Now, make a ping request - http://localhost:9001/ping and
revisit http://localhost:9001/actuator/metrics/api.ping.get and you should see this:

```json

  "name": "api.ping.get",
  "description": "a number of requests to /ping endpoint",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 1.0
    }
  ],
  "availableTags": [
    
  ]
}
```

Let us now add two more meters -
* A Gauge for measuring the latest overall Fraud Status for a credit card check in the system.
* Time taken by the Fraud Check API to do the Check.

```java
@Controller
@Validated
@RequestMapping("/")
public class FraudCheckerController {

   ...
   ...
   
   private final MeterRegistry meterRegistry;
   private final AtomicInteger latestFraudStatus = new AtomicInteger(0);

   @Autowired
   public FraudCheckerController(VerificationService verificationService, Parser userAgentParser, Counter pingCounter, MeterRegistry meterRegistry) {
      this.verificationService = verificationService;
      this.userAgentParser = userAgentParser;
      this.pingCounter = pingCounter;
      this.meterRegistry = meterRegistry;
      Gauge.builder("api.check.fraudstatus_latest", () -> latestFraudStatus)
              .description("latest status of fraud check")
              .register(meterRegistry);

   }
   
   ...
   ...

   @PostMapping(value = "check", consumes = "application/json", produces = "application/json")
   public ResponseEntity<FraudStatus> checkFraud(
           @RequestBody @Valid FraudCheckPayload payload,
           HttpServletRequest request) {

      Timer.Sample timer = Timer.start(meterRegistry);
      final var userAgent = Optional.ofNullable(request.getHeader("User-Agent"));
      final var deviceIdentity = userAgent
              .map(ua -> {
                 final Client client = userAgentParser.parse(ua);
                 return new DeviceIdentity(client.device, client.os, client.userAgent);
              })
              .orElse(DeviceIdentity.UNKNOWN);

      try {
         LOG.info(() -> String.format("{ 'checkFraud' : ' for chargedAmount %s on %s with User Agent %s and the DeviceIdentity is %s'}", payload.charge, payload.creditCard, userAgent, deviceIdentity));
         FraudStatus fraudStatus = verificationService.verifyTransactionAuthenticity(payload.creditCard, payload.charge, deviceIdentity);
         recordLastestFraudStatus(fraudStatus);

         timer.stop(Timer.builder("api.check.fraudstatus.execution_time")
                 .description("Time taken to do a fraud check for a card holder")
                 .publishPercentiles(0.25, 0.50, 0.75, 0.95)
                 .percentilePrecision(2)
                 .publishPercentileHistogram(true)
                 .register(meterRegistry));

         LOG.info(() -> String.format("{ 'FraudStatus' : '%s'}", fraudStatus));
         final var httpHeaders = new HttpHeaders() {{
            setContentType(MediaType.APPLICATION_JSON);
         }};

         return new ResponseEntity<>(fraudStatus, httpHeaders, HttpStatus.OK);
      } catch (InterruptedException e) {
         return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   private void recordLastestFraudStatus(FraudStatus fraudStatus) {
      int currentStatus = switch (fraudStatus.overall) {
         case FraudStatus.PASS -> 0;
         case FraudStatus.FAIL -> -1;
         case FraudStatus.SUSPICIOUS -> 1;
         default -> -1;
      };
      latestFraudStatus.set(currentStatus);
   }
}
```

and we add a corresponding ```FraudCheckerControllerMetricsTest``` would be:

```java
package com.tsys.fraud_checker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tags({
    @Tag("In-Process"),
    @Tag("ComponentTest")
})
public class FraudCheckerControllerMetricsTest {

  private final Money chargedAmount = new Money(Currency.getInstance("INR"), 1235.45d);
  private final CreditCard validCard = CreditCardBuilder.make()
      .withHolder("Jumping Jack")
      .withIssuingBank("Bank of Test")
      .withValidNumber()
      .withValidCVV()
      .withFutureExpiryDate()
      .build();

  private static final int CVV_STATUS_PASS = 0;
  private static final int ADDRESS_VERIFICATION_STATUS_PASS = 0;
  private static final int DEVICE_IDENTIFICATION_STATUS_PASS = 0;

  @MockBean
  private Random random;

  @Autowired
  private FraudCheckerController fraudCheckerController;

  @Autowired
  private TestRestTemplate client;

  @Test
  public void chargingAValidCardCapturesTheLatestOverallStatusMetric() throws Exception {
    // Given
    final FraudStatus fraudStatus = givenASuccessfulFraudCheckRequestIsMade();
    final String metricName = "api.check.fraudstatus_latest";

    // When
    final ResponseEntity<Map> apiCheckLatestFraudStatusMetric = client.getForEntity(String.format("/actuator/metrics/%s", metricName), Map.class);

    // Then
    assertThatResponseIs200OK(apiCheckLatestFraudStatusMetric);

    final Map metric = apiCheckLatestFraudStatusMetric.getBody();
    System.out.println("metric = " + metric);
    assertThat(metric.get("name"), is(metricName));
    assertThat(metric.get("description"), is("latest status of fraud check"));
    final List<Map<String, ?>> measurements = (List<Map<String, ?>>) metric.get("measurements");
    final Map<String, ?> totalUsersValue = measurements.get(0);
    assertThat(totalUsersValue.get("value"), is(toValue(fraudStatus)));
  }

  @Test
  public void chargingAValidCardCapturesTimeTakenByTheRequestToRespondMetric() {
    // Given
    final FraudStatus fraudStatus = givenASuccessfulFraudCheckRequestIsMade();
    final String metricName = "api.check.fraudstatus.execution_time";

    // When
    final ResponseEntity<Map> executionTimeMetric = client.getForEntity(String.format("/actuator/metrics/%s", metricName), Map.class);

    // Then
    assertThatResponseIs200OK(executionTimeMetric);

    final Map metric = executionTimeMetric.getBody();
    assertThat(metric.get("name"), is(metricName));
    assertThat(metric.get("description"), is("Time taken to do a fraud check for a card holder"));
    assertThat(metric.get("baseUnit"), is("seconds"));
    final List<Map<String, ?>> measurements = (List<Map<String, ?>>) metric.get("measurements");
    final Map<String, ?> callCount = measurements.get(0);
    assertThat(callCount.get("value"), is(2.0d));
    final Map<String, Double> totalTime = (Map<String, Double>) measurements.get(1);
    assertTrue(totalTime.get("value") > 0d);
    final Map<String, Double> maxTime = (Map<String, Double>) measurements.get(2);
    assertTrue(maxTime.get("value") > 0d);
  }

  private void assertThatResponseIs200OK(ResponseEntity<Map> response) {
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getHeaders().getContentType(), is(new MediaType("application", "json")));
  }

  private double toValue(FraudStatus fraudStatus) {
    return switch (fraudStatus.overall) {
      case FraudStatus.PASS -> 0;
      case FraudStatus.FAIL -> -1;
      case FraudStatus.SUSPICIOUS -> 1;
      default -> -1;
    };
  }

  private FraudStatus givenASuccessfulFraudCheckRequestIsMade() {
    given(random.nextInt(anyInt()))
        .willReturn(-2000) // for sleepMillis
        .willReturn(CVV_STATUS_PASS)
        .willReturn(ADDRESS_VERIFICATION_STATUS_PASS)
        .willReturn(DEVICE_IDENTIFICATION_STATUS_PASS);

    HttpHeaders headers = new HttpHeaders();
    headers.set("User-Agent", "Galaxy M5 (Android; ARM Linux Android 14_0_0_r29) Chrome/119.0.0.0");
    HttpEntity<FraudCheckPayload> payload = new HttpEntity<>(new FraudCheckPayload(validCard, chargedAmount), headers);
    final ResponseEntity<FraudStatus> fraudStatusResponse = client.postForEntity("/check", payload, FraudStatus.class);
    final FraudStatus fraudStatus = fraudStatusResponse.getBody();
    return fraudStatus;
  }
}
```

Now, it may not be convenient to introduce timers everywhere in the code and then to gather the stats for
performance tuning strategy.  Micrometer comes with ```@Timed``` annotation that we use to add timing support
to either specific types or to end-point methods that serve web request or to all methods.

For the ```@Timed``` annotation to work, we need to introduce in ```MetricsConfig``` a ```TimedAspect``` bean
as shown below:

```java
public class MetricsConfig {

  ...
  ...
   
  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }
}
```

Make sure you have the below dependency in ```build.gradle``` for the above ```TimedAspect``` to compile:

```groovy
dependencies {
   implementation 'org.springframework:spring-aspects'
}
```

Now, let us add the following ```@Timed``` annotation to ```UserController``` methods:

```java
public class FraudCheckerController { 
   ...
   ...

   @Timed(value = "api.fraudcheck.validatePathVariable", description = "time taken to do a get validation of path variable", histogram = true, percentiles = {0.95, 0.75, 0.5, 0.25})
   ResponseEntity<String> validatePathVariable(
           @PathVariable("id")
           @Min(value = 5, message = "A minimum value of 5 is required")
           @Max(value = 9999, message = "A maximum value of 9999 can be given")
           @Parameter(name = "id",
                   schema = @Schema(implementation = Integer.class),
                   description = "Value must be between 5 and 9999 (inclusive)",
                   example = "1",
                   required = true)
           int id) {
      ...
   }

   ...
   ...

   
   @PostMapping("validateHeaderParameterUsingPost")
   @Timed(value = "api.fraudcheck.validateHeaderParamUsingPost", description = "time taken to do a post validation for header parameter", histogram = true, percentiles = {0.95, 0.75, 0.5, 0.25})
   ResponseEntity<String> validateHeaderParameterUsingPost(
           @RequestHeader(value = "param")
           @Min(5) @Max(9999) int param,
           @RequestBody @Valid FraudCheckPayload fraudCheckPayload) {
      ...
   }
   
   ...
   ...
}
```

Also add the corresponding timer tests in ```UserControllerSpecs```

```java
public class FraudCheckerControllerMetricsTest {
  
   ...
   ...

   @Test
   public void validatingHeaderParameterUsingPostCapturesTimeTakenByTheRequestToCompleteMetric() {
      // Given
      HttpHeaders headers = new HttpHeaders() {{
         set("User-Agent", "Galaxy M5 (Android; ARM Linux Android 14_0_0_r29) Chrome/119.0.0.0");
         set("param", "50");
      }};
      HttpEntity<FraudCheckPayload> payload = new HttpEntity<>(new FraudCheckPayload(validCard, chargedAmount), headers);
      client.postForEntity("/validateHeaderParameterUsingPost", payload, String.class);
      final String metricName = "api.fraudcheck.validateHeaderParamUsingPost";

      // When
      final ResponseEntity<Map> metricResponse = client.getForEntity(String.format("/actuator/metrics/%s", metricName), Map.class);

      // Then
      assertThatResponseIs200OK(metricResponse);

      final Map metric = metricResponse.getBody();
      assertThat(metric.get("name"), is(metricName));
      assertThat(metric.get("description"), is("time taken to do a post validation for header parameter"));
      final List<Map<String, ?>> measurements = (List<Map<String, ?>>) metric.get("measurements");
      final Map<String, ?> time = measurements.get(0);
      assertTrue((double) time.get("value") >= 1.0d);
   }

   @Test
   public void validatingAPathVariableCapturesTimeTakenByTheRequestToCompleteMetric() {
      // Given
      client.getForEntity("/validatePathVariable/500", String.class);
      final String metricName = "api.fraudcheck.validatePathVariable";

      // When
      final ResponseEntity<Map> metricResponse = client.getForEntity(String.format("/actuator/metrics/%s", metricName), Map.class);

      // Then
      assertThatResponseIs200OK(metricResponse);

      final Map metric = metricResponse.getBody();
      assertThat(metric.get("name"), is(metricName));
      assertThat(metric.get("description"), is("time taken to do a get validation of path variable"));
      final List<Map<String, ?>> measurements = (List<Map<String, ?>>) metric.get("measurements");
      System.out.println("measurements = " + measurements);
      final Map<String, ?> totalUsersValue = measurements.get(0);
      assertThat(totalUsersValue.get("value"), is(1.0d));
   }

   ...
   ...
}
```

After getting a green bar, lets start the Application and make sure you create a few users
and update a few as well and see what all we get at the http://localhost:9001/actuator/metrics endpoint.  
You will see something similar:

```json
{
   "names": [
      "api.check.fraudstatus_latest",
      "api.fraudcheck.validateHeaderParamUsingPost",
      "api.fraudcheck.validatePathVariable",
      "api.ping.get",      
      ...
   ]
}
```

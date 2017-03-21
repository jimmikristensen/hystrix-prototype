# Quick Start

If this is the first time you set up this prototype, I encourage you to skip the quick start and read the description [below](#Hystrixh-prototype).

1) __Start Mountebank:__

```bash
docker run -p 2525:2525 -p 4545:4545 -p 4546:4546 -d jkris/mountebank:latest --allowInjection
```
2) __Configure Mountebank imposters:__

```bash
cd mountebank_config/curl
curl -X PUT -d @initial_setup.json http://localhost:2525/imposters
```

3) __Start the Hystrix Dashboard:__

```bash
docker run -d -p 8081:9002 --name hystrix-dashboard mlabouardy/hystrix-dashboard:latest
```

4) __Start the prototype application:__

```bash
cd Prototype
./gradlew jettyRun
```

5) __Monitor circuit health through Hystrix Dashboard at http://localhost:8081/hystrix__

Add the hystrix stream: http://<ip_address_of_host_machine>:8080/HystrixPrototype/hystrix.stream

6) __Putting load on the prototype:__

```bash
ab -n 2000 -c 10 http://localhost:8080/HystrixPrototype/v1/hello
```

Watch the Dashboard to see the requests going through. Go to [Simulating slow response from the DOC](#simulating-slow-response-from-the-doc) for further testing.

# Hystrix Prototype

The purpose of this prototype is to gain experience with Hystrix as a circuit breaker pattern in distributed architecture. We achieve this by:

1. Wrapping HTTP calls to downstream services in Hystrix commands.
2. Using Mountebank to control latency of downstream services.
3. Using JMeter or Apache Benchmark to put load on the prototype.

The prototype is a very basic implementation of a web application, which uses Hystrix Commands to wrap HTTP requests. The web server exposes Hystrix metrics which can be used for monitoring the HTTP requests and the state of the circuit breaker in a Hystrix Dashboard.

The Hystrix commands will execute 2 HTTP GET requests to two different URLs, which should be controlled by Mountebank and can be manipulated by sending HTTP requests to the Mountebank server.

The following sections will explain the setup of the specific components.

* [Setting up and configuring Mountebank](#setting-up-and-configuring-mountebank)
* [Setting up Hystrix Dashboard](#setting-up-hystrix-dashboard)
* [Import the Prototype in Eclipse](#import-the-prototype-in-eclipse)
* [Running the prototype Java application](#running-the-prototype-java-application)
* [Putting load on the prototype application](#putting-load-on-the-prototype-application)
* [Project Overview](#project-overview)

Find links to the resources used in this manual in the [references section](#references)

## Prerequisites

* [Eclipse](#references) with [Jetty](#references) or Tomcat support.
* [Docker](#references) for running [Mountebank](#references) and [Hystrix](#references) Dashboard.
* [CURL](#references) or [Postman](#references) for configuring Mountebank.
* [JMeter](#references) or [Apache Benchmark](#references) for putting load on the prototype.

This guide assumes that the developer has knowledge about eclipse and project handling.

## Setting up and configuring Mountebank:

A docker image can be used for setting up the Mountebank server:

```bash
docker run -p 2525:2525 -p 4545:4545 -p 4546:4546 -d jkris/mountebank:latest --allowInjection
```

Access the mountebank server at: [http://localhost:2525](http://localhost:2525)

__Use Postman or cURL to configure Mountebank as described below:__

### Configure using Postman

* Open Postman and import the Postman collection:
	* Click the _Import_ button located in the upper left side of the window and import the collection located here:
		* __mountebank_config/postman/HystrixPrototype.postman_collection__
	* You should now see _5 imported_ HTTP requests
* Send the request named __[Stub] Initial setup__ to setup Mountebank the first time

### Or configure using cURL

Open a terminal in the root directory of the project and run the following commands:

```bash
cd mountebank_config/curl
curl -X PUT -d @initial_setup.json http://localhost:2525/imposters
```

### Verify the stubs are running

You should now have created two stubs on ports 4545 and 4546:
* Quotes service stub: [http://localhost:4545/quotes](http://localhost:4545/quotes)
* Time service stub: [http://localhost:4546/time](http://localhost:4546/time)

## Setting up Hystrix Dashboard

Install a Hystrix Dashboard by executing following docker command:

```bash
docker run -d -p 8081:9002 --name hystrix-dashboard mlabouardy/hystrix-dashboard:latest
```

Find more information about the Hystrix Dashboard in the [References section](#references).

Verify that the hystrix dashboard works by accessing [http://localhost:8081/hystrix](http://localhost:8081/hystrix)

## Import the Prototype in Eclipse

The prototype application is located in the __Prototype__ directory. The application uses [Gradle](#references) as the build and dependency management tool and can be imported into eclipse in two ways:

* [Import gradle project](#import-gradle-project)
* [Import existing project](#import-existing-project)

### Import gradle project

Using the eclipse gradle-plugin to import the project, gradle will automatically take care of dependencies and you will be able to run gradle commands directly from eclipse.

Open eclipse:

* Help -> Eclipse Marketplace
* Search for _Buildship Gradle Integration 2.0_ and install it

When eclipse has restarted, import the project:

* File -> Import
* Gradle -> Existing Gradle Project
* Browse for the _Prototype_ projects root dir and click _Open_
* Click _Finish_

### Import existing project

If you don't want to install the gradle plugin in eclipse, the project can be imported in the traditional way. Before importing the project into eclipse, you need to generate the eclipse .project file. This is done using the gradle wrappe - run the following command that will setup an eclipse project:

```bash
# change to the project root directory
cd Prototype
./gradlew eclipse
```

_If you are on a windows machine, use the gradlew.bat file instead._

Open eclipse:

* File -> Import
* General -> Existing projects into Workspace
* Browse for the _Prototype_ projects root dir and click __Open__

You should now have a _HystrixPrototype_ project in the project explorer.

## Running the prototype Java application

The prototype application can be run from within __eclipse__ or from the __command line__ using gradle.

### Running from Eclipse

Running from eclipse requires a [Jetty plugin](#references), which can be installed in eclipse using this guide: [http://eclipse-jetty.github.io](http://eclipse-jetty.github.io)

Once the Jetty plugin is installed do the following in eclipse:

* Right click on the project in the Project Explorer
* Run As -> Run Configuration
* Right click _Jetty Webapp_ and select _New_
* To tell eclipse where to find the __webapp__ dir, click the _Scan_ button
* Make sure you select port _8080_ for your application
* Click _Run_

The endpoint for accessing the server endpoint is: [http://localhost:8080/v1/hello](http://localhost:8080/v1/hello). This of course depends on the port you configured in the run configuration in eclipse. 

### Running from Command Line using Gradle

```bash
# change to the project root directory
cd Prototype
./gradlew jettyRun
```

This uses the project files and deploys them to Jetty. Running this way will make the endpoint available here: [http://localhost:8080/HystrixPrototype/v1/hello](http://localhost:8080/HystrixPrototype/v1/hello)

> Notice that running from command line makes the prototype available on a different URL than Eclipse!

### Accessing the prototype endpoint

The application should now be running via __eclipse__ or __command line__. The application should be available through one of the following URLs depending on how you started it:

* Eclipse: [http://localhost:8080/v1/hello](http://localhost:8080/v1/hello)
* Command Line: [http://localhost:8080/HystrixPrototype/v1/hello](http://localhost:8080/HystrixPrototype/v1/hello)

Try accessing it through __Postman__ or __curl__:

```bash
curl http://localhost:8080/v1/hello
# or
curl http://localhost:8080/HystrixPrototype/v1/hello
```

The output should look similar to this:

```bash
Quites command secceded : true
Time command succeded   : true
Time  : Server time is 15:54:13
Quote : Program testing can be used to show the presence of bugs, but never to show their absence! - Edsger Dijkstra
```

## Putting load on the prototype application

Now that the Hystrix Dashboard and Mountebank stubs are in place and you have the prototype running on a Jetty server, it's time to add some load on the application.

### Monitor circuit health through Hystrix Dashboard

Access the hystrix dashboard [http://localhost:8081/hystrix](http://localhost:8081/hystrix) and add the hystrix stream URL depending on how you started the prototype.

* Eclipse: http://<ip_address_of_host_machine>:8080/hystrix.stream
* Command Line: http://<ip_address_of_host_machine>:8080/HystrixPrototype/hystrix.stream

> Notice that because the dashboard is running in a docker container and the prototype application sending the data stream is running on the host machine, you must use the IP address of the host machine when adding the stream URL on the dashboard page.

Click the _Monitor Stream_ button. Try sending some requests to the prototype URL and you should see two circuits (Command1 and Command2) both receiving requests and both in _Closed_ state.

> If you see the message _Unable to connect to Command Metric Stream._ try sending a few requests to the prototype application __(/v1/hello)__ as it does not start the stream before the first request has been received. If it is still not working, your application might not be started or you added the wrong hystrix.stream URL.

### Putting load on the prototype

To put load on the prototype use Apache Benchmark or JMeter.

#### Using JMeter

The project contains two configurations located in the _JMeter_ dir. Which one you should use depends on how you started the prototype.

* Eclipse: __eclipse_started.jmx__ (use this if you startet the prototype from eclipse)
* Command Line: __cmdline_started.jmx__ (use this if you startet the prototype from command line)

Both configs are configured with __10 Threads__ and __2000 Loop Count__. The only difference is the HTTP request path they use _(http://localhost:8080/v1/hello or http://localhost:8080/HystrixPrototype/v1/hello)_.

* Open JMeter
* File -> Open (browse to the desired .jmx file)
* Click the green arrow to _Start_ the load test

#### Using Apache Benchmark

To use Apache Benchmark run one of the following commands:

```bash
# Run this command of the prototype was started using eclipse
ab -n 2000 -c 10 http://localhost:8080/v1/hello
# or
# Run this command of the prototype was started from command line
ab -n 2000 -c 10 http://localhost:8080/HystrixPrototype/v1/hello
```

This will start the test using __10 Threads__ and __2000 Loop Count__.

#### Monitoring the prototype circuits while running the test

Run the test using one of the methods described above while you watch Hystrix Dashboard. You should be able to see the requests being porcessed by the Hystrix Commands and that the circuit remain closed at all times.  

### Simulating slow response from the DOC

Now that everything is set up and we are able to put load on the prototype, it's time to simulate slow response from the dependent-on components (DOC).

To simulate slow response from the two DOC send a PUT using Postman or CURL. This will configure the DOC stubs to have a latency of 1500 milliseconds.

#### Using Postman

Send a PUT request to Mountebank by selecting __[Stub] Both services slow response__ and click _Send_.

#### Using CURL

```bash
cd mountebank_config/curl
curl -X PUT -d @both_services_slow_response.json http://localhost:2525/imposters
```

Now rerun the test from before and take a look at the Dashboard. This time you will see the percentage rising and the circuits opening. You will also notice that you still receive 200 OK responses in your test. This means that Hystrix has detected the slow response, opened its circuits and returning the fallback response insteam.

> There are additional stub configurations which will simulate slow response from one of the two DOCs or from both of them. Use Postman or CURL to play around with Hystrix and see what happens if you change configuration while testing.

# Project Overview

## Important settings

_QuotesHttpRequestCommand.java_ and _TimeHttpRequestCommand.java: 
contains settings for the circuit breakers and url to the imposters in mountebank

PoolingConnectionManager
Contains thread pool settings for the HTTP client library

CloseableHttpClientFactory
Contains timeout values for the HTTP client used for accessing the imposters

# References

* Eclipse [https://eclipse.org/](https://eclipse.org/)
* Eclipse Jetty Integration [http://eclipse-jetty.github.io/](http://eclipse-jetty.github.io/)
* Hystrix Wiki [https://github.com/Netflix/Hystrix/wiki](https://github.com/Netflix/Hystrix/wiki)
* Hystrix Dashboard [https://github.com/Netflix/Hystrix/wiki/Dashboard](https://github.com/Netflix/Hystrix/wiki/Dashboard)
* Docker [https://docs.docker.com/](https://docs.docker.com/)
* Mountebank [http://www.mbtest.org/](http://www.mbtest.org/)
	* Getting started [http://www.mbtest.org/docs/gettingStarted](http://www.mbtest.org/docs/gettingStarted)
* Mountebank Docker Image [https://hub.docker.com/r/jkris/mountebank/](https://hub.docker.com/r/jkris/mountebank/)
* JMeter [http://jmeter.apache.org/](http://jmeter.apache.org/)
* Apache Benchmark [https://httpd.apache.org/docs/2.4/programs/ab.html](https://httpd.apache.org/docs/2.4/programs/ab.html)
* Postman [https://www.getpostman.com/](https://www.getpostman.com/)
* CURL [https://curl.haxx.se/docs/manpage.html](https://curl.haxx.se/docs/manpage.html)
* Gradle [https://gradle.org/](https://gradle.org/)
* Gradle plugin for Eclipse [http://www.vogella.com/tutorials/EclipseGradle/article.html](http://www.vogella.com/tutorials/EclipseGradle/article.html#installation-via-the-marketplace)
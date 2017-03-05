# Hystrix Prototype

The purpose of this prototype is to gain experience with Hystrix as a circuit breaker pattern in distributed architecture. We achieve this by:

1. Wrapping HTTP calls to downstream services in Hystrix commands.
2. Using Mountebank to control latency of downstream services.
3. Using JMeter or Apache Benchmark to put load on the prototype.

The prototype is a very basic implementation of a web application, which uses Hystrix Commands to wrap HTTP requests. The web server exposes Hystrix metrics which can be used for monitoring the HTTP requests and the state of the circuit breaker in a Hystrix dashboard.

The Hystrix commands will execute 2 HTTP GET requests to two different URLS, which should be controlled by mountebank and can be manipulated by sending HTTP requests to the mountebank server.

The following sections will explain the setup of the specific components.

* [Setting up and configuring Mountebank](#setting-up-and-configuring-mountebank)
* [Setting up Hystrix Dashboard](#setting-up-hystrix-dashboard)
* [Running the prototype Java application](running-the-prototype-java-application)
* [Putting load on the prototype](putting-load-on-the-prototype)

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

* Postman is an application used for sending HTTP requests
* Download and install Postman: https://www.getpostman.com/
* Import the Postman collection:
	* Click the _Import_ button located in the upper left side of the window and import the collection located here:
		* __mountebank_config/postman/HystrixPrototype.postman_collection__
	* You should now see _3 imported_ HTTP requests
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

# Hystrix Prototype Project:

The prototype application is located in the __Prototype__ directory. The application used gradle as the build and dependency management tool and can be imported into eclipse in two ways:

* 

The project can be run with tomcat or Jetty. It has been tested with Jetty, which can 
be installed in eclipse using this guide: http://eclipse-jetty.github.io/

Start the Jetty/Tomcat server on port 8080.

The endpoint for accessing the server endpoint is: http://localhost:8080/v1/hello.
This of course depends on the port, which is configured in the run configuration in
eclipse. 





TESTING THE SETUP:

1.
Use postman to execute the imported GET request, which should access the following url:
http://localhost:8080/v1/hello and return a value like: 

'Hello :) First command succeeded: true. Second command succeded: true'

2. Connect the hystrix dashboard to the webserver by typing the url to the webserver in
the hystrix dashboard. Remember that, the dashboard is running in a docker container 
which does not work with 'localhost'. Use the url like: 
http://[network ip etc. '192.168.1.78']:8080/hystrix.stream

3. Use JMeter to put more load on the webserver. Use the 'load.jmx' file located 'Jmeter' 
folder, which is configured to put load on http://localhost:8080/v1/hello

4. Use and modify the imported PUT request to put more latency on the mountebank imposters



IMPORTANT SETTINGS:

HttpRequestCommand.java and HttpRequestCommand1.java:  
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
* curl [https://curl.haxx.se/docs/manpage.html](https://curl.haxx.se/docs/manpage.html)
* Gradle [https://gradle.org/](https://gradle.org/)
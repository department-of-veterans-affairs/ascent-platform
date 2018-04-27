## What is this repository for? ##

Ascent Platform Repository is a suite of Project POM files that provides application services with parent and starter dependencies for the new platform. 

**ascent-platform-docker-build: Ascent Platform Docker Build**

This project includes docker build and deployment for the Ascent Platform components.

**ascent-platform-parent: Ascent Platform Starter Projects**

This project includes ascent starter libraries for cache, logger, security, swagger, rest interfaces.

## Overview of core platform services ##
**ascent-discovery: Discovery Service**

Spring Cloud Netflix Eureka Discovery Service. REST-based service discovery and registration for fail over and load-balancing.
See [Ascent-Discovery](https://github.com/department-of-veterans-affairs/ascent-platform/wiki/PLATFORM-:-Ascent-Discovery) or go to [Ascent Discovery Repository](https://github.com/department-of-veterans-affairs/ascent-discovery) for additional details.


**ascent-config: Cloud Config Service**

Spring Cloud Config to centralize external configuration management, backed by Git. See [Ascent-Config](https://github.com/department-of-veterans-affairs/ascent-platform/wiki/PLATFORM-:-Ascent-Config) or go to [Ascent Config Repository](https://github.com/department-of-veterans-affairs/ascent-config) for additional details.


**ascent-gateway: API Gateway**

Spring Cloud Zuul Gateway Service. It provides Dynamic routing, monitoring, resiliency, security, and more. See [Ascent-Gateway](https://github.com/department-of-veterans-affairs/ascent-platform/wiki/Ascent-Gateway) or go to [Ascent Gateway Repository](https://github.com/department-of-veterans-affairs/ascent-gateway) for additional details.


**ascent-dashboard: Dashboard(s)**

Demo of various dashboards such as Hystrix which is a provided dashboard, Turbine to monitor a single server or a cluster of servers aggregated, custom consolidated swagger dashboard and Monitoring Dashboard (Spring Boot Actuator URLs).  Other dashboards, if we decided to tinker, can go here so we don't need to deploy 50 applications locally to test out basic dashboards. See [Ascent-Dashboard](https://github.com/department-of-veterans-affairs/ascent-platform/wiki/PLATFORM-:-Ascent-Dashboard) or go to [Ascent Dashboard Repository](https://github.com/department-of-veterans-affairs/ascent-dashboard) for additional details.


**ascent-zipkin: Instrumentation and Audit**
Zipkin service lets aggregate and track the time-span for processing every incoming request within sleuth-enabled services by consuming the instrumentation data and persisting them in elasticsearch index. It also provides a simple yet intuitive UI to visually view the request spans across multiple services. See [Ascent-Zipkin](https://github.com/department-of-veterans-affairs/ascent-platform/wiki/PLATFORM-:-Ascent-Instrumentation-Sleuth-and-Zipkin) or go to [Ascent Zipkin Repository](https://github.com/department-of-veterans-affairs/ascent-zipkin) for additional details.

**Service Application Ports**
* Discovery - 8761
* Cloud Config - 8760
* Gateway - 8762
* Misc. Dashboard(s) - 8763
* Zipkin - 8700

For more documentation details, please see the [wiki](https://github.com/department-of-veterans-affairs/ascent-platform/wiki)

## How do I get set up? ##

Create a **GitHub** account. Make sure to follow the [Ascent Quick Start Guide](https://github.com/department-of-veterans-affairs/ascent-platform/wiki/DEV-:-Platform-Quick-Start-Guide) for the system prerequisites that needs to be installed in your machine  and follow the steps  for generating new SSH key with passphrase to connect to GitHub using your account. 

Create a project workspace locally. From the command line, go to your project workspace and run the below command to checkout ascent-platform code base. 

*git clone https://github.com/department-of-veterans-affairs/ascent-platform.git*

<h3>Build the docker images for core platform services</h3>

The repository contains the start up script **ascent-builds.sh** that gets the code base for core platform services from their respective public repositories and builds the docker image for them locally.

<h3>Running the core services in docker-demo profile</h3>

Upon successfully building the docker images, running the **start-all.sh** script under **ascent-platform-docker-build** should bring up the docker containers for the core platform services,elk stack,filebeat and redis. Run the **stop-all.sh** script to bring down the docker containers.

<h3>Integrating application services with core platform services</h3>

A few sample services are created in order to demonstrate how the applications would integrate with the core platform services. The code base for these services are pushed under the **ascent-sample** repository.
See the [Ascent Sample quick start guide](https://github.com/department-of-veterans-affairs/ascent-sample/wiki/DEV-:-Quick-Start-Guide-for-Ascent-Sample) for step by step details. 

# GDS Metrics: Dropwizard version
Library for [prometheus](https://prometheus.io/) instrumentation in [Dropwizard](http://www.dropwizard.io) based apps.

## Overview

The library can be added to your web app to capture metrics about how it's performing. These metrics are served from an endpoint on your app and can be scraped by Prometheus and turned into Grafana dashboards.

## Build the project
`./gradlew build`

## Publish to a Maven local repositoty
`./gradlew publishToMavenLocal`

## Adding the library to your project

Add the online repository where the library is stored.

### Maven

```
<repositories>
    <repository>
        <id>reliability-engineering-repository</id>
        <name>Repository containing reliability enginieering dependencies</name>
        <url>https://dl.bintray.com/reliability-engineering-gds/gds_metrics_dropwizard</url>
    </repository>
</repositories>
``` 

### Gradle
```
repositories {
    maven { url "https://dl.bintray.com/reliability-engineering-gds/gds_metrics_dropwizard" }
}
```

Add the library as dependency to your project.

### Maven
```
<dependency>
    <groupdId>engineering.gds-reliability</groupdId>
    <artifactId>gds-metrics-dropwizard</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
```
implementation 'engineering.gds-reliability:gds-metrics-dropwizard:1.0.0'
```

### Configure the app to be used by prometheus

1. Your application must be deployed on [PaaS](https://www.cloud.service.gov.uk/).
2. Add the environment variable `PROMETHEUS_METRICS_PATH`. Example:
```
PROMETHEUS_METRICS_PATH=/metrics
```
3. If you want to have Dropwizard extended metrics enabled, define the `ENABLE_DROPWIZARD_METRICS` environment variable. Example:
```
ENABLE_DROPWIZARD_METRICS=true
```

### Changes in your project

1. Add the `MetricsBundle`.
```
public void initialize(final Bootstrap<ExampleConfiguration> bootstrap) {
    ...
    bootstrap.addBundle(new MetricsBundle());
    ...
}
```

2. Disable app security for the metrics path.
If your application already has security (authentication) implemented, the metrics path defined in the variable `PROMETHEUS_METRICS_PATH` should be excluded because this path already has its own security. `Configuration.getInstance.getPrometheusMetricsPath()` can be used to recover the proper value.

## Running on PaaS

If your app runs on the [GOV.UK PaaS](https://www.cloud.service.gov.uk/), you'll need to set the environment variable with:

```
$ cf set-env your-app-name PROMETHEUS_METRICS_PATH /metrics
```

This command makes the metrics endpoint available in production, whereas the setup steps above only applied temporarily to the server on your local machine.

In production, this endpoint is automatically protected with authentication. Citizens will not be able to see your metrics.

```
$ cf set-env your-app-name ENABLE_DROPWIZARD_METRICS true
```

This command makes the Dropwizard extended metrics available, obtaining a more verbose metrics.

## Extended metrics

By default the application activates some default metrics making them available to the user. In addition, the library offers the possibility (see steps above) to enable the Dropwizard metrics. This metrics are based on the project [Dropwizard Metrics](http://metrics.dropwizard.io)

The users can choose to activate them or not if they consider they bring something interesting or they want to build some custom Grafana dashboards. 

## Adding custom metrics
This step is optional.

By default, common metrics will be recorded, but you can record your own metrics, too. You might want to capture how many users are signed up for your service or how many emails are sent.

The library is built on top of the `prometheus_java_client`, so you can use the [interface it provides](https://github.com/prometheus/client_java#instrumenting) for this. There's more documentation on types of metric [here](https://prometheus.io/docs/concepts/metric_types/).

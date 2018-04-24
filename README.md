# GDS metrics for Dropwizard apps

GDS Metrics are in Alpha and these instructions are subject to change.

The GDS [Dropwizard][] metrics library enables your Java web app to export performance data to [Prometheus][] and can be added to your app using this library. 

The library is a thin wrapper around the [Prometheus instrumentation library for JVM applications][] that:

* adds a MetricsBundle you can include in your dropwizard app
* fixes [label naming][] so it's consistent across different app types
* protects your /metrics endpoint with basic HTTP authentication for apps deployed to GOV.UK PaaS 

Once you’ve added the library, metrics data is served from an endpoint on your app and is scraped by Prometheus. This data can be turned into performance dashboards using [Grafana][].

You can read more about the Reliability Engineering monitoring solution [here][].

## Before using GDS metrics

Before using GDS metrics you should have:

* created a Java app
* deployed that Java app to [GOV.UK Platform as a Service (PaaS)][]

## How to build your project

1. Add the GDS metrics library to where your project is stored by adding:

    Maven
    ```
    <repositories>
        <repository>
            <id>reliability-engineering-repository</id>
            <name>Repository containing reliability enginieering dependencies</name>
            <url>https://dl.bintray.com/reliability-engineering-gds/gds_metrics_dropwizard</url>
        </repository>
    </repositories>
    ```

    Gradle
    ```
    repositories {
        maven { url "https://dl.bintray.com/reliability-engineering-gds/gds_metrics_dropwizard" }
    }
    ```

2. Add your library as a dependency to your project.

    Maven
    ```
    <dependency>
        <groupdId>engineering.gds-reliability</groupdId>
        <artifactId>gds-metrics-dropwizard</artifactId>
        <version>1.0.0</version>
    </dependency>
    ```

    Gradle
    ```
    implementation 'engineering.gds-reliability:gds-metrics-dropwizard:1.0.0'
    ```

By default, metrics will be exposed at the path /metrics. You can change this with an environment variable like this:

    ```
    PROMETHEUS_METRICS_PATH=/metrics
    ```

## How to Change your project

1. Add the `MetricsBundle`.
    ```
    public void initialize(final Bootstrap<ExampleConfiguration> bootstrap) {
        ...
        bootstrap.addBundle(new MetricsBundle());
        ...
    }
    ```

2. If your app requires full authentication, disable the basic authentication for the metrics path (by default, /metrics). The Dropwizard library enables HTTP basic authentication for the metrics path so Prometheus can scrape the metrics.

To recover the original value, use:

`Configuration.getInstance.getPrometheusMetricsPath()`

Your metrics endpoint will now be available in your production environment. Citizens won’t see your metrics in production.

## Running on GOV.UK Platform as a Service (PaaS)

The install steps for GDS Metrics only apply to a project on your local machine. If your app runs on [PaaS][], you'll need to set the [environment variable][] by running:

```
$ cf set-env your-app-name PROMETHEUS_METRICS_PATH /metrics
```

Where `your-app-name` is the name of your app.

Your metrics endpoint will now be available in your production environment. Citizens won’t see your metrics in production as this endpoint is automatically protected with authentication.

## How to setup extended metrics

<placeholder to actually list what metrics you’ll get>

Extended metrics are based on the project [Dropwizard Metrics][], you can choose to activate them if you want to build custom Grafana dashboards.

You can also enable Dropwizard extended metrics by running this command:

```
$ cf set-env your-app-name ENABLE_DROPWIZARD_METRICS true
```

Where `your-app-name` is the name of your app.

## Adding logging metrics

To record how many log lines you are emitting, add an appender of type `instrumented` to your config.yaml:

```yaml
logging:
  # <... logging configuration ...>
  appenders:
    # <... other appenders go here>
    - type: instrumented
```

This will add a metric named `logback_appender_total` which records the total number of log messages at each logging level.  For example:

```
logback_appender_total{level="debug",} 0.0
logback_appender_total{level="warn",} 1.0
logback_appender_total{level="trace",} 0.0
logback_appender_total{level="error",} 0.0
logback_appender_total{level="info",} 14.0
```

## How to configure custom metrics

While common metrics are recorded by default, you can also:

* record your own metrics such as how many users are signed up for your service, or how many emails it's sent
* use the [Prometheus interface][] to set your own metrics as the metrics Dropwizard library is built on top of the `prometheus_java_client`

You can read more about the different types of metrics available in the [Prometheus documentation][].

## Contributing

GDS Reliability Engineering welcome contributions. We'd appreciate it if you write tests with your changes and document them where appropriate, this will help us review them quickly.

## Licence

This project is licensed under the [MIT License][].



[Dropwizard]: http://www.dropwizard.io/
[Prometheus]: https://prometheus.io/
[Grafana]: https://grafana.com/
[here]: https://reliability-engineering.cloudapps.digital/#reliability-engineering
[GOV.UK Platform as a Service (PaaS)]: https://www.cloud.service.gov.uk/
[Gradle]: https://gradle.org/
[Maven]: https://maven.apache.org/
[PaaS]: https://www.cloud.service.gov.uk/
[environment variable]: https://docs.cloud.service.gov.uk/#environment-variables
[Dropwizard Metrics]: http://metrics.dropwizard.io/
[Prometheus interface]: https://github.com/prometheus/client_java#instrumenting
[Prometheus documentation]: https://prometheus.io/docs/concepts/metric_types/
[MIT License]: https://github.com/alphagov/gds_metrics_dropwizard/blob/master/LICENSE
[Prometheus instrumentation library for JVM applications]: https://github.com/prometheus/client_java
[label naming]: https://prometheus.io/docs/practices/naming/#labels

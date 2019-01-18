# GDS metrics for Dropwizard apps

GDS Metrics are in Alpha and these instructions are subject to change.

The GDS [Dropwizard][] metrics library enables your Java web app to export performance data to [Prometheus][] and can be added to your app using this library.

The library is a thin wrapper around the [Prometheus instrumentation library for JVM applications][] that adds a PrometheusBundle you can include in your dropwizard app.

Once you’ve added the library, metrics data is served from an endpoint on your app and is scraped by Prometheus. This data can be turned into performance dashboards using [Grafana][].

You can read more about the Reliability Engineering monitoring solution [here][].

## Before using GDS metrics

Before using GDS metrics you should have:

* created a Dropwizard app

## How to build your project

1. Add the GDS metrics library to where your project is stored by adding:

    Maven
    ```
    <repositories>
        <repository>
            <id>reliability-engineering-repository</id>
            <name>Repository containing reliability engineering dependencies</name>
            <url>https://dl.bintray.com/alphagov/maven</url>
        </repository>
    </repositories>
    ```

    Gradle
    ```
    repositories {
        maven { url "https://dl.bintray.com/alphagov/maven" }
    }
    ```

2. Add your library as a dependency to your project.

    Maven
    ```
    <dependency>
        <groupId>engineering.gds-reliability</groupId>
        <artifactId>gds-metrics-dropwizard</artifactId>
        <version>0.2.0</version>
    </dependency>
    ```

    Gradle
    ```
    implementation 'engineering.gds-reliability:gds-metrics-dropwizard:0.2.0'
    ```

The metrics will be exposed at the path /prometheus/metrics on the admin port.

## How to Change your project

Add the `PrometheusBundle`:

```
public void initialize(final Bootstrap<ExampleConfiguration> bootstrap) {
    ...
    bootstrap.addBundle(new PrometheusBundle());
    ...
}
```

Your metrics endpoint will now be available in your production environment.

## How to configure custom metrics

While common metrics are recorded by default, you can also:

* record your own metrics such as how many users are signed up for your service, or how many emails it's sent
* use the [Prometheus interface][] to set your own metrics as the metrics Dropwizard library is built on top of the `prometheus_java_client`

You can read more about the different types of metrics available in the [Prometheus documentation][].

## Contributing

GDS Reliability Engineering welcome contributions. We'd appreciate it if you write tests with your changes and document them where appropriate, this will help us review them quickly.

## Releasing a new version

To release a new version:

 - bump the version in gradle.properties and the dependencies in the README
   and commit
 - tag with the new version number and push the tag (eg `git tag 0.1.3; git push --tags`)
 - travis will build and deploy the new version to bintray
 - bump the version in gradle.properties to the next SNAPSHOT (eg `0.1.3-SNAPSHOT`)

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

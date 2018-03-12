# GDS metrics for Dropwizard apps

GDS Metrics are in Alpha and these instructions are subject to change.

GDS [Dropwizard][] metrics enable your Java web app to export performance data to [Prometheus][] and can be added to your app using this library. Once you’ve added the library, metrics data is served from an endpoint on your app and is scraped by Prometheus. This data can be turned into performance dashboards using [Grafana][].

You can read more about the Reliability Engineering monitoring solution [here][].

## Before using GDS metrics

Before using GDS metrics you should have:

* created a Java app
* deployed that Java app to [GOV.UK Platform as a Service (PaaS)][]

## How to build your project

To use GDS metrics you must:

1. Run the following [Gradle][] command to build your project:

    `./gradlew build`

2. Publish to a [Maven][] local repository, by running:

    `gradle publishToMavenLocal`

3. Add the GDS metrics library to where your project is stored by adding:

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

4. Add your library as a dependency to your project.

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

5. When your app is deployed to the PaaS, you can add an environment variable so Prometheus can discover your app’s metrics, for example:

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

2. Disable app security for the metrics path. If your application already has security (authentication) implemented, the metrics path defined in the variable `PROMETHEUS_METRICS_PATH` should be excluded because this path already has its own security. `Configuration.getInstance.getPrometheusMetricsPath()` can be used to recover the proper value.

## Running on GOV.UK Platform as a Service (PaaS)

The install steps for GDS Metrics only apply to a project on your local machine. If your app runs on [PaaS][], you'll need to set the [environment variable][] by running:

```
$ cf set-env your-app-name PROMETHEUS_METRICS_PATH /metrics
```

Where `your-app-name` is the name of your app.

Your metrics endpoint will now be available in your production environment. Citizens won’t see your metrics in production as this endpoint is automatically protected with authentication.

## How to setup extended metrics

Extended metrics are based on the project [Dropwizard Metrics][], you can choose to activate them if you want to build custom Grafana dashboards.

You can also enable Dropwizard extended metrics by running this command:

```
$ cf set-env your-app-name ENABLE_DROPWIZARD_METRICS true
```

Where `your-app-name` is the name of your app.

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

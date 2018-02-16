# gds_metrics_dropwizard
Library for prometheus instrumentation in __Dropwizard__ based apps.

## Build the project
`./gradlew build`

## Publish to a Maven local repositoty
`gradle publishToMavenLocal`

## Adding to your project

Add the library as dependency to your project.

### Configure the app to be used by prometheus

1. Your application must be deployed on [PaaS](https://www.cloud.service.gov.uk/).
2. Add the environment variable `PROMETHEUS_METRICS_PATH`. Example:
```
PROMETHEUS_METRICS_PATH=/metrics
```
3. If you want to have Dropwizard extended metrics enabled define the `ENABLE_DROPWIZARD_METRICS` environment variable. Example:
```
ENABLE_DROPWIZARD_METRICS=true
```

### Maven
```
<dependency>
    <groupdId>uk.gov.reng</groupdId>
    <artifactId>gds-metrics-dropwizard</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
```
implementation 'uk.gov.reng:gds-metrics-dropwizard:1.0.0'
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

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
PROMETHEUS_METRICS_PATH = /metrics
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

1. Instantiate  the `Configuration` class in your `run` method.
```
public void run(final Configuration conf, final Environment env) throws Exception {
    ...
    final Configuration configuration = Configuration.getInstance();
    ...
}
```

2. Set the servlet endpoint to allow Prometheus to collect metrics.
```
public void run(final Configuration conf, final Environment env) throws Exception {
    ...
    environment.servlets().addServlet("metrics", new MetricsServlet())
        .addMapping(configuration.getPrometheusMetricsPath());
    ...
}
```

3. If you want the endpoint to be protected with authentication, add the `AuthenticationFilter` filter.
```
public void run(final Configuration conf, final Environment env) throws Exception {
    ...
    environment.servlets()
        .addFilter("AuthenticationFilter", new AuthenticationFilter())
        .addMappingForUrlPatterns(
            EnumSet.of(DispatcherType.REQUEST),
            true,
            configuration.getPrometheusMetricsPath());
    ...
}
```
4. Initialize the metrics and register the `DropwizardExports`.
```
public void initialize(Bootstrap<MonitoringConfiguration> bootstrap) {
    ...
    final MetricRegistry metrics = bootstrap.getMetricRegistry();
    CollectorRegistry.defaultRegistry.register(new DropwizardExports(metrics));
    ...
}
```
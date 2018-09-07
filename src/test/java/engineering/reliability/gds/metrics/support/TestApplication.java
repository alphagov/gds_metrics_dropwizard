package engineering.reliability.gds.metrics.support;

import engineering.reliability.gds.metrics.bundle.PrometheusBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TestApplication extends Application<TestConfiguration> {
    @Override
    public void initialize(Bootstrap<TestConfiguration> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.addBundle(new PrometheusBundle());
        // DropwizardAppRule is a bit of a hack and skips the call to Application.run(String...)
        // which is where metrics get registered in the first place.
        // In order for PrometheusBundleTest.noDropwizardJvmMetricsAreLogged to be exercised,
        // we need to ensure that the jvm.* metrics actually get registered.
        // therefore we have to explicitly register them here
        bootstrap.registerMetrics();
    }

    @Override
    public void run(TestConfiguration configuration, Environment environment) {
        environment.jersey().register(new TestResource());
    }
}

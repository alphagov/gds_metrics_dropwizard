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
    }

    @Override
    public void run(TestConfiguration configuration, Environment environment) {
        environment.jersey().register(new TestResource());
    }
}

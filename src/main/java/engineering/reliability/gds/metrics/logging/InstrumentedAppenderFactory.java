package engineering.reliability.gds.metrics.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.helpers.NOPAppender;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.AbstractAppenderFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;
import io.prometheus.client.logback.InstrumentedAppender;

@JsonTypeName("instrumented")
public class InstrumentedAppenderFactory extends AbstractAppenderFactory<ILoggingEvent> {

    @JsonProperty
    private boolean enabled = true;

    @Override
    public Appender<ILoggingEvent> build(LoggerContext context,
                                         String applicationName,
                                         LayoutFactory<ILoggingEvent> layoutFactory,
                                         LevelFilterFactory<ILoggingEvent> levelFilterFactory,
                                         AsyncAppenderFactory<ILoggingEvent> asyncAppenderFactory) {
        if (!enabled) {
            final Appender<ILoggingEvent> appender = new NOPAppender<>();
            appender.start();
            return appender;
        }

        final InstrumentedAppender appender = new InstrumentedAppender();

        appender.setContext(context);
        appender.setName("prometheus-instrumented-appender");

        appender.addFilter(levelFilterFactory.build(threshold));
        getFilterFactories().forEach(f -> appender.addFilter(f.build()));
        appender.start();

        return wrapAsync(appender, asyncAppenderFactory);
    }
}

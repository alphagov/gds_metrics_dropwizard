package engineering.reliability.gds.metrics.config;

import engineering.reliability.gds.metrics.utils.EnvVarUtils;

import java.lang.reflect.Field;
import java.util.Objects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractConfigurationTest {

	// new this up for the first time so we can overwrite it knowing that envVarUtils has been initialised
	protected static Configuration configuration = Configuration.getInstance();

	protected static void clearConfigurationConfiguration() throws NoSuchFieldException, IllegalAccessException {
		final Field instance = Configuration.class.getDeclaredField("instance");
		instance.setAccessible(true);
		instance.set(configuration, null);
		// this forces configuration to be default values when initialised
		insertEnvVarUtilsIntoConfiguration(new EnvVarUtils());
	}

	protected static void insertEnvVarUtilsIntoConfiguration(EnvVarUtils envVarUtils) throws NoSuchFieldException, IllegalAccessException {
		final Field instance = Configuration.class.getDeclaredField("envVarUtils");
		instance.setAccessible(true);
		instance.set(configuration, envVarUtils);
	}

	protected void setConfigurationInEnvironment(String application, String path, Boolean enabled) throws NoSuchFieldException, IllegalAccessException {
		EnvVarUtils envVarUtils = mock(EnvVarUtils.class);
		when(envVarUtils.getEnv("VCAP_APPLICATION")).thenReturn(Objects.isNull(application)?null:"{ \"application_id\" => \""+application+"\" }");
		when(envVarUtils.getEnv("PROMETHEUS_METRICS_PATH")).thenReturn(path);
		when(envVarUtils.getEnv("ENABLE_DROPWIZARD_METRICS")).thenReturn(enabled.toString());
		insertEnvVarUtilsIntoConfiguration(envVarUtils);
		configuration = Configuration.getInstance();
	}

}

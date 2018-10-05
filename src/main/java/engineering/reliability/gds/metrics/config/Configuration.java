package engineering.reliability.gds.metrics.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import engineering.reliability.gds.metrics.utils.EnvVarUtils;

import java.util.Objects;

public class Configuration {

	private static Configuration instance;
	private static EnvVarUtils envVarUtils;

	private String applicationId;
	private String prometheusMetricsPath;
	private boolean activeDropwizardMetrics;

	private Configuration() {
	}

	public static synchronized Configuration getInstance() {
		if (Objects.isNull(instance)) {
			// this is so we can overwrite it for testing purposes
			// i.e. overwrite envVarUtils then getInstance()
			envVarUtils = Objects.isNull(envVarUtils)?new EnvVarUtils():envVarUtils;
			instance = new Configuration();
			instance.populateProperties();
		}

		return instance;
	}

	private void populateProperties() {
		applicationId = readApplicationId();
		prometheusMetricsPath = readPrometheusMetricsPath();
		activeDropwizardMetrics = readDropwizardMetricsActivation();
	}

	public String getApplicationId() {
		return applicationId;
	}

	public String getPrometheusMetricsPath() {
		return prometheusMetricsPath;
	}

	public boolean isAuthEnable() {
		return Objects.nonNull(applicationId);
	}

	public boolean isActiveDropwizardMetrics() {
		return activeDropwizardMetrics;
	}

	private String readApplicationId() {
		final String vcapApplication = envVarUtils.getEnv("VCAP_APPLICATION");
		final JsonObject jsonObject;

		if (Objects.isNull(vcapApplication)) {
			return null;
		}

		jsonObject = new JsonParser().parse(vcapApplication).getAsJsonObject();

		return jsonObject.get("application_id").getAsString();
	}

	private String readPrometheusMetricsPath() {
		final String prometheusMetricsPath = envVarUtils.getEnv("PROMETHEUS_METRICS_PATH");

		return Objects.nonNull(prometheusMetricsPath) ? prometheusMetricsPath : "/metrics";
	}

	private boolean readDropwizardMetricsActivation() {
		final String activeDropwizardMetrics = envVarUtils.getEnv("ENABLE_DROPWIZARD_METRICS");

		return Objects.nonNull(activeDropwizardMetrics) && activeDropwizardMetrics.equals("true");

	}

}

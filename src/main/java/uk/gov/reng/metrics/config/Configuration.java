package uk.gov.reng.metrics.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Objects;

public class Configuration {

	private static Configuration instance;
	private String applicationId;
	private String prometheusMetricsPath;
	private boolean activeDropwizardMetrics;

	private Configuration() {
	}

	public static synchronized Configuration getInstance() {
		if (Objects.isNull(instance)) {
			instance = new Configuration();
			instance.populateProperties();
		}

		return instance;
	}

	public void populateProperties() {
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
		final String vcapApplication = System.getenv("VCAP_APPLICATION");
		final JsonObject jsonObject;

		if (Objects.isNull(vcapApplication)) {
			return null;
		}

		jsonObject = new JsonParser().parse(vcapApplication).getAsJsonObject();

		return jsonObject.get("application_id").getAsString();
	}

	private String readPrometheusMetricsPath() {
		final String prometheusMetricsPath = System.getenv("PROMETHEUS_METRICS_PATH");

		return Objects.nonNull(prometheusMetricsPath) ? prometheusMetricsPath : "/metrics";
	}

	private boolean readDropwizardMetricsActivation() {
		final String activeDropwizardMetrics = System.getenv("ENABLE_DROPWIZARD_METRICS");

		if (Objects.nonNull(activeDropwizardMetrics) && activeDropwizardMetrics.equals("true")) {
			return true;
		}

		return false;
	}
}

package uk.gov.ida.metrics.utils;

public class EnvVarUtils {

    public EnvVarUtils() {}

    public String getEnv(String name) {
        return System.getenv(name);
    }
}

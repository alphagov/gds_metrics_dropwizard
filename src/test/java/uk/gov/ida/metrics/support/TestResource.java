package uk.gov.ida.metrics.support;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path(TestResource.TEST_RESOURCE_PATH)
public class TestResource {

    public static final String TEST_RESOURCE_PATH = "/";

    @Timed
    @GET
    public String get() {
        return "hello";
    }
}

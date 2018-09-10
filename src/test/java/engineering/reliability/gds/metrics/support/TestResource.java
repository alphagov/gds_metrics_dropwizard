package engineering.reliability.gds.metrics.support;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static engineering.reliability.gds.metrics.support.TestResource.TEST_RESOURCE_PATH;

@Path(TEST_RESOURCE_PATH)
public class TestResource {

    public static final String TEST_RESOURCE_PATH = "/";

    @Timed
    @GET
    public String get() {
        return "hello";
    }
}

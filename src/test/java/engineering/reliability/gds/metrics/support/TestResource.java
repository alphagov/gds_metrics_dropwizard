package engineering.reliability.gds.metrics.support;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class TestResource {
    @Timed
    @GET
    public String get() {
        return "hello";
    }
}

package golf.test;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.impl.client.CloseableHttpClient;

import com.netflix.hystrix.exception.HystrixRuntimeException;

import golf.test.cmd.HttpRequestCommand;
import golf.test.cmd.HttpRequestCommand1;

@Path("/hello")
public class Endpoint {

	@Inject
	CloseableHttpClient http_client;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response hello() throws ValidationException, InterruptedException {
		Boolean req = false;
		Boolean req1 = false;
		try {
			HttpRequestCommand cmd = new HttpRequestCommand(http_client);
			req = cmd.execute();
			if (cmd.isSuccessfulExecution() == false) {
				// If needed we can check if execution went well..
				// Should be used for optimization of flow - not errorhandling.
				// Hystrix fallback should handle errors.
				// System.out.println(cmd.getExecutionException());
			}
			HttpRequestCommand1 cmd1 = new HttpRequestCommand1(http_client);
			req1 = cmd1.execute();

		} catch (HystrixRuntimeException e) {
			if (e.getCause() instanceof InterruptedException) {
				throw (InterruptedException) e.getCause();
			}
			return Response.status(Status.SERVICE_UNAVAILABLE).build();
		} catch (Exception e) {
			return Response.status(Status.SERVICE_UNAVAILABLE).build();
		}
		String reply = String.format("Hello :) First command succeeded: %b. Second command succeded: %b", req, req1); 
		return Response.ok(reply, MediaType.TEXT_PLAIN_TYPE).build();
	}

}

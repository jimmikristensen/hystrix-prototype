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

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException;

import golf.test.cmd.ChuckHttpRequestCommand;
import golf.test.cmd.TimeHttpRequestCommand;

@Path("/hello")
public class Endpoint {

	@Inject
	CloseableHttpClient http_client;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response hello() throws ValidationException, InterruptedException {
		boolean chuckCommandSucceded = true;
		boolean timeCommandSucceded = true;
		String chuckReq = null;
		String timeReq = null;
		
		try {
			HystrixCommand<String> cmd = new ChuckHttpRequestCommand(http_client);
			chuckReq = cmd.execute();
			if (cmd.isSuccessfulExecution() == false) {
				// If needed we can check if execution went well..
				// Should be used for optimization of flow - not errorhandling.
				// Hystrix fallback should handle errors.
				// System.out.println(cmd.getExecutionException());
				chuckCommandSucceded = false;
			}
			HystrixCommand<String> cmd1 = new TimeHttpRequestCommand(http_client);
			timeReq = cmd1.execute();
			if (cmd1.isSuccessfulExecution() == false) {
				timeCommandSucceded = false;
			}

		} catch (HystrixRuntimeException e) {
			if (e.getCause() instanceof InterruptedException) {
				throw (InterruptedException) e.getCause();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		String reply = String.format("Chuck command secceded: %b\n Time command succeded: %b\n"
				+ "Time: %s\nChuck says: %s", chuckCommandSucceded, timeCommandSucceded, timeReq, chuckReq);
		return Response.ok(reply, MediaType.TEXT_PLAIN_TYPE).build();
	}

}

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

import golf.test.cmd.QuotesCommand;
import golf.test.cmd.TimeCommand;

@Path("/hello")
public class PrototypeService {

	@Inject
	CloseableHttpClient httpClient;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response hello() throws ValidationException, InterruptedException {
		boolean quotesCommandSucceded = true;
		boolean timeCommandSucceded = true;
		String quoteReq = null;
		String timeReq = null;
		
		try {
			
			// command handling the HTTP request sent to the Quotes service stub
			HystrixCommand<String> quotesCmd = new QuotesCommand(httpClient);
			quoteReq = quotesCmd.execute();
			if (quotesCmd.isSuccessfulExecution() == false) {
				// If needed we can check if execution went well.
				// Should be used for optimization of flow - not error handling.
				// Hystrix fallback should handle errors.
				// quotesCmd.getExecutionException() can be used to get the exception 
				// that caused command to fail.
				quotesCommandSucceded = false;
			}
			
			// command handling the HTTP request sent to the Time service stub
			HystrixCommand<String> timeCmd = new TimeCommand(httpClient);
			timeReq = timeCmd.execute();
			if (timeCmd.isSuccessfulExecution() == false) {
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
		
		// generate the response message
		String reply = String.format("%-24s: %b\n%-24s: %b\n%-6s: %s\n%-6s: %s\n", 
				"Quotes command secceded",
				quotesCommandSucceded, 
				"Time command succeded",
				timeCommandSucceded, 
				"Time",
				timeReq, 
				"Quote",
				quoteReq);
		
		// return the response in the body of the 200 OK message
		return Response.ok(reply, MediaType.TEXT_PLAIN_TYPE).build();
	}

}

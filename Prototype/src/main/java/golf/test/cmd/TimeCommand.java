package golf.test.cmd;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class TimeCommand extends HystrixCommand<String> {

	/* Name of the command - shown in the hystrix dashboard */
	public static final String CMD_NAME = "TimeCommand";
	
	/* Endpoint which the command should access (Downstream service) */
	public static final String CLIENT_ENDPOINT = "http://localhost:4546/time";
	
	/* Thread pool size for handling command requests */
	public static final int THREAD_POOL_SIZE = 15;
		
	/* Allowed execution time before hystrix aborts the command */
	public static final int ALLOWED_EXC_TIME_MS = 1500;

	/* The HTTP client */
	private CloseableHttpClient client;
	
	/*
	 * Configuration of the command
     * @see https://github.com/Netflix/Hystrix/wiki/Configuration
	 */
	public TimeCommand(CloseableHttpClient client) {
		super(Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey(CMD_NAME+"-Pool"))
				.andCommandKey(HystrixCommandKey.Factory.asKey(CMD_NAME))
				.andThreadPoolPropertiesDefaults(
						HystrixThreadPoolProperties.Setter()
						.withCoreSize(THREAD_POOL_SIZE))
				.andCommandPropertiesDefaults(
						HystrixCommandProperties.Setter()
						.withExecutionTimeoutInMilliseconds(ALLOWED_EXC_TIME_MS)));
		
		this.client = client;
	}

	/*
	 * This method will be executed when execute() is invoked
	 * It sends a HTTP request to the Time DOC and returns the response
	 * @see com.netflix.hystrix.HystrixCommand#run()
	 */
	@Override
	protected String run() throws Exception {
		HttpGet get = new HttpGet(CLIENT_ENDPOINT);
		String stringResponse = "";

		// Execute the HTTP request
		CloseableHttpResponse response = client.execute(get);
		try {			
			// Get the response from the Time service stub returned by the 
			// Mountebank imposter and parse it as a String
			HttpEntity entity = response.getEntity();
			stringResponse = EntityUtils.toString(entity, "UTF-8");
			
			EntityUtils.consume(entity); // Ensure response is fully consumed
		} finally {
			try{
				response.close();
			}
			catch(IOException ioe) {
				System.out.println("failed closing response. " + ioe.getMessage());
			}
		}
		
		// return the response from the DOC
		return "Server time is "+stringResponse;
	}

	/*
	 * If the command fails by run() throwing any kind of exception 
	 * the command will fallback and return the string in this method
	 * @see com.netflix.hystrix.HystrixCommand#getFallback()
	 */
	@Override
	protected String getFallback() {
		// Instead of getting the time from the Time service stub 
		// get the local time and return that
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		Date currentDate = new Date();
		return "Local time is "+df.format(currentDate);
	}
}

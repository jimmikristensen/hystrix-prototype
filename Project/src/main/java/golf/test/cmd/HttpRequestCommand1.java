package golf.test.cmd;

import java.io.IOException;

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

public class HttpRequestCommand1 extends HystrixCommand<Boolean> {

	//Name of the command - shown in the hystrix dashboard
	public static final String CMD_NAME = "Command2";
	
	//Endpoint which the command should access (Downstream service)
	public static final String CLIENT_ENDPOINT = "http://localhost:4546/test";
	
	//Thread pool size for handling command requests
	public static final int THREAD_POOL_SIZE = 15;
		
	//Allowed execution time before hystrix aborts the command
	public static final int ALLOWED_EXC_TIME_MS = 1500;

	private CloseableHttpClient client;
	
	public HttpRequestCommand1(CloseableHttpClient client) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(CMD_NAME+"-Pool"))
				.andCommandKey(HystrixCommandKey.Factory.asKey(CMD_NAME))
				.andThreadPoolPropertiesDefaults(
						HystrixThreadPoolProperties.Setter().withCoreSize(THREAD_POOL_SIZE))
				.andCommandPropertiesDefaults(
						HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(ALLOWED_EXC_TIME_MS)));
		
		this.client = client;
	}

	@Override
	protected Boolean run() throws Exception {
		HttpGet get = new HttpGet(CLIENT_ENDPOINT);

		CloseableHttpResponse response = client.execute(get);
		try {			
			//Here we could do something useful with the response, but
			//this is only a prototype, and server does not return anything useful.
			HttpEntity entity1 = response.getEntity();
			
			EntityUtils.consume(entity1); // Ensure response is fully consumed
		} finally {
			try{
				response.close();
			}
			catch(IOException ioe) {
				System.out.println("failed closing response. " + ioe.getMessage());
			}
		}
		return true;
	}

	@Override
	protected Boolean getFallback() {
		return false;
	}
}

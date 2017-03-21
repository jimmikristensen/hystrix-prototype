package golf.test.client;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/*
 * This is the HTTP connection pool used in the hystrix command
 * @see https://github.com/jimmikristensen/hystrix-prototype#important-settings
 */
public class PoolingConnectionManager {
	
	private static PoolingHttpClientConnectionManager manager;
	
	private static PoolingConnectionManager instance;
	
	private PoolingConnectionManager() {
		
		manager = new PoolingHttpClientConnectionManager();
		
		// Increase max total connection to 200
		manager.setMaxTotal(200);
		
		// Increase default max connection per route to 20
		manager.setDefaultMaxPerRoute(20);
		
		// Increase max connections for localhost:80 to 50
		HttpHost localhost = new HttpHost("localhost", 80);
		manager.setMaxPerRoute(new HttpRoute(localhost), 50);
	}
	
	public CloseableHttpClient getClient(int connectTimeout, int socketTimeout) {
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(connectTimeout)
				.setSocketTimeout(socketTimeout).build();
		
		return HttpClients.custom()
		        .setDefaultRequestConfig(requestConfig)
		        .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
				.setConnectionManager(manager)
		        .build();
	}
	
	public static PoolingConnectionManager getInstance() {
		if(instance == null) {
			instance = new PoolingConnectionManager();
		}
		return instance;
	}
}

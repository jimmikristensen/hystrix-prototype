package golf.test.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

import golf.test.PrototypeService;

/*
 * This is the configuration for this Jersey web service
 * @see https://jersey.java.net/
 */
public class ApplicationConfig extends ResourceConfig {

	public ApplicationConfig() {
		registerBinders();
		registerRESTServices();
	}

	private void registerRESTServices() {
		// Register HelloService to tell Jersey that this is a web service class 
		register(PrototypeService.class);
	}

	private void registerBinders() {
		register(new AbstractBinder() {
            @Override
            protected void configure() {
            	
            	// Create a binding for the HTTP client to make it available 
            	// in the HelloService class via the annotation @Inject
                bindFactory(CloseableHttpClientFactory.class).to(CloseableHttpClient.class)
                .proxy(true).proxyForSameScope(false).in(RequestScoped.class);
            }
        });
		
	}
	
}

package golf.test.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

import golf.test.Endpoint;

public class ApplicationConfig extends ResourceConfig {

	public ApplicationConfig() {
		registerBinders();
		registerRESTServices();
	}

	private void registerRESTServices() {
		register(Endpoint.class);
	}

	private void registerBinders() {
		register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(CloseableHttpClientFactory.class).to(CloseableHttpClient.class)
                .proxy(true).proxyForSameScope(false).in(RequestScoped.class);
            }
        });
		
	}
	
}

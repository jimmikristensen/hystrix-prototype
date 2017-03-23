package golf.test.config;

import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.configuration.AbstractConfiguration;

import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.hystrix.Hystrix;

@WebListener
public class InitClass implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// recommendations from https://ahus1.github.io/hystrix-examples/manual.html#_equip_your_application_with_hystrix
		
		// Shutdown all thread pools; waiting a little time for shutdown
		Hystrix.reset(1, TimeUnit.SECONDS);

		// Shutdown configuration listeners that might have been activated by
		// Archaius
		if (ConfigurationManager.getConfigInstance() instanceof DynamicConfiguration) {
			((DynamicConfiguration) ConfigurationManager.getConfigInstance()).stopLoading();

		} else if (ConfigurationManager.getConfigInstance() instanceof ConcurrentCompositeConfiguration) {
			ConcurrentCompositeConfiguration config = ((ConcurrentCompositeConfiguration) ConfigurationManager
					.getConfigInstance());

			for (AbstractConfiguration innerConfig : config.getConfigurations()) {
				if (innerConfig instanceof DynamicConfiguration) {
					((DynamicConfiguration) innerConfig).stopLoading();
				}
			}
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

	}

}

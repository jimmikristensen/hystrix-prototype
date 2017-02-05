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
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// shutdown all thread pools; waiting a little time for shutdown
		Hystrix.reset(1, TimeUnit.SECONDS);	
		// shutdown configuration listeners that might have been activated by
		// Archaius
		if (ConfigurationManager.getConfigInstance() instanceof DynamicConfiguration) {
		    ((DynamicConfiguration) ConfigurationManager.getConfigInstance())
		            .stopLoading();
		} else if (ConfigurationManager.getConfigInstance() instanceof ConcurrentCompositeConfiguration) {
		    ConcurrentCompositeConfiguration config =
		            ((ConcurrentCompositeConfiguration) ConfigurationManager
		                    .getConfigInstance());
		    for (AbstractConfiguration innerConfig : config.getConfigurations()) {
		        if (innerConfig instanceof DynamicConfiguration) {
		            ((DynamicConfiguration) innerConfig).stopLoading();
		        }
		    }
		}
	}
	
}

package plugins;

import org.drools.compiler.kie.builder.impl.KieServicesImpl;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import play.Environment;
import play.inject.ApplicationLifecycle;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Drools {
	
	public final KieSession kieSession;
	
	@Inject
	public Drools(ApplicationLifecycle lifecycle, Environment environment) {
		KieServices kieServices = new KieServicesImpl();
		KieContainer kc = kieServices.getKieClasspathContainer(environment.classLoader());
		kieSession = kc.newKieSession("owlRestAPIdrools");
		
		lifecycle.addStopHook(() -> {
			kieSession.destroy();
			return CompletableFuture.completedFuture(null);
		});
	}
	
}

package app;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

public class AgentClient {

	private final static String URL = "http://%s/Chat-war/api/%s";
	private final String url;
	
	public AgentClient(String hostToRecive) {
		super();
		this.url = String.format(URL, hostToRecive, "agents");
	}

	public void performAction(EndpointHandler<AgentRest> proxyHandler) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget rtarget = client.target(url);
		AgentRest rest = rtarget.proxy(AgentRest.class);
		
		// do work with rest proxy in a new thread
		Runnable runnable = () -> { 
			proxyHandler.handle(rest);
			client.close();
		};
		new Thread(runnable).start();
	}
	
}

package app;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

public class UserClient {
	
	private final static String URL_FORMAT = "http://%s/Chat-war/api/%s";
	private final String url;
	
	public UserClient(String receiverHost) {
		super();
		this.url = String.format(URL_FORMAT, receiverHost, "connections");
	}

	public void performAction(EndpointHandler<NodesRest> proxyHandler) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget rtarget = client.target(url);
		NodesRest rest = rtarget.proxy(NodesRest.class);
		
		// do work with rest proxy in a new thread
		Runnable runnable = () -> { 
			proxyHandler.handle(rest);
			client.close();
		};
		new Thread(runnable).start();
	}

}

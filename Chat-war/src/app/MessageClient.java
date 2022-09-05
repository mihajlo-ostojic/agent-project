package app;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

public class MessageClient {
	
	private final static String URL = "http://%s/Chat-war/api/%s";
	private final String url;
	
	public MessageClient(String hostToRecive) {
		super();
		this.url = String.format(URL, hostToRecive, "messages");
	}

	public void performAction(EndpointHandler<MessageRest> proxyHandler) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget rtarget = client.target(url);
		MessageRest rest = rtarget.proxy(MessageRest.class);
		
		// do work with rest proxy in a new thread
		Runnable runnable = () -> { 
			proxyHandler.handle(rest);
			client.close();
		};
		new Thread(runnable).start();
	}

}

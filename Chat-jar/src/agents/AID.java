package agents;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import models.Host;


public final class AID implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private Host host;
	private AgentType type;
	public AID() {
		super();
	}
	public AID(String name, Host host, AgentType type) {
		super();
		this.name = name;
		this.host = host;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Host getHost() {
		return host;
	}
	public void setHost(Host host) {
		this.host = host;
	}
	public AgentType getType() {
		return type;
	}
	public void setType(AgentType type) {
		this.type = type;
	} 

	
}

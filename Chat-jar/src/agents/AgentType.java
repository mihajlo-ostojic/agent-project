package agents;

import java.io.Serializable;

public  class AgentType implements Serializable {
	
	
	private boolean isStatefull;
	private String name;
	public AgentType(boolean isStatefull, String name) {
		super();
		this.isStatefull = isStatefull;
		this.name = name;
	}
	public AgentType() {
		super();
	}
	public boolean isStatefull() {
		return isStatefull;
	}
	public void setStatefull(boolean isStatefull) {
		this.isStatefull = isStatefull;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}

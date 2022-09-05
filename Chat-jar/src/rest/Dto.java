package rest;

import java.io.Serializable;

public class Dto implements Serializable{

	public String command;
	public String performative;
	public String agentName;
	public String agentType;
	public String id;
	public String hostAlias;
	public String store;
	public String author;
	public String title;
	
	public Dto(String command, String performative, String agentName, String agentType, String id, String halias) {
		super();
		this.command = command;
		this.performative = performative;
		this.agentName = agentName;
		this.agentType = agentType;
		this.id = id;
		this.hostAlias = halias;
	}
	public Dto() {
		super();

	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getPerformative() {
		return performative;
	}
	public void setPerformative(String performative) {
		this.performative = performative;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getAgentType() {
		return agentType;
	}
	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHostAlias() {
		return hostAlias;
	}
	public void setHostAlias(String hostAlias) {
		this.hostAlias = hostAlias;
	}
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	
	
	
}

package agents;

import java.io.Serializable;

import javax.jms.Message;

import messagemanager.ACLMessage;
import messagemanager.AgentMessage;

public interface Agent extends Serializable {

	public String init();
	public void init(String agentId);
	public void init(AID aid);
	public void handleMessage(AgentMessage message);
	public void handleMessage(ACLMessage message);
	public String getAgentId();
	
	public String getName();
}

package agentmanager;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;

import agents.AID;
import agents.Agent;
import agents.AgentType;
import messagemanager.ACLMessage;
import messagemanager.Performative;
import models.Book;
import models.Host;
import models.User;

@Remote
public interface AgentManagerRemote {
	public String startAgent(String name);
	public AID startAgent(AgentType type, String name);
	public void stopAgent(AID aid);
	public List<AID> getRunningAgentsAids();
	public List<Agent> getRunningAgents();
	public Agent getRunningAgent(AID aid);
	public List<AgentType> getTypes();
	public void startAgent(String agentId, String name);
	public Agent getAgentById(String agentId);
	public void stopAgent(String agentId);
	
	public Agent getAgentByName(String name);
	
	public void updateAgentTypes(List<AgentType> newTypes);
	public ACLMessage getMessageForOtherChatAgents(Host node, Performative performative, Object content);
	
	
	public void updateRunningAgents(List<AID> aids);
	public ArrayList<User> getAllLoggedUsers();
	public String getAllLoggedUsersAsString();
	
	public ArrayList<User> getAllRegUsers();
	public String getAllRegUsersAsString();
	
	public void logOutUsersFromNode(String alias);
	
	
	public String getRunningAgentsString();
	
	public ArrayList<Book> getStore1();
	public ArrayList<Book> getStore2();
	public ArrayList<Book> getStore3();
	
	public void setStore1(List<Book> books);
	public void setStore2(List<Book> books);
	public void setStore3(List<Book> books);
	
	public void removeAgentsBelogingToNode(Host node);

}

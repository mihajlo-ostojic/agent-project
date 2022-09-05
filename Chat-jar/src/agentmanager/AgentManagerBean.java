package agentmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

import agents.AID;
import agents.Agent;
import agents.AgentType;
import agents.CachedAgentsRemote;
import agents.ChatAgent;
import agents.CollectorAgent;
import agents.SearchAgent;
import agents.UserAgent;
import app.AgentClient;
import app.AgentRestBean;
import chatmanager.ChatManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.Performative;
import models.Book;
import models.Host;
import models.User;
import nodemanager.NodeManagerRemote;
import util.JNDILookup;
import ws.WSAgents;

/**
 * Session Bean implementation class AgentManagerBean
 */
@Stateless
@LocalBean
public class AgentManagerBean implements AgentManagerRemote {
	
	@EJB
	private NodeManagerRemote nodeManagerRemote;
	
	@EJB
	private CachedAgentsRemote cachedAgents; //user agents for chat
	
	@EJB
	private ChatManagerRemote chatManager; //user agents for chat
	
	@EJB
	private WSAgents socket;
	
	
	
	
	public ArrayList<Book> bookStore1 = new ArrayList<Book>(); 
	public ArrayList<Book> bookStore2 = new ArrayList<Book>(); 
	public ArrayList<Book> bookStore3 = new ArrayList<Book>();
	
	private Map<AID, Agent> runingAgents; // other agents
	
	private ArrayList<AgentType> agTypes;
	
    public AgentManagerBean() {
    	runingAgents = new HashMap<AID, Agent>();
    	agTypes = new ArrayList<AgentType>();
    	agTypes.add(new AgentType(true,"ChatAgent"));
    	agTypes.add(new AgentType(true,"UserAgent"));
    	agTypes.add(new AgentType(false,"CollectorAgent"));
    	agTypes.add(new AgentType(false,"SearchAgent"));
    	
    	
    	
    }
    
    @PostConstruct
	public void postConstruct() {
    	bookStore1 = new ArrayList<Book>(); 
    	bookStore2 = new ArrayList<Book>(); 
    	bookStore3 = new ArrayList<Book>();
	}

	@Override
	public String startAgent(String name) {
		Agent agent = (Agent) JNDILookup.lookUp(name, Agent.class);
		return agent.init();
	}
	
	@Override
	public void startAgent(String agentId, String name) {
		Agent agent = (Agent) JNDILookup.lookUp(name, Agent.class);
		agent.init(agentId);
		cachedAgents.addRunningAgent(agentId, agent);
		System.out.println("Cached agent id : " + agent.getAgentId());
	}

	@Override
	public Agent getAgentById(String agentId) {
		return cachedAgents.getRunningAgents().get(agentId);
	}

	@Override
	public void stopAgent(String agentId) {
		cachedAgents.stopAgent(agentId);
	}

	@Override
	public AID startAgent(AgentType type, String name) {
		
		Agent agent = null;// = (Agent) JNDILookup.agentLookUp(type);
		AID aid = new AID(name, nodeManagerRemote.getCurrentNode(), type);
		if(type.getName().equals("ChatAgent"))
		{
			Agent agenta = (Agent) JNDILookup.lookUp(JNDILookup.ChatAgentLookup, Agent.class);
			
			agent = new ChatAgent();
			agent.init(aid);
		}else if(type.getName().equals("UserAgent"))
		{
			Agent agentb = (Agent) JNDILookup.lookUp(JNDILookup.UserAgentLookup, Agent.class);
			agent = new UserAgent();
			agent.init(aid);
		}else if(type.getName().equals("CollectorAgent"))
		{
			Agent agentc = (Agent) JNDILookup.lookUp(JNDILookup.CollectorAgentLookUp, Agent.class);
			agent = new CollectorAgent();
			agent.init(aid);
		}else if(type.getName().equals("SearchAgent"))
		{
			Agent agentd = (Agent) JNDILookup.lookUp(JNDILookup.SearchAgentLookUp, Agent.class);
			agent = new SearchAgent();
			agent.init(aid);
		}

		if(agent!=null) {
			runingAgents.put(aid, agent);
		

		socket.onMessage(getRunningAgentsString());
		
		// inform rest of the nodes
		for (String nodeAlias: nodeManagerRemote.getAllAliases()) {
			new AgentClient(nodeAlias)
			.performAction(rest -> rest.updateAgents(getRunningAgentsAids()));
		}}
		return aid;
		

	}

	@Override
	public void stopAgent(AID aid) {
		List<AID> list = new ArrayList<AID>(runingAgents.keySet());
		AID foundId = null;
		for(AID id:list) {
			if(id.getName().equals(aid.getName()) && id.getHost().getAlias().equals(aid.getHost().getAlias()))
			{
				foundId = id;
				break;
			}
		}
		if(foundId!=null)
		{
			runingAgents.remove(foundId);
			socket.onMessage(getRunningAgentsString());
		}
	}

	@Override
	public List<AID> getRunningAgentsAids() {
		return new ArrayList<>(runingAgents.keySet());
	}

	@Override
	public List<Agent> getRunningAgents() {
		return new ArrayList<>(runingAgents.values());
	}

	@Override
	public Agent getRunningAgent(AID aid) {
		return runingAgents.get(aid);
	}

	@Override
	public List<AgentType> getTypes() {
			
		return agTypes;
	}

	@Override
	public ACLMessage getMessageForOtherChatAgents(Host node, Performative performative, Object content) {
		AID sender = null;
		
		
		for (AID id : getRunningAgentsAids()) {
			if (id.getHost().getAlias().equals(nodeManagerRemote.getCurrentNode().getAlias()) )
			{
				if (id.getType().getName().equals(ChatAgent.class.getSimpleName()))
				{
					sender = id;
					break;
				}
			}
		}
		
		
		List<AID> receivers =  new ArrayList<AID>();
		for (AID id : getRunningAgentsAids()) {
			if (!id.getHost().getAlias().equals(nodeManagerRemote.getCurrentNode().getAlias()) )
			{
				if (id.getType().getName().equals(ChatAgent.class.getSimpleName()))
				{
					
				}
			}
		}
		
		return new ACLMessage(performative, sender, receivers);
	
	}

	@Override
	public ArrayList<User> getAllLoggedUsers() {
		// TODO Auto-generated method stub
		return (ArrayList<User>) chatManager.loggedInUsers();
	}

	@Override
	public ArrayList<User> getAllRegUsers() {
		return (ArrayList<User>) chatManager.regeisteredUsers();
	}

	@Override
	public void logOutUsersFromNode(String alias) {
		chatManager.logOutUsersFromNode(alias);
		
	}

	@Override
	public String getAllLoggedUsersAsString() {
		
		String response = "";
		for(User u: getAllLoggedUsers())
		{
			response +=  u.getUsername()+","+u.getPassword()+"|";
		}
		
		return response;
	}

	@Override
	public String getAllRegUsersAsString() {
		String response = "";
		for(User u: getAllRegUsers())
		{
			response +=  u.getUsername()+","+u.getPassword()+"|";
		}
		
		return response;
	}

	@Override
	public void updateAgentTypes(List<AgentType> sendTypes) {
		agTypes.removeIf(curr -> !sendTypes.contains(curr));
		
		for(AgentType newType: sendTypes) {
			if (!agTypes.contains(newType)) agTypes.add(newType);
		}
		
		
		String response = "agentTypes:";
		List<AgentType> types = getTypes();
		for(AgentType type: types)
		{
			response += type.getName();
			if(type.isStatefull()) response +=",true|";
			else response +=",false|";
		}
		
		socket.onMessage(response);
	}

	@Override
	public void updateRunningAgents(List<AID> aids) {
		Host host = nodeManagerRemote.getCurrentNode();
		
		List<AID> adsInList = new ArrayList<AID>();
		
		
		for(AID id: getRunningAgentsAids())
		{
			if(!id.getHost().getAlias().equals(host.getAlias()))
			{
				adsInList.add(id);
			}
		}
		
		List<AID> adsToAdd = new ArrayList<AID>();
		
		for(AID id: aids)
		{
			if(!id.getHost().getAlias().equals(host.getAlias()))
			{
				adsToAdd.add(id);
			}
			
		}
		
		for(AID id: adsInList) {
			if (!adsToAdd.contains(id)) {
				runingAgents.remove(id);
			}
		}
		
		for(AID id: adsToAdd) {
			if (!adsInList.contains(id))
				runingAgents.put(id, null);
		}
		
		socket.onMessage(getRunningAgentsString());
		
	}

	@Override
	public String getRunningAgentsString() {
		String response = "agentAIDs:";
		for(AID id: getRunningAgentsAids())
		{
			response += id.getName() + ","+ id.getType().getName() + "," + id.getHost().getAlias()+"|";
		}
		return response;
	}

	@Override
	public ArrayList<Book> getStore1() {
		return cachedAgents.getStore1();
	}

	@Override
	public ArrayList<Book> getStore2() {
		// TODO Auto-generated method stub
		return cachedAgents.getStore2();
	}

	@Override
	public ArrayList<Book> getStore3() {
		// TODO Auto-generated method stub
		return cachedAgents.getStore3();
	}

	@Override
	public void setStore1(List<Book> books) {
		cachedAgents.setStore1(books);
	}

	@Override
	public void setStore2(List<Book> books) {
		cachedAgents.setStore2(books);
		
	}

	@Override
	public void setStore3(List<Book> books) {
		cachedAgents.setStore3(books);
		
	}

	@Override
	public Agent getAgentByName(String name) {
		
		List<Agent> agents = getRunningAgents();
		Agent found = null;
		for(Agent a: agents)
		{
			if(a.getName().equals(name)) {
				found = a;
				break;
			}
		}
		
		return found;
	}

	@Override
	public void removeAgentsBelogingToNode(Host node) {
		runingAgents.keySet().removeIf(k -> k.getHost().getAlias().equals(node.getAlias()));
		socket.onMessage(getRunningAgentsString());
		
	}
	




}

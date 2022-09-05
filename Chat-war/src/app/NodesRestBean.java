package app;



import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.Path;

import org.jboss.security.auth.spi.Users;

import agentmanager.AgentManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.Performative;
import ws.WSChat;


import nodemanager.*;
import models.Host;
import models.User;

@Singleton
@Startup
@Remote(NodesRest.class)
@Path("/nodes")
public class NodesRestBean implements NodesRest{

	@EJB
	private NodeManagerRemote nodeManager;
	
	
	@EJB 
	private AgentManagerRemote agentManager;
	
	
	@EJB
	private WSChat socket;
	
	
	@PostConstruct
	private void init() {
		if (nodeManager.getCurrentNode().getMasterAlias() != null)
		{
			System.out.println("******************************************");
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("This node is slave");
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("******************************************");
			connectToMaster();
		}else
		{
			System.out.println("******************************************");
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("This node is master");
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("******************************************");
		}
	}
	
	@PreDestroy
	private void shutDown() {
		sendOtherNodesNodeSD(nodeManager.getCurrentNode());
	}
	
	//slave is connection to master
	private void connectToMaster() {
			new NodeClient(nodeManager.getCurrentNode().getMasterAlias())
			.performAction((rest) -> rest.registerNewNode(nodeManager.getCurrentNode()));
	}
	

	@Schedule(hour = "*", minute = "*", second = "*/10", persistent = false)//TODO: OVDE PRE / IDE *
	private void heartbeat() {
		System.out.println("start connection");
		for(String nodeAlias : nodeManager.getAllAliases()) {
			
			System.out.println("ping in progress: " + nodeAlias  );
			new NodeClient(nodeAlias)
						.performAction(rest -> {
				int cnt = 1;
				while (cnt <= 2) {
					try {
						boolean temp = rest.ping();
						if (temp) 
						{
							System.out.println(" node: " + nodeAlias + " responded: " + cnt);
							return;
						}
					} catch (Exception e) {
						System.out.println(" node: " + nodeAlias + " not responded: " + cnt);
					} finally {
						cnt++;						
					}
		}
				
			System.out.println(" Node: " + nodeAlias + " is dead");
				
				Host node = nodeManager.getNode(nodeAlias);
				removNode(node);
				sendOtherNodesNodeSD(node);
				
			});
			
		}
	}
	
	
	@Override
	public void registerNewNode(Host node) {
		System.out.println(String.format("Registering new node: %s", node.getAlias()));
		nodeManager.addNode(node);
		sendALLNodesNewNode(node);
		sendNewNodeRestNodes(node);

		sendAgentTypesToNewNode(node);
		sendRunnigAgentToNewNode(node);
		getAgentTypesFromNode(node);
		getAgentFromNode(node);
		
//		sendLogedUsersToNewNode(node);
//		sendRegisteredUsersToNewNode(node);
		
	}

	@Override
	public void addNewNode(Host node) {
		nodeManager.addNode(node);
	}

	@Override
	public void removNode(Host node) {
		System.out.println("*** Removing node: " + node.getAlias());
		String nodeAlias = node.getMasterAlias() == null ? nodeManager.getCurrentNode().getMasterAlias() : node.getAlias();
		nodeManager.removeNode(nodeAlias);
		agentManager.logOutUsersFromNode(node.getAlias());
		sendUsersInfoAboutLogedUsersAfterNodeFail();
		agentManager.removeAgentsBelogingToNode(node);
		
	}

	@Override
	public void reciveNodes(ArrayList<Host> nodes) {
		
		System.out.println("*** Receiving nodes:");
		Host master = new Host("", nodeManager.getCurrentNode().getMasterAlias(),"");
		nodeManager.addNode(master);
		
		for (Host node: nodes) {
			
			nodeManager.addNode(node);
		}
		
	}

	@Override
	public boolean ping() {
		System.out.println("This node is pinged");
		return true;
	}
	
	

	//master

	private void sendALLNodesNewNode(Host newNode) {
		for (String existingNodeAlias: nodeManager.getAllAliases()) {
			if (existingNodeAlias.equals(newNode.getAlias())) continue;
			
			new NodeClient(existingNodeAlias)
				.performAction(rest -> rest.addNewNode(newNode));
		}
	}
	

	private void sendNewNodeRestNodes(Host node) {
		ArrayList<Host> nodes = new ArrayList<Host>(nodeManager.getAllNodes());
		new NodeClient(node.getAlias())
			.performAction(rest -> rest.reciveNodes(nodes));
	}


	private void sendLogedUsersToNewNode(Host node) {
		ACLMessage msg = agentManager.getMessageForOtherChatAgents(
				node, 
				Performative.GET_LOGGED_USERS_FROM_MASTER, 
				agentManager.getAllLoggedUsers());
		String temp = agentManager.getAllLoggedUsersAsString();
		String temp2 = nodeManager.getCurrentNode().getAlias();
		String temp3 = nodeManager.getCurrentNode().getAddress();
		
		msg.userArgs.put("logedusers", temp);
		msg.userArgs.put("nodealias", temp2);
		msg.userArgs.put("nodeaddress", temp3);
		new MessageClient(node.getAlias()).performAction(rest -> rest.sendMessage(msg));
	}


	private void sendRegisteredUsersToNewNode(Host node) {
		ACLMessage msg = agentManager.getMessageForOtherChatAgents(
				node, 
				Performative.GET_REGISTERED_USER_FROM_MASTER, 
				agentManager.getAllRegUsers());
		msg.userArgs.put("regusers", agentManager.getAllRegUsersAsString());
		msg.userArgs.put("nodealias", nodeManager.getCurrentNode().getAlias());
		msg.userArgs.put("nodeaddress", nodeManager.getCurrentNode().getAddress());
		
		new MessageClient(node.getAlias()).performAction(rest -> rest.sendMessage(msg));
	}
	
	
	//slave

	private void sendOtherNodesNodeSD(Host node) {
		for (String nodeAlias: nodeManager.getAllAliases()) {
			new NodeClient(nodeAlias)
			.performAction(rest -> rest.removNode(node));
		}
	}
	
	private void sendUsersInfoAboutLogedUsersAfterNodeFail() {

		String response = "loggedInList:";
		List<User> users = agentManager.getAllLoggedUsers();
		for (User u : users) {
			response += u.getUsername() + "|";
		}
		for (User u : users) {
			if(u.getId()!=null || !u.getId().isEmpty())
				socket.onMessage(u.getId(), response);
		}

	}
	
	
		private void sendAgentTypesToNewNode(Host node) {
			new AgentClient(node.getAlias())
			.performAction(rest -> rest.updateTypes(agentManager.getTypes()));
		}

	
		private void sendRunnigAgentToNewNode(Host node) {
			new AgentClient(node.getAlias())
			.performAction(rest -> rest.updateAgents(agentManager.getRunningAgentsAids()));
		}
	
	
		
		private void getAgentTypesFromNode(Host node) {
			new AgentClient(node.getAlias())
			.performAction(rest -> agentManager.updateAgentTypes(rest.getAllTypes()));
		}
		
		
		private void getAgentFromNode(Host node) {
			new AgentClient(node.getAlias())
			.performAction(rest -> agentManager.updateRunningAgents(rest.getAllAgents()));
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

}

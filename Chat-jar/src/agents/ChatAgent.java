package agents;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import agentmanager.AgentManagerRemote;
import app.MessageClient;
import chatmanager.ChatManagerRemote;
import messagemanager.AgentMessage;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.Performative;
import models.Host;
import models.User;
import nodemanager.NodeManagerRemote;
import util.JNDILookup;
import ws.WSChat;

@Stateful
@Remote(Agent.class)
public class ChatAgent implements Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String agentId;
	private AID aId;

	@EJB
	private ChatManagerRemote chatManager;
	@EJB
	private CachedAgentsRemote cachedAgents;
	@EJB
	private MessageManagerRemote messageMenager;
	@EJB
	private WSChat ws;

	@EJB
	private NodeManagerRemote nodeManager;
	
	@EJB
	private AgentManagerRemote agentManager;
	
	
	@PostConstruct
	public void postConstruct() {
		System.out.println("Created Chat Agent!");
	}

	//private List<String> chatClients = new ArrayList<String>();

	protected MessageManagerRemote msm() {
		return (MessageManagerRemote) JNDILookup.lookUp(JNDILookup.MessageManagerLookup, MessageManagerRemote.class);
	}
	
	
	@Override
	public void handleMessage(AgentMessage message) {
//		TextMessage tmsg = (TextMessage) message;
		
		String receiver;
		try {
			receiver = (String) message.getUserArg("receiver");
			String sessionId = (String) message.getUserArg("sessionId");
			if (agentId.equals(receiver)) {
				String option = "";
				String response = "";
				try {
					option = (String) message.getUserArg("command");
					switch (option) {
					case "REGISTER":
						String username = (String) message.getUserArg("username");
						String password = (String) message.getUserArg("password");
	
						boolean result = chatManager.register(new User(username, password));

						response = "register:" + (result ? "OK "+username : "NO: Not registered!");
						
						for (Agent agent : new ArrayList<Agent>(cachedAgents.getRunningAgents().values())) {
							ACLMessage newMessage = new ACLMessage();
							newMessage.userArgs.put("sessionId",sessionId);
							newMessage.userArgs.put("sender", "chat");
							newMessage.userArgs.put("receiver", agent.getAgentId());
							newMessage.userArgs.put("command", "NEW_REGISTER");
							newMessage.userArgs.put("content", "c");
							newMessage.userArgs.put("date", "d");
							newMessage.userArgs.put("subject", "s");
							if(!agent.getAgentId().equals("chat")) {
								messageMenager.post(newMessage);
							}
						}
						
						break;
					case "LOG_IN":
						username = (String) message.getUserArg("username");
						password = (String) message.getUserArg("password");

						result = chatManager.login(username, password,sessionId);

						response = "login:OK id" + (result ? username : "No!");
						
						for (Agent agent : new ArrayList<Agent>(cachedAgents.getRunningAgents().values())) {
							ACLMessage newMessage = new ACLMessage();
							newMessage.userArgs.put("sessionId",sessionId);
							newMessage.userArgs.put("sender", "chat");
							newMessage.userArgs.put("receiver", agent.getAgentId());
							newMessage.userArgs.put("command", "NEW_LOGIN");
							newMessage.userArgs.put("content", "c");
							newMessage.userArgs.put("date", "d");
							newMessage.userArgs.put("subject", "s");
							if(!agent.getAgentId().equals("chat")) {
								messageMenager.post(newMessage);
							}
						}
						
						break;
					case "GET_LOGGEDIN":
						response = "loggedInList:";
						List<User> users = chatManager.loggedInUsers();
						for (User u : users) {
							response += u.getUsername() + "|";
						}

						break;
					case "GET_REGISTERED":
						response = "registeredList:";
						List<User> users2 = chatManager.regeisteredUsers();
						for (User u : users2) {
							response += u.getUsername() + "|";
						}

						break;
					case "LOGOUT" :
						username = (String) message.getUserArg("username");
						password = (String) message.getUserArg("password");
						result = chatManager.logout(username, password);
						response = "logout:OK id" + (result ? username : "No!");
						
						for (Agent agent : new ArrayList<Agent>(cachedAgents.getRunningAgents().values())) {
							ACLMessage newMessage = new ACLMessage();
							newMessage.userArgs.put("sessionId",sessionId);
							newMessage.userArgs.put("sender", "chat");
							newMessage.userArgs.put("receiver", agent.getAgentId());
							newMessage.userArgs.put("command", "NEW_LOGOUT");
							newMessage.userArgs.put("content", "c");
							newMessage.userArgs.put("date", "d");
							newMessage.userArgs.put("subject", "s");
							if(!agent.getAgentId().equals("chat")) {
								messageMenager.post(newMessage);
							}
						}
						
						break;
					case "SEND_ALL" :
						String sender = (String) message.getUserArg("sender");
						String realsender = (String) message.getUserArg("realsender");
						String content = (String) message.getUserArg("content");
						String date = (String) message.getUserArg("date");
						String subject = (String) message.getUserArg("subject");
						for (Agent agent : new ArrayList<Agent>(cachedAgents.getRunningAgents().values())) {
							ACLMessage newMessage = new ACLMessage();
							newMessage.userArgs.put("sessionId",sessionId);
							newMessage.userArgs.put("sender", sender);
							newMessage.userArgs.put("receiver", agent.getAgentId());
							newMessage.userArgs.put("command", "RECIVE_MESSAGE");
							newMessage.userArgs.put("content", content);
							newMessage.userArgs.put("date", date);
							newMessage.userArgs.put("subject", subject);
							newMessage.userArgs.put("realsender", realsender);
							if(!agent.getAgentId().equals("chat")) {
//								agent.handleMessage(message);
								messageMenager.post(newMessage);
							}
						}
						response = "messages:ALL send";
						break;
					default:
						response = "ERROR!Option: " + option + " does not exist.";
						break;
					}
					System.out.println(response);
					ws.onMessage(sessionId, response);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public String init() {
		agentId = "chat";
		cachedAgents.addRunningAgent(agentId, this);
		return agentId;
	}

	@Override
	public String getAgentId() {
		return agentId;
	}

	@Override
	public void init(String agentId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMessage(ACLMessage message) {
		
		System.out.println("chat agent dobio nekakvu poruku");
		String receiver;
		try {
			receiver = (String)  message.userArgs.get("receiver");
			String sessionId = (String) message.userArgs.get("sessionId");
			if (true) {
				String option = "";
				String response = "";
				try {
					option = (String) message.userArgs.get("command");
					System.out.println("chat agent dobio komandu: "+option);
					switch (option) {
					case "REGISTER":
						String username = (String) message.userArgs.get("username");
						String password = (String) message.userArgs.get("password");
	
						boolean result = chatManager.register(new User(username, password));
						
						String nodeAlias = nodeManager.getCurrentNode().getAlias();
						String nodeAddress = nodeManager.getCurrentNode().getAddress();
						
						if(result) {
							
							// send all nodes registered users
							
							for (Host node: nodeManager.getAllNodes()) {
								new MessageClient(node.getAlias())
								.performAction(rest -> {
									ACLMessage temp = agentManager.getMessageForOtherChatAgents(node, Performative.REGISTER_USER_FROM_NODE, username+"|"+password);
									temp.userArgs.put("receiver", "chat");
									temp.userArgs.put("nodealias", nodeAlias);
									temp.userArgs.put("nodeaddress", nodeAddress);
									temp.userArgs.put("username", username);
									temp.userArgs.put("password", password);
									rest.sendMessage(temp);
								});
							}
							
						}

						response = "register:" + (result ? "OK "+username : "NO: Not registered!");
						
						for (Agent agent : new ArrayList<Agent>(cachedAgents.getRunningAgents().values())) {
							ACLMessage newMessage = new ACLMessage();
							newMessage.userArgs.put("sessionId",sessionId);
							newMessage.userArgs.put("sender", "chat");
							newMessage.userArgs.put("receiver", agent.getAgentId());
							newMessage.userArgs.put("command", "NEW_REGISTER");
							newMessage.userArgs.put("content", "c");
							newMessage.userArgs.put("date", "d");
							newMessage.userArgs.put("subject", "s");
							if(!agent.getAgentId().equals("chat")) {
								messageMenager.post(newMessage);
							}
						}
						
						break;
					case "LOG_IN":
						username = (String) message.userArgs.get("username");
						password = (String) message.userArgs.get("password");
						nodeAlias = nodeManager.getCurrentNode().getAlias();
						nodeAddress = nodeManager.getCurrentNode().getAddress();
						result = chatManager.login(username, password,sessionId);
						
						if(result)
						{
							// send all nodes loged in users
							for (Host node: nodeManager.getAllNodes()) {
								new MessageClient(node.getAlias())
								.performAction(rest -> {
									ACLMessage temp = agentManager.getMessageForOtherChatAgents(node, Performative.USERLOGIN_FROM_NODE, username+"|"+password);
									temp.userArgs.put("nodealias", nodeAlias);
									temp.userArgs.put("nodeaddress", nodeAddress);
									temp.userArgs.put("username", username);
									temp.userArgs.put("password", password);
									
									rest.sendMessage(temp);
								});
							}
						}

						response = "login:OK id" + (result ? username : "No!");
						
						for (Agent agent : new ArrayList<Agent>(cachedAgents.getRunningAgents().values())) {
							ACLMessage newMessage = new ACLMessage();
							newMessage.userArgs.put("sessionId",sessionId);
							newMessage.userArgs.put("sender", "chat");
							newMessage.userArgs.put("receiver", agent.getAgentId());
							newMessage.userArgs.put("command", "NEW_LOGIN");
							newMessage.userArgs.put("content", "c");
							newMessage.userArgs.put("date", "d");
							newMessage.userArgs.put("subject", "s");
							if(!agent.getAgentId().equals("chat")) {
								messageMenager.post(newMessage);
							}
						}
						
						break;
					case "GET_LOGGEDIN":
						response = "loggedInList:";
						List<User> users = chatManager.loggedInUsers();
						for (User u : users) {
							response += u.getUsername() + "|";
						}

						break;
					case "GET_REGISTERED":
						response = "registeredList:";
						List<User> users2 = chatManager.regeisteredUsers();
						for (User u : users2) {
							response += u.getUsername() + "|";
						}

						break;
					case "LOGOUT" :
						username = (String) message.userArgs.get("username");
						password = (String) message.userArgs.get("password");
						result = chatManager.logout(username, password);
						response = "logout:OK id" + (result ? username : "No!");
						nodeAlias = nodeManager.getCurrentNode().getAlias();
						nodeAddress = nodeManager.getCurrentNode().getAddress();
						if(result)
						{
							// send other nodes all loged in users
							for (Host node: nodeManager.getAllNodes()) {
								new MessageClient(node.getAlias())
								.performAction(rest -> {
									ACLMessage temp = agentManager.getMessageForOtherChatAgents(node, Performative.USERLOGED_OUT_FROM_NODE, username+"|"+password);
									temp.userArgs.put("nodealias", nodeAlias);
									temp.userArgs.put("nodeaddress", nodeAddress);
									temp.userArgs.put("username", username);
									temp.userArgs.put("password", password);
									rest.sendMessage(temp);
								});
							}
						}
						
						for (Agent agent : new ArrayList<Agent>(cachedAgents.getRunningAgents().values())) {
							ACLMessage newMessage = new ACLMessage();
							newMessage.userArgs.put("sessionId",sessionId);
							newMessage.userArgs.put("sender", "chat");
							newMessage.userArgs.put("receiver", agent.getAgentId());
							newMessage.userArgs.put("command", "NEW_LOGOUT");
							newMessage.userArgs.put("content", "c");
							newMessage.userArgs.put("date", "d");
							newMessage.userArgs.put("subject", "s");
							if(!agent.getAgentId().equals("chat")) {
								messageMenager.post(newMessage);
							}
						}
						
						break;
					case "SEND_ALL" :
						String sender = (String) message.userArgs.get("sender");
						String realsender = (String) message.userArgs.get("realsender");
						String content = (String) message.userArgs.get("content");
						String date = (String) message.userArgs.get("date");
						String subject = (String) message.userArgs.get("subject");
						
						
						//SEND TO OTHER NODES
						// 
						ACLMessage newMessageForNodes = new ACLMessage();
						newMessageForNodes.userArgs.put("sessionId",sessionId);
						newMessageForNodes.userArgs.put("sender", sender);
						newMessageForNodes.userArgs.put("command", "RECIVE_MESSAGE");
						newMessageForNodes.userArgs.put("content", content);
						newMessageForNodes.userArgs.put("date", date);
						newMessageForNodes.userArgs.put("subject", subject);
						newMessageForNodes.userArgs.put("realsender", realsender);
						newMessageForNodes.performative = Performative.SEND_MESSAGE_ALL;
						
						for (Host node: nodeManager.getAllNodes()) {
							new MessageClient(node.getAlias())
							.performAction(rest -> {
								rest.sendMessage(newMessageForNodes);
							});
						}
						
						
						for (Agent agent : new ArrayList<Agent>(cachedAgents.getRunningAgents().values())) {
							ACLMessage newMessage = new ACLMessage();
							newMessage.userArgs.put("sessionId",sessionId);
							newMessage.userArgs.put("sender", sender);
							newMessage.userArgs.put("receiver", agent.getAgentId());
							newMessage.userArgs.put("command", "RECIVE_MESSAGE");
							newMessage.userArgs.put("content", content);
							newMessage.userArgs.put("date", date);
							newMessage.userArgs.put("subject", subject);
							newMessage.userArgs.put("realsender", realsender);
							if(!agent.getAgentId().equals("chat")) {
//								agent.handleMessage(message);
								messageMenager.post(newMessage);
							}
						}
						response = "messages:ALL send";
						break;
					case "REGISTER_USER_FROM_NODE":
						username = (String) message.userArgs.get("username");
						password = (String) message.userArgs.get("password");
						String nodealias = (String) message.userArgs.get("nodealias");
						String nodeaddress = (String) message.userArgs.get("nodeaddress");
						User u1 = new User(username,password);
						u1.setHost(new Host("", nodealias, nodeaddress));
						chatManager.registerFromNode(u1);
						
						List<User> usersloged = chatManager.regeisteredUsers();
						List<User> realusersloged = chatManager.loggedInUsers();
						response = "registeredList:";
						for (User u : usersloged) {
							response += u.getUsername() + "|";
						}
						for(User u : realusersloged )
						{
							if(u.getId()==null || u.getId().isEmpty())
							{
								
							}
							else {
								ws.onMessage(u.getId(), response);
							}
						}
						return;
						
					case "USERLOGIN_FROM_NODE":
						username = (String) message.userArgs.get("username");
						password = (String) message.userArgs.get("password");
						nodealias = (String) message.userArgs.get("nodealias");
						nodeaddress = (String) message.userArgs.get("nodeaddress");
						u1 = new User(username,password);
						u1.setHost(new Host("", nodealias, nodeaddress));
						chatManager.loginFromNode(u1);
						
						usersloged = chatManager.loggedInUsers();
						response = "loggedInList:";
						for (User u : usersloged) {
							response += u.getUsername() + "|";
						}
						for(User u : usersloged)
						{
							if(u.getId()==null || u.getId().isEmpty())
							{
								
							}
							else {
								ws.onMessage(u.getId(), response);
							}
						}
						return;
					case "USERLOGED_OUT_FROM_NODE":
						username = (String) message.userArgs.get("username");
						password = (String) message.userArgs.get("password");
						nodealias = (String) message.userArgs.get("nodealias");
						nodeaddress = (String) message.userArgs.get("nodeaddress");
						u1 = new User(username,password);
						u1.setHost(new Host("", nodealias, nodeaddress));
						chatManager.logoutFromNode(u1.getUsername());
						response = "loggedInList:";
						usersloged = chatManager.loggedInUsers();
						for (User u : usersloged) {
							response += u.getUsername() + "|";
						}
						for(User u : usersloged)
						{
							if(u.getId()==null || u.getId().isEmpty())
							{
								
							}
							else {
								ws.onMessage(u.getId(), response);
							}
						}
						return;
					case "SEND_MESSAGE_ALL":
						
						sender = (String) message.userArgs.get("sender");
						realsender = (String) message.userArgs.get("realsender");
						content = (String) message.userArgs.get("content");
						date = (String) message.userArgs.get("date");
						subject = (String) message.userArgs.get("subject");
						
						
						for (Agent agent : new ArrayList<Agent>(cachedAgents.getRunningAgents().values())) {
							ACLMessage newMessage = new ACLMessage();
							newMessage.userArgs.put("sessionId",sessionId);
							newMessage.userArgs.put("sender", sender);
							newMessage.userArgs.put("receiver", agent.getAgentId());
							newMessage.userArgs.put("command", "RECIVE_MESSAGE");
							newMessage.userArgs.put("content", content);
							newMessage.userArgs.put("date", date);
							newMessage.userArgs.put("subject", subject);
							newMessage.userArgs.put("realsender", realsender);
							if(!agent.getAgentId().equals("chat")) {
								messageMenager.post(newMessage);
							}
						}
						response = "messages:ALL send";
						return;
					case "GET_LOGGED_USERS_FROM_MASTER" :
						String lusers = (String) message.userArgs.get("logedusers");
						String nal = (String) message.userArgs.get("nodealias");
						String nad = (String) message.userArgs.get("nodeaddress");
						
						//ucitati node podatake i to poslati
						chatManager.getLogUsersFromMaster(lusers,nal,nad);

						usersloged = chatManager.loggedInUsers();
						for (User u : usersloged) {
							response += u.getUsername() + "|";
						}
						for(User u : usersloged)
						{
							if(u.getId()==null || u.getId().isEmpty())
							{
								
							}
							else {
								ws.onMessage(u.getId(), response);
							}
						}
						return;
					case "GET_REGISTERED_USER_FROM_MASTER" :
						String regusers = (String) message.userArgs.get("regusers");
						
						nal = (String) message.userArgs.get("nodealias");
						nad = (String) message.userArgs.get("nodeaddress");
						//ucitati node podatake i to poslati
						chatManager.getRegUsersFromMaster(regusers,nal,nad);

						
						realusersloged = chatManager.loggedInUsers();
						List<User> reguserss = chatManager.regeisteredUsers();
						for (User u : reguserss) {
							response += u.getUsername() + "|";
						}
						for(User u : realusersloged)
						{
							if(u.getId()==null || u.getId().isEmpty())
							{
								
							}
							else {
								ws.onMessage(u.getId(), response);
							}
						}
						return;
					default:
						response = "ERROR!Option: " + option + " does not exist.";
						break;
					}
					System.out.println(response);
					ws.onMessage(sessionId, response);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}

	@Override
	public void init(AID aid) {
		this.aId = aid;
		
	}

	@Override
	public String getName() {
		return aId.getName();
	}
}

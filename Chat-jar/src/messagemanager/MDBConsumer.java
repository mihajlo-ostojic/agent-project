package messagemanager;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import agentmanager.AgentManagerRemote;
import agents.Agent;
import agents.CachedAgentsRemote;
import app.MessageClient;
import chatmanager.ChatManagerRemote;
import models.Host;
import models.User;
import nodemanager.NodeManagerRemote;

/**
 * Message-Driven Bean implementation class for: MDBConsumer
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/topic/publicTopic") })
public class MDBConsumer implements MessageListener {


	@EJB
	private CachedAgentsRemote cachedAgents;
	
	@EJB
	private NodeManagerRemote nodeManager;
	
	@EJB
	private ChatManagerRemote chatManager;
	
	@EJB
	private AgentManagerRemote agentManager;
	/**
	 * Default constructor.
	 */
	public MDBConsumer() {

	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		String receiver;
		try {
			
			
			

			
			if(checkIfOtherAgentMessage(message))
			{
				receiver = (String) message.getObjectProperty("reciver");
				String socket = (String) message.getObjectProperty("socket");
				String comm = (String) message.getObjectProperty("command");
				String perf = (String) message.getObjectProperty("performative");
				String store = (String) message.getObjectProperty("store");
				ACLMessage newColSerMessg = new ACLMessage();
				newColSerMessg.userArgs.put("reciver", receiver);
				newColSerMessg.userArgs.put("socket", socket);
				newColSerMessg.userArgs.put("command", comm);
				newColSerMessg.userArgs.put("performative", perf);
				newColSerMessg.userArgs.put("store", store);
				
				Agent agentsc = agentManager.getAgentByName(receiver);
				agentsc.handleMessage(newColSerMessg);
				return;
				
			}
			
			if(checkIfMessageIfForOtherNode(message))
				return;
			
			receiver = (String) message.getObjectProperty("receiver");
			
			
			String sender = (String)  message.getObjectProperty("sender");
			String content = (String) message.getObjectProperty("content");
			String date = (String) message.getObjectProperty("date");
			String subject = (String) message.getObjectProperty("subject");
			String command = (String) message.getObjectProperty("command");
			String sessionId = (String) message.getObjectProperty("sessionId");
			String username = (String) message.getObjectProperty("username");
			String password = (String) message.getObjectProperty("password");
			String realsender = (String) message.getObjectProperty("realsender");
			String nodealias = (String) message.getObjectProperty("nodealias");
			String nodeaddress = (String) message.getObjectProperty("nodeaddress");
			System.out.println("salje se poruka za "+receiver);
			HashMap<String,Agent> angenti = cachedAgents.getRunningAgents();
			ArrayList<String> keyList = new ArrayList<String>(cachedAgents.getRunningAgents().keySet());
			ACLMessage newMsg = new ACLMessage();
			
//			AgentMessage newMsg = new AgentMessage();
			String performative = (String) message.getObjectProperty("performative");
			boolean result = checkPerformative(performative);
			
			if(result)
			{
				ACLMessage messageForChat = new ACLMessage();
//				messageForChat.userArgs.put("command", command);
				receiver = "chat";
//				messageForChat.userArgs.put("receiver", receiver);
				messageForChat.userArgs.put("sender", sender);
				messageForChat.userArgs.put("content", content);
				messageForChat.userArgs.put("date", date);
				messageForChat.userArgs.put("subject", subject);
				
				messageForChat.userArgs.put("nodealias", nodealias);
				messageForChat.userArgs.put("nodeaddress", nodeaddress);
				
				
				messageForChat.userArgs.put("sessionId", sessionId);
				messageForChat.userArgs.put("username", username);
				messageForChat.userArgs.put("password", password);
				messageForChat.userArgs.put("realsender", realsender);
				if(performative.equals(Performative.REGISTER_USER_FROM_NODE.toString()))
				{
					receiver = "chat";
					messageForChat.userArgs.put("command", "REGISTER_USER_FROM_NODE");
					
				}else if(performative.equals(Performative.USERLOGIN_FROM_NODE.toString()))
				{
					receiver = "chat";
					messageForChat.userArgs.put("command", "USERLOGIN_FROM_NODE");
					
				}else if(performative.equals(Performative.USERLOGED_OUT_FROM_NODE.toString()))
				{
					receiver = "chat";
					messageForChat.userArgs.put("command", "USERLOGED_OUT_FROM_NODE");
					
				}else if(performative.equals(Performative.SEND_MESSAGE_ALL.toString()))
				{
					receiver = "chat";
					messageForChat.userArgs.put("command", "SEND_MESSAGE_ALL");
					
				}else if(performative.equals(Performative.SEND_MESSAGE_USER_IN_SOME_NODE.toString())) {
					receiver = (String) message.getObjectProperty("receiver");
					messageForChat.userArgs.put("command", "RECIVE_MESSAGE");
					
				}else if(performative.equals(Performative.GET_LOGGED_USERS_FROM_MASTER.toString())) {
					receiver = "chat";
					messageForChat.userArgs.put("command", "GET_LOGGED_USERS_FROM_MASTER");
					messageForChat.userArgs.put("logedusers", (String) message.getObjectProperty("logedusers"));
				}else if(performative.equals(Performative.GET_REGISTERED_USER_FROM_MASTER.toString())) {
					receiver = "chat";
					messageForChat.userArgs.put("command", "GET_REGISTERED_USER_FROM_MASTER");
				
				}
				messageForChat.userArgs.put("nodealias",(String) message.getObjectProperty("nodealias"));
				messageForChat.userArgs.put("nodeaddress",(String) message.getObjectProperty("nodeaddress"));
				System.out.println("Salje se poruka za: "+receiver);
				System.out.println("Salje se poruka sa komandom: "+messageForChat.userArgs.get("command"));
				
				messageForChat.userArgs.put("receiver", receiver);

				Agent agent1 = (Agent) cachedAgents.getRunningAgents().get(receiver);
				agent1.handleMessage(messageForChat);
				
				
			}else {
			newMsg.userArgs.put("command", command);
			newMsg.userArgs.put("receiver", receiver);
			newMsg.userArgs.put("sender", sender);
			newMsg.userArgs.put("content", content);
			newMsg.userArgs.put("date", date);
			newMsg.userArgs.put("subject", subject);
			newMsg.userArgs.put("sessionId", sessionId);
			newMsg.userArgs.put("username", username);
			newMsg.userArgs.put("password", password);
			newMsg.userArgs.put("realsender", realsender);
			Agent agent = (Agent) cachedAgents.getRunningAgents().get(receiver);
			agent.handleMessage(newMsg);
			}
		

		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean checkPerformative(String performative)
	{
		
		if (performative == null || performative.isEmpty())
		{
			return false;
		}
		
		
		return true;
	}
	
	private boolean checkIfMessageIfForOtherNode(Message message)
	{
		ACLMessage messageForUser = new ACLMessage();
		messageForUser.performative = Performative.SEND_MESSAGE_USER_IN_SOME_NODE;
		try {
			String receiver = (String) message.getObjectProperty("receiver");
			String sender = (String)  message.getObjectProperty("sender");
			String content = (String) message.getObjectProperty("content");
			String date = (String) message.getObjectProperty("date");
			String subject = (String) message.getObjectProperty("subject");
			String command = (String) message.getObjectProperty("command");
			String sessionId = (String) message.getObjectProperty("sessionId");
			String username = (String) message.getObjectProperty("username");
			String password = (String) message.getObjectProperty("password");
			String realsender = (String) message.getObjectProperty("realsender");
			
			if(receiver==null || receiver.isEmpty())
				return false;
			
			User foundUser = null;
			for(User u: chatManager.loggedInUsers())
			{
				if(u.getUsername().equals(receiver)) {
					foundUser = u;
					if(u.getHost().getAlias().equals(nodeManager.getCurrentNode().getAlias()))
					{
						return false;
					}
				}
			}
			
			
			messageForUser.userArgs.put("command", command);
			messageForUser.userArgs.put("receiver", receiver);
			messageForUser.userArgs.put("sender", sender);
			messageForUser.userArgs.put("content", content);
			messageForUser.userArgs.put("date", date);
			messageForUser.userArgs.put("subject", subject);
			messageForUser.userArgs.put("sessionId", sessionId);
			messageForUser.userArgs.put("username", username);
			messageForUser.userArgs.put("password", password);
			messageForUser.userArgs.put("realsender", realsender);
			
			if(foundUser!=null) {
			for (Host node: nodeManager.getAllNodes()) {
				if(node.getAlias().equals(foundUser.getHost().getAlias())) {
					new MessageClient(node.getAlias())
					.performAction(rest -> {
						rest.sendMessage(messageForUser);
					});
				}
				System.out.println("Sending message to other node");
			}}else {return false;}
			
			return true;
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	
	
	private boolean checkIfOtherAgentMessage(Message message)
	{
		try {
			if(message.getObjectProperty("forchat").equals("yes"))
			{
				return true;
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

}

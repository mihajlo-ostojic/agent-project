package agents;

import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import chatmanager.ChatManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import models.ChatMessages;
import models.User;
import ws.WSChat;

@Stateful
@Remote(Agent.class)
public class UserAgent implements Agent {

	/**
	 * 
	 */
	private AID id;
	private static final long serialVersionUID = 1L;
	private String agentId;
	private AID aId;
	@EJB
	private CachedAgentsRemote cachedAgents;
	
	@EJB
	private ChatManagerRemote chatManager;
	
	@EJB
	private MessageManagerRemote messageManager;
	
	private ChatMessages chatMessages;
	
	@EJB
	private WSChat ws;

	@PostConstruct
	public void postConstruct() {
		System.out.println("Created User Agent!");
		chatMessages = new ChatMessages();
	}
	
	/*public void handleMessage(AgentMessage msg) {
//		TextMessage tmsg = (TextMessage) msg;
		String receiver;
		String sender;
		String message;
		try {
			receiver = (String) message.userArgs.get("receiver");
			sender = (String) message.userArgs.get("sender");
			message = (String) message.userArgs.get("message");
			if (receiver.equals(agentId)) {
				System.out.println("Received from :" + sender);
				
				
				String option = "";
				String response = "";
				try {
					option = (String) message.userArgs.get("command");
					switch (option) {
					case "RECIVE_MESSAGE":
						String temp = (String) message.userArgs.get("content");
						String subjecttemp = (String) message.userArgs.get("subject");
						String datetemp = (String) message.userArgs.get("date");
						String realsender = (String) message.userArgs.get("realsender");
						models.Message newMsg = new models.Message(sender,receiver,temp,datetemp,subjecttemp, realsender);
						chatMessages.receiveMessage(newMsg);
						if(realsender!=null)
							newMsg.setSender("ALL");
						response = "message:"+newMsg.toString();
						String idGnenta = getAgentId();
						String idSesije = chatManager.getSessionId(idGnenta);
						if(idSesije.length()!=0)
							ws.onMessage(idSesije, response);
						break;
					case "GET":
						
						String  sessionId = (String) message.userArgs.get("sessionId");
						String  who = (String) message.userArgs.get("sender");
						
						response = "messages:";
						for (models.Message mess : chatMessages.getChatForUser(who)) {
							if(who.equals("ALL"))
								mess.setSender("ALL");
							response +=  mess.toString()+"|";
						}
						ws.onMessage(sessionId, response);
						break;
					case "NEW_REGISTER":
						sessionId = (String) message.userArgs.get("sessionId");
						response = "registeredList:";
						List<User> users2 = chatManager.regeisteredUsers();
						for (User u : users2) {
							response += u.getUsername() + "|";
						}
						idGnenta = getAgentId();
						idSesije = chatManager.getSessionId(idGnenta);
						if(idSesije.length()!=0)
							ws.onMessage(idSesije, response);
//						ws.onMessage(chatManager.getSessionId(getAgentId()), response);
						break;
					case "NEW_LOGIN":
						sessionId = (String) message.userArgs.get("sessionId");
						response = "loggedInList:";
						List<User> users = chatManager.loggedInUsers();
						for (User u : users) {
							response += u.getUsername() + "|";
						}
						idGnenta = getAgentId();
						idSesije = chatManager.getSessionId(idGnenta);
						if(idSesije.length()!=0)
							ws.onMessage(idSesije, response);
//						ws.onMessage(chatManager.getSessionId(getAgentId()), response);
						break;
					case "NEW_LOGOUT":
						sessionId = (String) message.userArgs.get("sessionId");
						response = "loggedInList:";
						List<User> users3 = chatManager.loggedInUsers();
						for (User u : users3) {
							response += u.getUsername() + "|";
						}
						idGnenta = getAgentId();
						idSesije = chatManager.getSessionId(idGnenta);
						if(idSesije.length()!=0)
							ws.onMessage(idSesije, response);
//						ws.onMessage(chatManager.getSessionId(getAgentId()), response);
						break;
					default:
						response = "ERROR!Option: " + option + " does not exist.";
						break;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/


	@Override
	public String init() {
		agentId = generateId();
//		cachedAgents.addRunningAgent(agentId, this);
		return agentId;
	}

	private String generateId() {
		Random r = new Random();
		int low = 10;
		int high = 100;
		return Integer.toString(r.nextInt(high - low) + low);
	}

	@Override
	public String getAgentId() {
		return agentId;
	}
	
	private void sendMessage(Message msg) {
		TextMessage tmsg = (TextMessage) msg;
		try {
			String receiver = (String) tmsg.getObjectProperty("receiver");
			String sender = (String) tmsg.getObjectProperty("sender");
			String content = (String) tmsg.getObjectProperty("content");
			
			chatMessages.sendMessage(new models.Message(sender,receiver,content));
			AgentMessage forwardMessage = new AgentMessage();
			messageManager.post(forwardMessage);
		} catch(Exception e)
		{
			
		}
		
	}

	@Override
	public void init(String agentId) {
		this.agentId = agentId;
	}

	@Override
	public void handleMessage(ACLMessage msg) {
		
		String receiver;
		String sender;
		String message;
		try {
			receiver = (String) msg.userArgs.get("receiver");
			sender = (String) msg.userArgs.get("sender");
			message = (String) msg.userArgs.get("message");
			if (receiver.equals(agentId)) {
				System.out.println("Received by user agent :" + agentId);
				System.out.println("Received from :" + sender);
				
				
				String option = "";
				String response = "";
				try {
					option = (String) msg.userArgs.get("command");
					switch (option) {
					case "RECIVE_MESSAGE":
						System.out.println("Prima poruku useragent: "+ agentId);
						String temp = (String) msg.userArgs.get("content");
						String subjecttemp = (String) msg.userArgs.get("subject");
						String datetemp = (String) msg.userArgs.get("date");
						String realsender = (String) msg.userArgs.get("realsender");
						models.Message newMsg = new models.Message(sender,receiver,temp,datetemp,subjecttemp, realsender);
						chatMessages.receiveMessage(newMsg);
						if(realsender!=null)
							newMsg.setSender("ALL");
						response = "message:"+newMsg.toString();
						String idGnenta = getAgentId();
						String idSesije = chatManager.getSessionId(idGnenta);
						if(idSesije.length()!=0)
							ws.onMessage(idSesije, response);
						break;
					case "GET":
						
						String  sessionId = (String) msg.userArgs.get("sessionId");
						String  who = (String) msg.userArgs.get("sender");
						
						response = "messages:";
						for (models.Message mess : chatMessages.getChatForUser(who)) {
							if(who.equals("ALL"))
								mess.setSender("ALL");
							response +=  mess.toString()+"|";
						}
						ws.onMessage(sessionId, response);
						break;
					case "NEW_REGISTER":
						sessionId = (String) msg.userArgs.get("sessionId");
						response = "registeredList:";
						List<User> users2 = chatManager.regeisteredUsers();
						for (User u : users2) {
							response += u.getUsername() + "|";
						}
						idGnenta = getAgentId();
						idSesije = chatManager.getSessionId(idGnenta);
						if(idSesije.length()!=0)
							ws.onMessage(idSesije, response);
//						ws.onMessage(chatManager.getSessionId(getAgentId()), response);
						break;
					case "NEW_LOGIN":
						sessionId = (String) msg.userArgs.get("sessionId");
						response = "loggedInList:";
						List<User> users = chatManager.loggedInUsers();
						for (User u : users) {
							response += u.getUsername() + "|";
						}
						idGnenta = getAgentId();
						idSesije = chatManager.getSessionId(idGnenta);
						if(idSesije.length()!=0)
							ws.onMessage(idSesije, response);
//						ws.onMessage(chatManager.getSessionId(getAgentId()), response);
						break;
					case "NEW_LOGOUT":
						sessionId = (String) msg.userArgs.get("sessionId");
						response = "loggedInList:";
						List<User> users3 = chatManager.loggedInUsers();
						for (User u : users3) {
							response += u.getUsername() + "|";
						}
						idGnenta = getAgentId();
						idSesije = chatManager.getSessionId(idGnenta);
						if(idSesije.length()!=0)
							ws.onMessage(idSesije, response);
//						ws.onMessage(chatManager.getSessionId(getAgentId()), response);
						break;
					default:
						response = "ERROR!Option: " + option + " does not exist.";
						break;
					}
					
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
	public void handleMessage(AgentMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return aId.getName();
	}
}

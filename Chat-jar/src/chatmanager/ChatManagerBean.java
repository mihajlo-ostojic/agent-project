package chatmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Stateful;

import agentmanager.AgentManagerRemote;
import models.Host;
import models.Message;
import models.User;
import nodemanager.NodeManagerRemote;
import util.JNDILookup;

// TODO Implement the rest of Client-Server functionalities 
/**
 * Session Bean implementation class ChatBean
 */
@Singleton
@LocalBean
public class ChatManagerBean implements ChatManagerRemote, ChatManagerLocal {

	private List<User> registered = new ArrayList<User>();
	private List<User> loggedIn = new ArrayList<User>();
	
	
	@EJB
	private AgentManagerRemote agentManager;
	
	@EJB
	private NodeManagerRemote nodeManager;
	/**
	 * Default constructor.
	 */
	public ChatManagerBean() {

	}
	
	public String getSessionId(String username)
	{
		for (User u : loggedIn) {
			if(u.getUsername()==username) return u.getId();
		}
		return "";
	}
	

	@Override
	public boolean register(User user) {
		for (User u : registered) {
			if(u.getUsername().equals(user.getUsername()))
			{
				return false;
			}
		}
		user.setHost(new Host("",nodeManager.getCurrentNode().getAlias(),nodeManager.getCurrentNode().getAddress()));
		registered.add(user);
		return true;
	}

	@Override
	public boolean login(String username, String password, String id) {
		boolean exists = registered.stream().anyMatch(u->u.getUsername().equals(username) && u.getPassword().equals(password));
		if(exists) {
			
			for (User u : loggedIn) {
				if(u.getUsername()==username) return false;
			}
			User newU = new User(username, password, id);
			newU.setHost(new Host("",nodeManager.getCurrentNode().getAlias(),nodeManager.getCurrentNode().getAddress()));
			loggedIn.add(newU);
			agentManager.startAgent(username,JNDILookup.UserAgentLookup);
			
			return true;
		}
		return exists;
	}

	@Override
	public List<User> loggedInUsers() {
		return loggedIn;
	}
	
	@Override
	public List<User> regeisteredUsers() {
		return registered;
	}
	
	@Override
	public boolean logout(String username, String password) {
		System.err.println("Username za logout: "+ username);
		
		User foundUser = null;
		for (User user : loggedIn) {
			if(user.getUsername().equals(username))
			{
				foundUser = user;
			}
		}
		if(foundUser!=null) {
			loggedIn.remove(foundUser);
			System.out.println("User romeved from logedIn");
			return true;
		}
		return false;
	
	}

	@Override
	public void logOutUsersFromNode(String alias) {
		
		ArrayList<User> usersToRemove = new ArrayList<User>();
		User foundUser = null;
		for (User user : loggedIn) {
			if(user.getHost().getAlias().equals(alias))
			{
				usersToRemove.add(new User(user.getUsername(), user.getPassword()));
			}
		}
		
		for(User user:usersToRemove)
		{
			System.out.println("Loging out user from host: " + alias);
			logout(user.getUsername(), user.getPassword());
		}
		
	}

	@Override
	public void registerFromNode(User user) {
		registered.add(user);
		
	}

	@Override
	public void loginFromNode(User user) {
		loggedIn.add(user);
		
	}

	@Override
	public void logoutFromNode(String username) {
		System.err.println("Username za logout from other node: "+ username);
		
		User foundUser = null;
		for (User user : loggedIn) {
			if(user.getUsername().equals(username))
			{
				foundUser = user;
			}
		}
		if(foundUser!=null) {
			loggedIn.remove(foundUser);
			System.out.println("User romeved from logedIn");

		}
		
	}

	@Override
	public void getLogUsersFromMaster(String info,String nodeAlias, String nodeAddress) {
		String[] allUsers = info.split("|");
		//preuzeti node podatke
		for(String userString: allUsers)
		{
			String[] data = userString.split(",");
			User temp = new User(data[0],data[1]);
			temp.setHost(new Host("",nodeAlias,nodeAddress));
			loggedIn.add(temp);
		}
		
	}

	@Override
	public void getRegUsersFromMaster(String info,String nodeAlias, String nodeAddress) {
		String[] allUsers = info.split("|");
		//preuzeti node podatke
		for(String userString: allUsers)
		{
			String[] data = userString.split(",");
			User temp = new User(data[0],data[1]);
			temp.setHost(new Host("",nodeAlias,nodeAddress));
			registered.add(temp);
		}
		
	}

}
